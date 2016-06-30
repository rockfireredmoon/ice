package org.icenet;

import java.io.Closeable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class Simulator implements Closeable {

	private SimulatorMessageDecoder decoder;
	private Thread pingThread;
	private boolean stopPing;
	private final String hostname;
	private final int port;

	public enum ProtocolState {
		LOBBY, GAME
	}

	private static final Logger LOG = Logger.getLogger(Simulator.class.getName());
	private Channel channel;
	private NioEventLoopGroup workerGroup;
	private SimulatorClientHandler handler;
	private final Map<SimulatorMessage, SimulatorMessage> awaitingReply = new ConcurrentHashMap<>();
	private final Object replyLock = new Object();
	// private HeloMessage helo;
	private HeloReplyMessage hello;
	private final List<MessageListener> messageListeners = new ArrayList<>();
	private final Map<ReplyCallbackWrapper, Semaphore> replyLocks = new ConcurrentHashMap<>();
	private ProtocolState protocol = ProtocolState.LOBBY;
	private static int simpingSeq;
	private Exception disconnectException;
	private boolean disconnected;

	private static SimulatorMessage NO_REPLY = new SimulatorMessage((byte) 0);

	public enum ReplyAction {

		RETURN, SKIP, HANDLED
	}

	public interface ReplyCallback {

		ReplyAction onReply(SimulatorMessage mesg) throws NetworkException;
	}

	private abstract class ReplyCallbackWrapper {

		private ReplyCallback callback;

		ReplyCallbackWrapper(ReplyCallback callback) {
			this.callback = callback;
		}

		abstract void onError(Exception e);
	}

	public interface MessageListener {

		void handlingError(SimulatorMessage mesg, Exception e);

		boolean receivedMessage(SimulatorMessage mesg);

		void simulatorDisconnect(Exception e);
	}

	public Simulator(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}

	public ProtocolState getMode() {
		return protocol;
	}

	public void awaitTermination() throws InterruptedException {
		channel.closeFuture().sync();
	}

	public void addListener(MessageListener listener) {
		messageListeners.add(listener);
	}

	public void removeListener(MessageListener listener) {
		messageListeners.add(listener);
	}

	public void setClientLoading(boolean clientLoading) throws NetworkException {
		sendMessage(new GameQueryMessageWithReply("client.loading", String.valueOf(clientLoading)) {
			@Override
			public boolean isWaitForReply() {
				return false;
			}

			@Override
			protected boolean onReply(SimulatorMessage reply) {
				LOG.info("Ack of client loading");
				return true;
			}
		});
	}

	@Override
	public void close() {
		if (isSimulatorPingActive()) {
			stopSimulatorPing();
		}
		try {
			LOG.info("Awaiting close");
			closeFuture().await();
			LOG.info("Simulator connection closed");
		} catch (InterruptedException ex) {
			LOG.warning("Interrupted waiting for close to complete.");
		}
	}

	public ChannelFuture closeFuture() {
		ChannelFuture future = channel.close();
		workerGroup.shutdownGracefully();
		return future;
	}

	public void startSimulatorPing() {
		if (pingThread != null) {
			throw new IllegalStateException();
		}
		LOG.info("Starting simulator ping");
		stopPing = false;
		final long simpingStart = System.currentTimeMillis();
		pingThread = new Thread("SimulatorPing") {
			@Override
			public void run() {
				try {
					while (!stopPing) {
						Thread.sleep(5000);
						long simpingTime = (System.currentTimeMillis() - simpingStart) * 10;
						GameQueryMessage simping = new GameQueryMessage("util.pingsim", String.valueOf(simpingSeq),
								String.valueOf(simpingTime));
						simpingSeq++;
						sendAndAwaitReplies(simping, new ReplyCallback() {
							@Override
							public ReplyAction onReply(SimulatorMessage mesg) {
								if (mesg instanceof QueryReplyMessage) {
									return ReplyAction.RETURN;
								}
								return ReplyAction.SKIP;
							}
						});
					}
				} catch (InterruptedException ie) {
				} catch (NetworkException ex) {
					LOG.log(Level.SEVERE, "Failed to send simulator ping. Will probably get disconnected.", ex);
				}
			}
		};
		pingThread.start();
	}

	public void stopSimulatorPing() {
		if (pingThread == null) {
			throw new IllegalStateException();
		}
		stopPing = true;
		try {
			pingThread.interrupt();
			pingThread.join();
		} catch (InterruptedException ex) {
		} finally {
			pingThread = null;
		}
	}

	public boolean isSimulatorPingActive() {
		return pingThread != null;
	}

	public void sendAndAwaitReplies(SimulatorMessage mesg, ReplyCallback callback) throws NetworkException {
		Semaphore lock = new Semaphore(1);
		final List<Exception> errors = new ArrayList<>();
		try {
			try {
				lock.acquire();
				replyLocks.put(new ReplyCallbackWrapper(callback) {
					@Override
					void onError(Exception e) {
						errors.add(e);
					}
				}, lock);

				//
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("Sending message: %s", mesg));
				}
				mesg.setSendTime(System.currentTimeMillis());
				if (mesg.getPayload() != null) {
					decoder.ctx.channel().writeAndFlush(mesg);
				}

				// Wait for reply
				lock.acquire();
			} finally {
				lock.release();
			}
		} catch (InterruptedException ie) {
			throw new NetworkException(NetworkException.ErrorType.INTERRUPTED_IO, ie);
		}
		if (!errors.isEmpty()) {
			Exception e = errors.get(0);
			if (e instanceof NetworkException) {
				throw (NetworkException) e;
			}
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR, errors.get(0));
		}
	}

	public SimulatorMessage sendMessage(SimulatorMessage mesg) throws NetworkException {
		try {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Sending message: %s", mesg));
			}
			prepareForReply(mesg);
			if (mesg.getPayload() != null) {
				decoder.ctx.channel().writeAndFlush(mesg);
			}
			return handleReply(mesg);
		} catch (InterruptedException ie) {
			throw new NetworkException(NetworkException.ErrorType.INTERRUPTED_IO, ie);
		}

	}

	private void prepareForReply(SimulatorMessage mesg) {
		mesg.setSendTime(System.currentTimeMillis());
		if (mesg.isWantsReply()) {
			awaitingReply.put(mesg, NO_REPLY);
		}
	}

	private SimulatorMessage handleReply(SimulatorMessage mesg) throws InterruptedException {
		// If a reply is expected
		try {
			if (mesg.isWantsReply() && mesg.isWaitForReply()) {
				long timeout = mesg.getReplyTimeout();
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("Waiting %d for reply to %s", timeout, mesg));
				}
				if (!disconnected) {
					synchronized (replyLock) {
						while (awaitingReply.get(mesg) == NO_REPLY && timeout > 0) {
							long started = System.currentTimeMillis();
							replyLock.wait(timeout);

							// If we are still awaiting reply it must be some
							// other
							// message that got replied to,
							// so loop again, reducing timeout
							if (awaitingReply.get(mesg) == null) {
								timeout -= (System.currentTimeMillis() - started);
								if (LOG.isLoggable(Level.FINE)) {
									LOG.fine(String.format("Reduced timeout of '%s' to %d", mesg, timeout));
								}
							} else {
								if (LOG.isLoggable(Level.FINE)) {
									LOG.fine(String.format("Notified of reply to %s", mesg));
								}
							}
						}
					}

					if (timeout > 0) {
						// This was our reply, and in time
						final SimulatorMessage reply = awaitingReply.get(mesg);
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Returning reply to %s", reply));
						}
						return reply;
					}
				}

				// Timed-out
				if (disconnected) {
					throw new NetworkException(NetworkException.ErrorType.SERVER_DISCONNECTED, "Server disconnected.");
				} else {
					throw new NetworkException(NetworkException.ErrorType.TIMEOUT, "Timeout wait for reply");
				}
			}
		} finally {
			// Regardless of success or failure, we are no longer waiting for
			// this
			if (mesg.isWaitForReply()) {
				awaitingReply.remove(mesg);
			}
		}

		// No reply
		return null;
	}

	public SimulatorMessage nextMessage() {
		return handler.getMessage();
	}

	public String getAuthData() {
		return hello.getAuthData();
	}

	public HeloReplyMessage.AuthenticationMode getAuthMode() {
		return hello.getAuthenticationMode();
	}

	public long getProtocolVersion() {
		return hello.getProtocolVersion();
	}

	public void setMode(ProtocolState mode) {
		LOG.info(String.format("Mode is now %s", mode));
		this.protocol = mode;
	}

	public void connectToSimulator() throws NetworkException {
		workerGroup = new NioEventLoopGroup();
		protocol = ProtocolState.LOBBY;
		disconnected = false;
		try {

			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new SimulatorClientInitializer());

			// Start the client.
			LOG.info(String.format("Connecting to simulator at %s:%d", hostname, port));
			final ChannelFuture cx = b.connect(hostname, port);
			ChannelFuture f = cx.sync(); // (5)
			channel = f.awaitUninterruptibly().channel();

			disconnectException = null;
			channel.closeFuture().addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture f) throws Exception {
					LOG.info("Channel closed.");
					synchronized (replyLock) {
						disconnected = true;
						awaitingReply.clear();
						replyLock.notifyAll();
					}
					for (int i = messageListeners.size() - 1; i >= 0; i--) {
						messageListeners.get(i).simulatorDisconnect(disconnectException);
					}
				}
			});

			// Get the handler instance to retrieve the answer.
			decoder = (SimulatorMessageDecoder) f.channel().pipeline().first();
			handler = (SimulatorClientHandler) f.channel().pipeline().last();

			// Wait for helo message
			ConnectMessage connect = new ConnectMessage();
			hello = (HeloReplyMessage) sendMessage(connect);
			return;

		} catch (InterruptedException ie) {
			close();
			throw new NetworkException(NetworkException.ErrorType.INTERRUPTED_IO, "Interrupted I/O.", ie);
		}
		// close();
		// throw new NetworkException(NetworkException.ErrorType.TIMEOUT,
		// "Timed out waiting for initial reply.");
	}

	public boolean isConnected() {
		return channel != null && channel.isActive();
	}

	class SimulatorMessageDecoder extends ByteToMessageDecoder { // (1)

		private int toRead = -1;
		private byte op;
		private ChannelHandlerContext ctx;
		private ProtocolState decodingProtocol = ProtocolState.LOBBY;

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			this.ctx = ctx;
			LOG.info("Channel is now open");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			LOG.info("Channel inactive.");
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			LOG.info("Channel unregistered.");
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			LOG.log(Level.SEVERE, "Exception caught in router channel.", cause);
			ctx.close();
		}

		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // (2)
			if (toRead == -1) {
				if (in.readableBytes() < 3) {
					return; // (3)
				}

				op = in.readByte();
				toRead = in.readShort();
				return;
			} else if (in.readableBytes() < toRead) {
				return;
			}

			// Read the message payload and create a basic message object
			SimulatorMessage routerMessage = new SimulatorMessage(op, toRead);
			in.readBytes(routerMessage.getPayload());

			// Decode actual message if recognised
			try {
				routerMessage = mutateMessage(routerMessage);
				LOG.info("ROUTER MESSAGE: " + routerMessage);
				if (routerMessage.getPayload().remaining() > 0) {
					LOG.warning(String.format("Bytes remaining after processing message! %d of %d remain in %s.",
							routerMessage.getPayload().remaining(), toRead, routerMessage.getClass()));
				}
			} catch (ParseException pe) {
				throw new RuntimeException("Failed to decode message.", pe);
			}

			out.add(routerMessage);
			toRead = -1;
		}

		private SimulatorMessage mutateMessage(SimulatorMessage routerMessage) throws ParseException {
			switch (op) {
			case SimulatorMessage.MSG_QUERY_REPLY:
				return new QueryReplyMessage(routerMessage);
			case SimulatorMessage.MSG_PING:
				return new PingFromServerMessage(routerMessage);
			}
			switch (decodingProtocol) {
			case GAME:
				switch (op) {
				case SimulatorMessage.MSG_SET_MAP:
					return new SetMapMessage(routerMessage);
				case SimulatorMessage.MSG_SPAWN_UPDATE:
					return new SpawnUpdateMessage(Simulator.this, routerMessage);
				case SimulatorMessage.MSG_CHAT_IN:
					return new ChatIncomingMessage(routerMessage);
				case SimulatorMessage.MSG_SYSTEM:
					return new SystemMessage(routerMessage);
				case SimulatorMessage.MSG_CREATURE_EVENT:
					return new CreatureEventReplyMessage(Simulator.this, routerMessage);
				case SimulatorMessage.MSG_JUMP:
					return new JumpMessage(routerMessage);
				case SimulatorMessage.MSG_STATUS_UPDATE:
					return new StatusUpdateMessage(routerMessage);
				case SimulatorMessage.MSG_ABILITY_QUERY_REPLY:
					return new ItemQueryReplyMessage(decodingProtocol, Simulator.this, routerMessage);
				case SimulatorMessage.MSG_INVENTORY_QUERY_REPLY:
					return new InventoryQueryReplyMessage(routerMessage);
				case SimulatorMessage.MSG_SCENERY_QUERY_REPLY:
					return new SceneryQueryReplyMessage(Simulator.this, routerMessage);
				case SimulatorMessage.MSG_PONG:
					throw new UnsupportedOperationException("BLA!");
					// return new ItemQueryReplyMessage(routerMessage);
				case SimulatorMessage.MSG_MOD:
					return new ModReplyMessage(routerMessage);
				}
				break;
			case LOBBY:
				switch (op) {
				case SimulatorMessage.MSG_HELO:
					return new HeloReplyMessage(routerMessage);
				case SimulatorMessage.MSG_LOBBY_ERROR:
					return new LobbyErrorMessage(routerMessage);
				case SimulatorMessage.MSG_LOGIN_OK:
					return new LoginOkMessage(routerMessage);
				case SimulatorMessage.MSG_LOBBY_ITEM_REPLY:
					return new ItemQueryReplyMessage(decodingProtocol, Simulator.this, routerMessage);
				case SimulatorMessage.MSG_MOD:
					return new ModReplyMessage(routerMessage);
				case SimulatorMessage.MSG_PROTOCOL_CHANGE:
					ProtocolChangeReply protocolChangeReply = new ProtocolChangeReply(routerMessage);

					/*
					 * The decoder tracks its own current protocol, as the
					 * ProtocolChangeReply might not get decoded for a while
					 */
					decodingProtocol = protocolChangeReply.getNewProtocol();
					return protocolChangeReply;
				}
				break;
			}
			return routerMessage;

		}
	}

	class SimulatorMessageEncoder extends MessageToByteEncoder<SimulatorMessage> {

		@Override
		protected void encode(ChannelHandlerContext ctx, SimulatorMessage msg, ByteBuf out) throws Exception {
			out.writeByte(msg.getCode());
			out.writeShort(msg.getLen());
			out.writeBytes(msg.getPayload());
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			SimulatorMessage m = (SimulatorMessage) msg;
			ByteBuf encoded = ctx.alloc().buffer(3 + m.getLen());
			encoded.writeByte(m.getCode());
			encoded.writeShort(m.getLen());
			if (m.getLen() > 0) {
				encoded.writeBytes(m.getPayload());
			}
			ctx.write(encoded, promise); // (1)
		}
	}

	class SimulatorClientInitializer extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast("decoder", new SimulatorMessageDecoder());
			pipeline.addLast("encoder", new SimulatorMessageEncoder());
			pipeline.addLast("handler", new SimulatorClientHandler());
		}
	}

	class SimulatorClientHandler extends SimpleChannelInboundHandler<SimulatorMessage> {

		private BlockingQueue<SimulatorMessage> messages = new LinkedBlockingQueue<SimulatorMessage>();

		@Override
		public void channelRead0(ChannelHandlerContext ctx, SimulatorMessage msg) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Received: %s", msg));
			}

			// First see if this message is an expected reply
			synchronized (replyLock) {
				// If no helo yet, expect that first
				// if (helo == null) {
				// if (msg.getCode() == SimulatorMessage.MSG_HELO) {
				// helo = (HeloReplyMessage) msg;
				// replyLock.notifyAll();
				// return;
				// }
				// }
				for (SimulatorMessage m : awaitingReply.keySet()) {
					if (m.isReply(msg)) {
						if (m.onReply(msg)) {
							awaitingReply.remove(m);
						} else {

							// This is our reply
							if (LOG.isLoggable(Level.FINE)) {
								LOG.fine(String.format("Found reply for '%s' in '%s'", m, msg));
							}
							awaitingReply.put(m, msg);
							replyLock.notifyAll();
						}
						return;
					}
				}
			}

			// // See if there are any waiting ReplyCallbacks for this messages
			for (Iterator<Map.Entry<ReplyCallbackWrapper, Semaphore>> replyEnIt = replyLocks.entrySet().iterator(); replyEnIt
					.hasNext();) {
				Map.Entry<ReplyCallbackWrapper, Semaphore> replyEn = replyEnIt.next();

				try {
					ReplyAction replyAction = replyEn.getKey().callback.onReply(msg);
					if (replyAction.equals(ReplyAction.HANDLED)) {
						return;
					} else if (replyAction.equals(ReplyAction.RETURN)) {
						// Handled
						replyEn.getValue().release();
						replyEnIt.remove();
						return;
					}
				} catch (Exception e) {
					// Release lock
					replyEn.getKey().onError(e);
					replyEn.getValue().release();
					replyEnIt.remove();
					return;
				}
			}

			// Wasn't a reply to anything, try the listeners
			try {
				for (MessageListener l : messageListeners) {
					if (l.receivedMessage(msg)) {
						return;
					}
				}
			} catch (Exception e) {

				LOG.log(Level.SEVERE, "Error handling reply message.", e);

				// Wasn't a reply to anything, try the listeners
				for (MessageListener l : messageListeners) {
					l.handlingError(msg, e);
				}
				return;
			}

			LOG.log(Level.SEVERE, String.format("Unhandled message. %s", msg));
		}

		private SimulatorMessage getMessage() {
			boolean interrupted = false;
			for (;;) {
				try {
					SimulatorMessage factorial = messages.take();
					if (interrupted) {
						Thread.currentThread().interrupt();
					}
					return factorial;
				} catch (InterruptedException e) {
					interrupted = true;
				}
			}
		}
	}
}
