package org.icenet.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.UnresolvedAddressException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.icelib.Appearance;
import org.icelib.Armed;
import org.icelib.ChannelType;
import org.icelib.Icelib;
import org.icelib.PageLocation;
import org.icelib.Persona;
import org.icelib.Point3D;
import org.icelib.Point4D;
import org.icelib.Profession;
import org.icelib.RGB;
import org.icelib.SceneryItem;
import org.icelib.Slot;
import org.icelib.Zone;
import org.icenet.ChatIncomingMessage;
import org.icenet.ChatOutgoingMessage;
import org.icenet.CreatureEventReplyMessage;
import org.icenet.GameItem;
import org.icenet.GameQueryMessageWithReply;
import org.icenet.HengeListMessage;
import org.icenet.InventoryQueryReplyMessage;
import org.icenet.ItemQueryMessage;
import org.icenet.ItemQueryReplyMessage;
import org.icenet.LobbyErrorMessage;
import org.icenet.LobbyItemQueryMessage;
import org.icenet.LobbyQueryMessage;
import org.icenet.LobbyQueryMessageWithReply;
import org.icenet.LoginMessage;
import org.icenet.ModReplyMessage;
import org.icenet.ModReplyMessage.Op;
import org.icenet.MovementMessage;
import org.icenet.NetworkException;
import org.icenet.NetworkException.ErrorType;
import org.icenet.PingFromServerMessage;
import org.icenet.PlayerJumpMessage;
import org.icenet.PongMessage;
import org.icenet.ProtocolChangeReply;
import org.icenet.QueryReplyMessage;
import org.icenet.QueryReplyMessage.Reply;
import org.icenet.RequestSpawnUpdateMessage;
import org.icenet.Router;
import org.icenet.SceneryQueryReplyMessage;
import org.icenet.SelectPersonaMessage;
import org.icenet.SetMapMessage;
import org.icenet.Simulator;
import org.icenet.Simulator.ProtocolState;
import org.icenet.SimulatorMessage;
import org.icenet.SpawnUpdateMessage;
import org.icenet.SpawnUpdateMessage.Elevation;
import org.icenet.SpawnUpdateMessage.Mods;
import org.icenet.SpawnUpdateMessage.Position;
import org.icenet.SpawnUpdateMessage.SpawnUpdate;
import org.icenet.SpawnUpdateMessage.Velocity;
import org.icenet.SpawnUpdateMessage.ZoneUpdate;
import org.icenet.StatusUpdateMessage;
import org.icenet.SystemMessage;
import org.icenet.TokenLoginMessage;
import org.icesquirrel.interpreter.SquirrelInterpretedTable;
import org.icesquirrel.runtime.SquirrelException;
import org.icesquirrel.runtime.SquirrelTable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * High level network client for the Planet Forever protocol. Users of this
 * class do not generally deal in low level messages, instead use the various
 * listeners and methods to monitor game events and perform operations.
 */
public class Client implements Simulator.MessageListener {

	public final static String HTTP_USER_AGENT = "Icenet";

	private static final Logger LOG = Logger.getLogger(Client.class.getName());
	private Simulator simulator;
	private final URI serverUrl;
	private List<Persona> personas = new ArrayList<>();
	private Map<Long, Spawn> spawns = new HashMap();
	private Map<String, Spawn> spawnsByName = new HashMap();
	private List<Spawn> readySpawns = new ArrayList<>();
	private long playerSpawnId = -1;
	private List<ClientListener> listeners = new ArrayList();
	private List<String> ignored;
	private Zone zone;
	private String terrainPath;
	private boolean loading;
	private ScheduledExecutorService movementExecutor;
	private Point3D location;
	private int rotDeg;
	private ScheduledFuture<?> movementFuture;
	private short speed;
	private int moveDeg;
	private Point3D lastSentLoc;
	private int lastSentDeg;
	private int lastSentMoveDeg;
	private short lastSentSpeed;
	private Spawn playerSpawn;
	private long lastGoodSpawnId;
	private Router router;
	private Object moveLock = new Object();
	private String authToken;
	private String mXCSRFToken;

	public Client(URI serverUrl) {
		this.serverUrl = serverUrl;
		zone = new Zone();
	}

	public Zone getZone() {
		return zone;
	}

	public void sendMessage(ChannelType currentChannel, String character, String text) throws NetworkException {
		String param = null;
		if (currentChannel.equals(ChannelType.TELL)) {
			param = character;
			fireChatMessage(null, character, currentChannel, text);
		} else if (currentChannel.equals(ChannelType.GM)) {
			// TODO is this hardcoded?
			param = "earthsages";
		}
		simulator.sendMessage(new ChatOutgoingMessage(currentChannel.format(), param, text));
	}

	/**
	 * Send a position update to the server. This method limits the rate at
	 * which updates are sent. Updates will be sent immediately if no other
	 * update has been sent for 250ms, otherwise they will be sent every 250ms
	 * until no more are sent. If the position has not changed, no update will
	 * be sent at all.
	 * 
	 * @param location
	 * @param rotDeg
	 *            rotation in degress
	 * @param moveDeg
	 *            movement direction
	 * @param type
	 *            movement type
	 */
	public void move(final Point3D location, final int rotDeg, final int moveDeg, short speed, boolean force) {
		synchronized (moveLock) {
			this.location = location.clone();
			this.moveDeg = moveDeg;
			this.rotDeg = rotDeg;
			this.speed = speed;
		}
		if (movementFuture == null || force) {
			doMove(force);
		} else {
			Icelib.removeMe("Already waiting to sending movement %d %d %d", moveDeg, rotDeg, speed);
		}
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void addListener(ClientListener l) {
		listeners.add(l);
	}

	public void removeListener(ClientListener l) {
		listeners.add(l);
	}

	public boolean isConnected() {
		return simulator != null && simulator.isConnected();
	}

	/**
	 * Connect to the server. Interally a connection will be first be made to
	 * the router, then a connection to the simulator pointed to by the routers
	 * response is connected, and all further communication happens over this
	 * simlulator connection.
	 * 
	 */
	public void connect() {
		if (simulator != null && simulator.isConnected()) {
			throw new IllegalStateException("Already connected.");
		}
		movementExecutor = Executors.newScheduledThreadPool(1);
		router = new Router(serverUrl);
		router.connectToRouter();
		doConnectorSimulator(router.getSimulatorHost(), router.getSimulatorPort());
	}

	/**
	 * Connect directly to the simulator without gettings its address from the
	 * router.
	 * 
	 * @param simulatorHost
	 * @param simulatorPort
	 */
	public void connect(String simulatorHost, int simulatorPort) {
		if (simulator != null && simulator.isConnected()) {
			throw new IllegalStateException("Already connected.");
		}
		doConnectorSimulator(simulatorHost, simulatorPort);
		movementExecutor = Executors.newScheduledThreadPool(1);
	}

	/**
	 * Login. If login fails, an exception will be thrown. Note, the connection
	 * will also be dropped by the server.
	 * 
	 * @param username
	 *            user name
	 * @param password
	 *            password
	 * @throws NetworkException
	 */
	public void login(String username, char[] password) throws NetworkException {
		checkConnected();
		switch (simulator.getAuthMode()) {
		case DEV:
			SimulatorMessage msg = simulator.sendMessage(new LoginMessage(username, password, simulator.getAuthData()));
			if (msg instanceof LobbyErrorMessage) {
				throw new NetworkException(NetworkException.ErrorType.INCORRECT_USERNAME_OR_PASSWORD,
						((LobbyErrorMessage) msg).getMessage());
			}
			break;
		case SERVICE:

			try {
				if (StringUtils.isNotBlank(authToken)) {

					String[] spl = authToken.split(":");
					String uid = spl[2];
					String sessionName = spl[0];
					String sessionId = spl[1];
					URL authUrl = new URL(simulator.getAuthData());
					URL replyUrl = new URL(authUrl.getProtocol(), authUrl.getHost(), authUrl.getPort(),
							"/user/" + uid + ".json");
					HttpURLConnection connection = (HttpURLConnection) replyUrl.openConnection();
					connection.setDoInput(true);
					connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
					connection.setRequestProperty("Cookie", sessionName + "=" + URLEncoder.encode(sessionId, "UTF-8"));
					connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					if (connection.getResponseCode() == 200) {
						handleServiceTokenLogin(connection.getInputStream());
					} else {
						throw new NetworkException(ErrorType.GENERAL_NETWORK_ERROR,
								String.format("Service responded with error code %d", connection.getResponseCode(),
										connection.getResponseMessage()));
					}
				} else {
					mXCSRFToken = null;
					URL authUrl = new URL(simulator.getAuthData());
					LOG.info(String.format("Auth data is %s", simulator.getAuthData()));
					URL replyUrl = new URL(authUrl, "rest/user/token.json");
					LOG.info(String.format("Auth URL is %s", replyUrl));
					HttpURLConnection connection = (HttpURLConnection) replyUrl.openConnection();
					connection.setDoOutput(true);
					connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					connection.getOutputStream().write("{}".getBytes());
					connection.getOutputStream().flush();
					if (connection.getResponseCode() == 200) {
						handleServiceToken(connection.getInputStream(), username, password);
					} else {
						throw new NetworkException(ErrorType.GENERAL_NETWORK_ERROR,
								String.format("Service responded with error code %d (%s)", connection.getResponseCode(),
										connection.getResponseMessage()));
					}
				}
			} catch (IOException ioe) {
				throw new NetworkException(ErrorType.AUTHENTICATION_SERICE_ERROR, ioe);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * Select a character for play. See {@link #getPersonas()} to get the list
	 * of available charcters.
	 * 
	 * @param character
	 *            character to play
	 * @return player spawn object
	 * @throws NetworkException
	 */
	public Spawn select(final Persona character) throws NetworkException {

		// void SimulatorThread :: SetPersona(int personaIndex)
		checkConnected();
		if (playerSpawn != null) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR, "Already selected.");
		}
		short personaIndex = (short) personas.indexOf(character);
		LOG.info(String.format("Selecting %s for play (character %d)", character.getDisplayName(), personaIndex));

		/**
		 * A number of different replies are sent on selection of personal
		 * 
		 * 1. ProtocolChangeReply - the game goes into play mode
		 * 
		 * 2. SpawnUpdateMessage(Zone+Position) - the players initial
		 * zone/position is sent
		 * 
		 * 3. SpawnUpdateMessage(Elevation+Position) - the players initial
		 * zone/position is sent
		 * 
		 * 4. ModMessage - stop swimming (optional)
		 * 
		 * 5. CreatureEventReply(Set Avatar) - link the creature ID to the
		 * active avatar
		 * 
		 * 6. EnvironmentUpdate(Set Map) - map is set
		 * 
		 * 6a. System Message
		 * 
		 * 7. SpawnUpdateMessage(Stat) - Appearance is sent
		 * 
		 * 8. SpawnUpdateMessage(Mods+Stats) - All mods and stats is sent
		 */

		// Wait for some more initial messages

		simulator.sendAndAwaitReplies(new SelectPersonaMessage(simulator, personaIndex), new Simulator.ReplyCallback() {
			@Override
			public Simulator.ReplyAction onReply(SimulatorMessage mesg) throws NetworkException {
				Icelib.removeMe("Message during select: %s", mesg);
				if (mesg instanceof ProtocolChangeReply) {
					// 1
					LOG.info("Select persona reply");
					// Now in game mode
					simulator.setMode(((ProtocolChangeReply) mesg).getNewProtocol());

					// Signal load screen as early as possible
					// setClientLoading(true);
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof SpawnUpdateMessage) {
					// 2,3,7,8
					SpawnUpdateMessage sum = (SpawnUpdateMessage) mesg;
					long spawnId = sum.getId();
					handleSpawnUpdate(spawnId == 0 ? playerSpawnId : spawnId, mesg);
					if (sum.has(Mods.class) && spawnId == playerSpawnId) {
						LOG.info("Have mods, persona selection complete.");
						return Simulator.ReplyAction.RETURN;
					}
					// if (sum.getId() != 0 && sum.getId() != playerSpawnId) {
					// LOG.warning("Got spawn update (" + sum.getId() +
					// ") for something other than the player spawn (" +
					// playerSpawnId +
					// " during select(). This was not expect and will screw
					// things up.");
					// return Simulator.ReplyAction.SKIP;
					// } else {
					// handleSpawnUpdate(playerSpawnId, mesg);
					// if (sum.getId() == playerSpawnId && sum.has(Stats.class))
					// {
					// // We now have enough to continue
					// return Simulator.ReplyAction.RETURN;
					// }
					// return Simulator.ReplyAction.HANDLED;
					// }
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof ModReplyMessage) {
					// 4 - A PF specific message instructing to stop swimming
					LOG.info("Client mod message received during inital personal selection. " + mesg);
					ModReplyMessage mrm = (ModReplyMessage) mesg;
					if (mrm.getOp().equals(Op.POPUP)) {
						throw new NetworkException(ErrorType.SERVER_DISCONNECTED, mrm.getMessage());
					}
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof CreatureEventReplyMessage) {
					// TODO I wonder if it's possible to get one of these not
					// for our own spawn
					// if it occurs during our own login.
					CreatureEventReplyMessage pllm = (CreatureEventReplyMessage) mesg;
					LOG.info("Creature event " + pllm.toString());
					if (pllm.getType() == CreatureEventReplyMessage.SUBMSG_AVATAR_CHANGED) {
						if (playerSpawnId == -1) {
							// 5
							playerSpawnId = pllm.getSpawnId();
							LOG.info(String.format("Player spawn ID is %d", playerSpawnId));
							playerSpawn = createSpawn(playerSpawnId);
							spawns.put(playerSpawnId, playerSpawn);
							playerSpawn.setPersona(character);
							return Simulator.ReplyAction.HANDLED;
						} else if (pllm.getSpawnId() == playerSpawnId) {
							LOG.info(String.format("Another SET_AVATAR for our spawn %d", playerSpawnId));
							return Simulator.ReplyAction.HANDLED;
						}
					}
				} else if (mesg instanceof SetMapMessage) {
					// 6
					SetMapMessage setMap = (SetMapMessage) mesg;
					configureZoneFromSetMapMessage(zone, setMap);
					// TODO more needs doing here
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof SystemMessage) {
					// 6a
					if (zone.getShardName() == null) {
						zone.setShardName(((SystemMessage) mesg).getMessage());
						LOG.info(String.format("Shard name is now %s", zone.getShardName()));
					} else if (zone.getWarpName() == null) {
						zone.setWarpName(((SystemMessage) mesg).getMessage());
						LOG.info(String.format("Warpname is now %s", zone.getWarpName()));
					}
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof LobbyErrorMessage) {
					throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
							((LobbyErrorMessage) mesg).getMessage());
				}
				return Simulator.ReplyAction.SKIP;
			}
		});

		LOG.info("Selecting process complete");
		final Spawn spawn = spawns.get(playerSpawnId);

		// Start pinging the server
		simulator.startSimulatorPing();

		LOG.info("Selected persona");

		return spawn;
	}

	public List<Persona> getPersonas() throws NetworkException {
		checkConnected();
		personas.clear();
		// Personas
		long id = 1;
		QueryReplyMessage qrm = (QueryReplyMessage) simulator
				.sendMessage(new LobbyQueryMessageWithReply("persona.list"));
		try {
			for (QueryReplyMessage.Reply qrmr : qrm.getReplies()) {

				// Name
				String name = qrmr.getStrings().get(0);

				// Name again?
				String name2 = qrmr.getStrings().get(1);

				// SpawnAppearance
				String appString = qrmr.getStrings().get(2);
				Appearance appearance = new Appearance(appString);

				// Partial character object
				Persona persona = new Persona();
				persona.setEntityId(id++);
				persona.setDisplayName(name);
				persona.setAppearance(appearance);

				// Equipment
				SquirrelTable eq = SquirrelInterpretedTable.table(qrmr.getStrings().get(3));
				for (Map.Entry<Object, Object> ens : eq.entrySet()) {
					Slot sl = Slot.fromCode(((Long) ens.getKey()).intValue());
					Long ent = (Long) ens.getValue();
					persona.addToInventory(ent);
					persona.equip(sl, ent);
				}

				// Level
				persona.setLevel(Integer.parseInt(qrmr.getStrings().get(4)));

				// Profession
				persona.setProfession(Profession.fromCode(Integer.parseInt(qrmr.getStrings().get(5))));

				personas.add(persona);

			}
		} catch (IOException pe) {
			throw new NetworkException(NetworkException.ErrorType.PARSING_ERROR, "Failed to parse persona details.",
					pe);
		}
		return new ArrayList<>(personas);
	}

	public Persona createCharacter(Persona character, Map<String, RGB> skin) throws NetworkException {
		List<String> l = new ArrayList<>();
		l.add(character.getFirstName());
		l.add(character.getLastName());
		l.add(Icelib.toEnglish(character.getProfession()));
		final Appearance appearance = character.getAppearance();
		final Appearance.Race race = appearance.getRace();
		final Appearance.Gender gender = appearance.getGender();
		l.add(String.valueOf(race.getCode()));
		l.add(String.valueOf(gender.toCode()));
		l.add(String.valueOf(appearance.getBody().getCode()));
		l.add(String.valueOf(appearance.getHead().getCode()));
		l.add(String.valueOf(appearance.getSize()));
		l.add(String.valueOf(character.getEquipment().get(Slot.CHEST)));
		l.add(String.valueOf(character.getEquipment().get(Slot.LEGS)));
		l.add(String.valueOf(character.getEquipment().get(Slot.FEET)));

		for (Map.Entry<String, RGB> en : skin.entrySet()) {
			l.add(en.getKey());
			l.add(Icelib.toHexNumber(en.getValue()));
		}
		LOG.info("Create: " + l);

		LobbyQueryMessage qm = new LobbyQueryMessageWithReply("persona.create", l);
		QueryReplyMessage qr = (QueryReplyMessage) simulator.sendMessage(qm);
		if (qr.isError()) {
			if (qr.getErrorMessage().indexOf("A character with that name already exists") != -1) {
				throw new NetworkException(NetworkException.ErrorType.NAME_TAKEN, qr.getErrorMessage());
			} else {
				throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR, qr.getErrorMessage());
			}
		}
		getPersonas();
		for (Persona gc : personas) {
			if (gc.getDisplayName().equals(character.getDisplayName())) {
				return gc;
			}
		}
		throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
				"New character was not in new persona list.");
	}

	public void accountTracking(int idx) throws NetworkException {
		LobbyQueryMessage qm = new LobbyQueryMessage("account.tracking", String.valueOf(idx));
		simulator.sendMessage(qm);
	}

	public void deletePersona(int personaIndex) throws NetworkException {
		LobbyQueryMessage qm = new LobbyQueryMessage("persona.delete", String.valueOf(personaIndex));
		simulator.sendMessage(qm);
	}

	public GameItem getItemByName(String itemName) throws NetworkException {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public GameItem getItem(long itemId) throws NetworkException {
		if (simulator.getMode().equals(ProtocolState.LOBBY)) {
			ItemQueryReplyMessage irm = (ItemQueryReplyMessage) simulator
					.sendMessage(new LobbyItemQueryMessage(itemId));
			if (irm == null)
				throw new IllegalArgumentException(String.format("No item with ID of %d", itemId));
			try {
				return new GameItem(irm);
			} catch (IOException pe) {
				throw new NetworkException(NetworkException.ErrorType.PARSING_ERROR, pe);
			}
		} else {
			Icelib.removeMe("Get item %s", itemId);
			Icelib.dumpTrace();
			ItemQueryMessage aqm = new ItemQueryMessage(itemId);
			ItemQueryReplyMessage rep = (ItemQueryReplyMessage) simulator.sendMessage(aqm);
			if (rep == null)
				throw new IllegalArgumentException(String.format("No item with ID of %d", itemId));
			try {
				return new GameItem(rep);
			} catch (IOException pe) {
				throw new NetworkException(NetworkException.ErrorType.PARSING_ERROR, pe);
			}
		}
	}

	public void swapInventoryItem(int slot1, int slot2) throws NetworkException {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public void deequip(int returnToSlot, Slot slot) throws NetworkException {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public void equip(Slot slotToEquipTo, int inventorySlotToTakeFrom) throws NetworkException {
		throw new UnsupportedOperationException("Not supported yet."); // To
																		// change
																		// body
																		// of
																		// generated
																		// methods,
																		// choose
																		// Tools
																		// |
																		// Templates.
	}

	public void setStatus(String status) throws NetworkException {
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("friends.status", status);
		QueryReplyMessage replies = (QueryReplyMessage) simulator.sendMessage(msg);
	}

	public void warpToLocation(Point3D location) throws NetworkException {
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("warp", String.valueOf(location.x),
				String.valueOf(location.z));
		QueryReplyMessage replies = (QueryReplyMessage) simulator.sendMessage(msg);
	}

	/**
	 * Select a henge or grove.
	 * 
	 * @param hengeName
	 */
	public void selectHenge(String name) throws NetworkException {
		// TODO no idea what that number 1 on the end is
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("henge.setDest", name, "1");
		QueryReplyMessage reply = (QueryReplyMessage) simulator.sendMessage(msg);
		LOG.info(String.format("Set henge replied with %s", reply));
		// destroySpawn(getPlayerSpawn());

		// String answer = reply.getReplies().get(0).getStrings().get(0);
		// if (!answer.equals("OK")) {
		// throw new
		// NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
		// "Failed to select henge. " + answer);
		// }
	}

	/**
	 * Get a list of groves available at the current location.
	 * 
	 * @return list of grove names
	 */
	public List<HengeListMessage.Henge> getGroves() {
		final List<HengeListMessage.Henge> g = new ArrayList<>();
		final GameQueryMessageWithReply msg = new GameQueryMessageWithReply("grove");
		simulator.sendAndAwaitReplies(msg, new Simulator.ReplyCallback() {
			@Override
			public Simulator.ReplyAction onReply(SimulatorMessage mesg) throws NetworkException {
				if (mesg instanceof CreatureEventReplyMessage && ((CreatureEventReplyMessage) mesg)
						.getType() == CreatureEventReplyMessage.SUBMSG_HENGE_LIST) {
					HengeListMessage hlm = (HengeListMessage) (((CreatureEventReplyMessage) mesg).getSubMessage());
					g.addAll(hlm.getHenges());
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof QueryReplyMessage && ((QueryReplyMessage) mesg).getId() == msg.getId()) {
					return Simulator.ReplyAction.RETURN;
				}
				return Simulator.ReplyAction.SKIP;
			}
		});
		return g;
	}

	public void warpToPlayer(String playerName) throws NetworkException {
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("warp", playerName);
		QueryReplyMessage replies = (QueryReplyMessage) simulator.sendMessage(msg);
	}

	public void deleteScenery(SceneryItem sceneryItem) {
		final GameQueryMessageWithReply msg = new GameQueryMessageWithReply("scenery.delete",
				String.valueOf(sceneryItem.getId()));
		checkQueryReply((QueryReplyMessage) simulator.sendMessage(msg));
	}

	public SceneryItem updateSceneryName(final SceneryItem item) {
		return doSceneryUpdate(item,
				new GameQueryMessageWithReply("scenery.edit", String.valueOf(item.getId()), "name", item.getName()))
						.get(0);
	}

	public SceneryItem updateSceneryAsset(final SceneryItem item) {
		return doSceneryUpdate(item,
				new GameQueryMessageWithReply("scenery.edit", String.valueOf(item.getId()), "asset", item.getAsset()))
						.get(0);
	}

	public SceneryItem updateSceneryLayer(final SceneryItem item) {
		return doSceneryUpdate(item, new GameQueryMessageWithReply("scenery.edit", String.valueOf(item.getId()),
				"layer", String.valueOf(item.getLayer()))).get(0);
	}

	public SceneryItem updateSceneryFlags(final SceneryItem item) {
		long flags = 0;
		if (item.isLocked()) {
			flags = flags | SceneryQueryReplyMessage.FLAG_LOCKED;
		}
		if (item.isPrimary()) {
			flags = flags | SceneryQueryReplyMessage.FLAG_PRIMARY;
		}
		return doSceneryUpdate(item, new GameQueryMessageWithReply("scenery.edit", String.valueOf(item.getId()),
				"flags", String.valueOf(flags))).get(0);
	}

	public SceneryItem updateScenery(final SceneryItem item) {
		return doSceneryUpdate(item, new GameQueryMessageWithReply("scenery.edit", String.valueOf(item.getId()), "p",
				String.format("%1.2f %1.2f %1.2f", item.getLocation().x, item.getLocation().y, item.getLocation().z),
				"q",
				String.format("%s %f %f %f", item.getRotation().w, item.getRotation().x, item.getRotation().y,
						item.getRotation().z),
				"s", String.format("%1.2f %1.2f %1.2f", item.getScale().x, item.getScale().y, item.getScale().z)))
						.get(0);
	}

	public Simulator getSimulator() {
		return simulator;
	}

	public SceneryItem addScenery(final String propName, Point3D toLocation, Point3D scale, Point4D rotation)
			throws NetworkException {
		final List<SceneryItem> g = new ArrayList<>();
		final GameQueryMessageWithReply msg = new GameQueryMessageWithReply("scenery.edit", "NEW", "asset", propName,
				"p", String.format("%1.2f %1.2f %1.2f", toLocation.x, toLocation.y, toLocation.z), "q",
				String.format("%f %f %f %s", rotation.x, rotation.y, rotation.z, rotation.w), "s",
				String.format("%1.2f %1.2f %1.2f", scale.x, scale.y, scale.z));
		simulator.sendAndAwaitReplies(msg, new Simulator.ReplyCallback() {
			@Override
			public Simulator.ReplyAction onReply(SimulatorMessage mesg) throws NetworkException {
				if (mesg instanceof SceneryQueryReplyMessage) {
					SceneryItem prop = createPropWithAssetAsName(mesg);
					if (prop.getAsset().equals(propName)) {
						// QUESTION Is it possible to get a response for a
						// different
						g.add(prop);
					}
					// We just skip this reply so it is handled as by {@link
					// #receivedMessage}
					// and sent to all the listeners
				} else if (mesg instanceof QueryReplyMessage && ((QueryReplyMessage) mesg).getId() == msg.getId()) {
					// TODO not sure about this error checking, needs testing
					// with various errors, such as permissions
					QueryReplyMessage qrm = (QueryReplyMessage) mesg;
					return checkQueryReply(qrm);
				}
				return Simulator.ReplyAction.SKIP;
			}
		});
		if (g.isEmpty()) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
					"Expect new prop response, didn't get one.");
		}
		return g.get(0);
	}

	public List<SceneryItem> listScenery(PageLocation pl) {
		return listScenery(pl, getZone());
	}

	public List<SceneryItem> listScenery(PageLocation pl, Zone zone) {
		final List<SceneryItem> g = new ArrayList<>();
		final GameQueryMessageWithReply msg = new GameQueryMessageWithReply("scenery.list",
				String.valueOf(zone.getId()), String.valueOf(pl.x), String.valueOf(pl.y));
		simulator.sendAndAwaitReplies(msg, new Simulator.ReplyCallback() {
			@Override
			public Simulator.ReplyAction onReply(SimulatorMessage mesg) throws NetworkException {
				if (mesg instanceof SceneryQueryReplyMessage) {
					SceneryItem prop = createPropWithAssetAsName(mesg);
					g.add(prop);
					return Simulator.ReplyAction.HANDLED;
				} else if (mesg instanceof QueryReplyMessage && ((QueryReplyMessage) mesg).getId() == msg.getId()) {
					QueryReplyMessage repl = (QueryReplyMessage) mesg;
					for (Reply r : repl.getReplies()) {
						Icelib.removeMe("QUERY REPLY: %d", r.getReplyNumber());
						for (String s : r.getStrings()) {
							Icelib.removeMe("QUERY REPLY: >> %s", s);

						}
					}
					return Simulator.ReplyAction.RETURN;
				}
				return Simulator.ReplyAction.SKIP;
			}
		});
		return g;
	}

	public List<Persona> getFriends() throws NetworkException {
		List<Persona> friends = new ArrayList<>();
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("friends.list");
		QueryReplyMessage replies = (QueryReplyMessage) simulator.sendMessage(msg);
		for (QueryReplyMessage.Reply reply : replies.getReplies()) {
			String name = reply.getStrings().get(0);
			int level = Integer.parseInt(reply.getStrings().get(1));
			Profession prof = Profession.fromCode(Integer.parseInt(reply.getStrings().get(2)));
			boolean online = "true".equals(reply.getStrings().get(3));
			String status = reply.getStrings().get(4);
			String shard = reply.getStrings().get(5);
			Persona gc = new Persona();
			gc.setOnline(online);
			gc.setDisplayName(name);
			gc.setLevel(level);
			gc.setProfession(prof);
			gc.setStatusText(status);
			gc.setShard(shard);
			friends.add(gc);
		}
		return friends;
	}

	public void addFriend(String name) throws NetworkException {
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("friends.add", name);
		QueryReplyMessage reply = (QueryReplyMessage) simulator.sendMessage(msg);
		String answer = reply.getReplies().get(0).getStrings().get(0);
		if (!answer.equals("OK")) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
					"Failed to add friend. " + answer);
		}
	}

	public void removeFriend(String name) throws NetworkException {
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("friends.remove", name);
		QueryReplyMessage reply = (QueryReplyMessage) simulator.sendMessage(msg);
		String answer = reply.getReplies().get(0).getStrings().get(0);
		if (!answer.equals("OK")) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
					"Failed to remove friend. " + answer);
		}
	}

	public List<String> getIgnored() throws NetworkException {
		if (ignored == null) {
			try {
				GameQueryMessageWithReply prefQuery = new GameQueryMessageWithReply("pref.getA", "chat.ignoreList");
				QueryReplyMessage reply = (QueryReplyMessage) simulator.sendMessage(prefQuery);
				if (reply.isError()) {
					throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
							String.format("Failed to retrieve ignore list. %s", reply.getErrorMessage()));
				}
				String eon = reply.getReplies().get(0).getStrings().get(0);
				ignored = new ArrayList<>();
				if (eon.length() > 0) {
					SquirrelTable ie = SquirrelInterpretedTable.table(eon);
					for (Object k : ie.keySet()) {
						ignored.add((String) k);
					}
				}
			} catch (SquirrelException ex) {
				throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
						"Failed to parse ignore list.", ex);
			}
		}
		return ignored;
	}

	public void addIgnored(String name) throws NetworkException {
		if (ignored == null) {
			getIgnored();
		}
		if (!ignored.contains(name)) {
			ignored.add(name);
		}
		saveIgnored();
	}

	public void removeIgnored(String name) throws NetworkException {
		if (ignored == null) {
			getIgnored();
		}
		ignored.remove(name);
		saveIgnored();
	}

	public void close() {
		LOG.info("Closing network client");
		if (simulator != null) {
			simulator.close();
		}
	}

	public void await() throws InterruptedException {
		checkConnected();
		LOG.info("Awaiting termination");
		simulator.awaitTermination();
		simulator.close();
	}

	public Spawn getSpawnByName(String name) {
		return spawnsByName.get(name);
	}

	public List<Spawn> getSpawns() {
		return readySpawns;
	}

	public void setSpawns(Map<Long, Spawn> spawns) {
		this.spawns = spawns;
	}

	public long getPlayerSpawnId() {
		return playerSpawnId;
	}

	public void setPlayerSpawnId(long playerSpawnId) {
		this.playerSpawnId = playerSpawnId;
	}

	public String getTerrainPath() {
		return terrainPath;
	}

	public void setTerrainPath(String terrainPath) {
		this.terrainPath = terrainPath;
	}

	public void updateInventory() throws NetworkException {
		Icelib.removeMe("Updating inventory");
		Icelib.dumpTrace();
		final GameQueryMessageWithReply msg = new GameQueryMessageWithReply("item.contents", "inv");
		final Map<Integer, Persona.SlotContents> newInventory = new HashMap<>();
		simulator.sendAndAwaitReplies(msg, new Simulator.ReplyCallback() {
			@Override
			public Simulator.ReplyAction onReply(SimulatorMessage mesg) throws NetworkException {
				if (mesg instanceof QueryReplyMessage && msg.isReply(mesg)) {
					LOG.info("Inventory update complete");

					// Update complete
					QueryReplyMessage replies = (QueryReplyMessage) mesg;
					QueryReplyMessage.Reply reply = replies.getReplies().get(0);
					int startSlot = Integer.parseInt(reply.getStrings().get(0));
					int numberOfSlots = Integer.parseInt(reply.getStrings().get(1));

					getPlayerSpawn().getPersona().setInventory(newInventory);
					fireInventoryUpdate();

					return Simulator.ReplyAction.RETURN;
				} else if (mesg instanceof InventoryQueryReplyMessage) {
					InventoryQueryReplyMessage iqrm = (InventoryQueryReplyMessage) mesg;
					LOG.info("IQ: " + iqrm);
					newInventory.put(iqrm.getSlot(), new Persona.SlotContents(iqrm.getItemId(), iqrm.getQty()));
					return Simulator.ReplyAction.HANDLED;
				}
				return Simulator.ReplyAction.SKIP;
			}
		});
	}

	public List<ForumItem> getForumCategories() throws NetworkException {
		List<ForumItem> cats = new ArrayList<>();
		cats.add(new ForumItem("Test Category 1", System.currentTimeMillis()));
		cats.add(new ForumItem("Test Category 2", System.currentTimeMillis() / 2));
		cats.add(new ForumItem("Test Category 3", System.currentTimeMillis() / 3));
		cats.add(new ForumItem("Test Category 4", System.currentTimeMillis() / 4));
		cats.add(new ForumItem("Test Category 5", System.currentTimeMillis() / 5));
		return cats;
	}

	public List<ForumPost> getForumPosts(ForumItem topic, int page) {
		List<ForumPost> l = new ArrayList<>();
		for (int i = 1; i <= 5; i++) {
			ForumPost p = new ForumPost();
			p.setAuthor("Emerald Icemoon");
			p.setNumber(i + (page * 5));
			p.setDate(System.currentTimeMillis() - (10000000 * i));
			p.setLastEdit(System.currentTimeMillis() - (5000000 * i));
			p.setEdits((int) (Math.random() * 10));
			p.setText("[b]" + i + "[/b] (page " + page
					+ ") Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed "
					+ "do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
					+ "Ut enim ad minim veniam, [b]quis[/b] nostrud exercitation ullamco "
					+ "laboris nisi ut aliquip ex ea commodo consequat. Duis aute "
					+ "irure dolor in reprehenderit in [i]voluptate[/i] velit esse cillum "
					+ "dolore eu fugiat nulla pariatur. Excepteur sint occaecat "
					+ "cupidatat non proident, sunt in culpa qui officia deserunt " + "mollit anim id est laborum.");
			l.add(p);
		}
		return l;
	}

	public List<ForumTopic> getForumThreads(ForumItem category) throws NetworkException {
		List<ForumTopic> cats = new ArrayList<>();
		cats.add(new ForumTopic("Test Thread 1", System.currentTimeMillis()));
		cats.add(new ForumTopic("Test Thread 2", System.currentTimeMillis() / 2));
		cats.add(new ForumTopic("Test Thread 3", System.currentTimeMillis() / 3));
		cats.add(new ForumTopic("Test Thread 4", System.currentTimeMillis() / 4));
		cats.add(new ForumTopic("Test Thread 5", System.currentTimeMillis() / 5));
		return cats;
	}

	@Override
	public void handlingError(SimulatorMessage mesg, Exception e) {
	}

	@Override
	public boolean receivedMessage(SimulatorMessage mesg) {
		if (mesg instanceof ChatIncomingMessage) {
			ChatIncomingMessage chat = (ChatIncomingMessage) mesg;
			String fullChannelPath = chat.getChannel();
			int idx = fullChannelPath.indexOf('/');
			String channelName = idx > -1 ? fullChannelPath.substring(0, idx) : fullChannelPath;
			String parm = idx > -1 ? fullChannelPath.substring(idx + 1) : "";
			ChannelType channel = ChannelType.fromCode(channelName);
			String recipient = null;
			if (parm.length() > 0) {
				recipient = parm.substring(1, parm.length() - 1);
			}

			// Special emote channel
			if (channel.equals(ChannelType.EMOTE)) {
				fireEmote(chat.getValue(), chat.getSender(), chat.getMessage());
			} else {
				fireChatMessage(chat.getSender(), recipient, channel, chat.getMessage());
			}
			return true;
		} else if (mesg instanceof SystemMessage) {
			for (int i = listeners.size() - 1; i >= 0; i--) {
				listeners.get(i).message(((SystemMessage) mesg).getMessage());
			}
			return true;
		} else if (mesg instanceof PingFromServerMessage) {
			try {
				if (LOG.isLoggable(Level.FINE))
					LOG.fine("Replying to ping");
				simulator.sendMessage(new PongMessage());
			} catch (NetworkException ex) {
				LOG.log(Level.SEVERE, "Failed to send ping response.", ex);
			}
			return true;
		} else if (mesg instanceof SpawnUpdateMessage) {
			SpawnUpdateMessage sum = (SpawnUpdateMessage) mesg;
			handleSpawnUpdate(sum.getId(), mesg);
			return true;
		} else if (mesg instanceof SetMapMessage) {
			SetMapMessage setMapMesg = (SetMapMessage) mesg;
			if ((setMapMesg.getMask() & SetMapMessage.SET_MAP) != 0) {
				configureZoneFromSetMapMessage(zone, setMapMesg);
				for (int i = listeners.size() - 1; i >= 0; i--) {
					listeners.get(i).zoneChanged(zone);
				}
			} else {
				LOG.severe(String.format("Unhandled SetMap mask %d", setMapMesg.getMask()));
			}
			return true;
		} else if (mesg instanceof CreatureEventReplyMessage) {
			CreatureEventReplyMessage pllm = (CreatureEventReplyMessage) mesg;
			Spawn spawn = spawns.get(pllm.getSpawnId());
			switch (pllm.getType()) {
			case CreatureEventReplyMessage.SUBMSG_LOGOUT:
				if (spawn == null) {
					LOG.warning(
							String.format("Got logout for spawn (%d) that we didn't know about.", pllm.getSpawnId()));
				} else {
					LOG.info(String.format("Got logout for spawn (%d).", pllm.getSpawnId()));
					destroySpawn(spawn);
				}
				return true;
			case CreatureEventReplyMessage.SUBMSG_JUMP:
				if (spawn == null) {
					LOG.warning(String.format("Got jump for spawn (%d) that we didn't know about.", pllm.getSpawnId()));
				} else {
					LOG.info(String.format("Network jump to %s", spawn.getLocation()));
					spawn.fireJump();
				}
				return true;
			}
		} else if (mesg instanceof SceneryQueryReplyMessage) {
			LOG.info(String.format("Scenery update %s", mesg));
			final SceneryItem prop = createPropWithAssetAsName(mesg);
			if (prop.getAssetName().equals("")) {
				// An empty name signals a delete
				firePropDeleted(prop);
			} else {
				firePropAddedOrUpdated(prop);
			}
			return true;
		} else if (mesg instanceof StatusUpdateMessage) {
			StatusUpdateMessage sum = (StatusUpdateMessage) mesg;
			LOG.info(String.format("Status update: %s", mesg));
			if (sum.getType().equals(StatusUpdateMessage.Type.LOGGED_IN)) {
				firePlayerLoggedIn(sum.getName());
			} else if (sum.getType().equals(StatusUpdateMessage.Type.LOGGED_OUT)) {
				firePlayerLoggedOut(sum.getName());
			} else if (sum.getType().equals(StatusUpdateMessage.Type.FRIEND_ADDED)) {
				fireFriendAdded(sum.getName());
			} else if (sum.getType().equals(StatusUpdateMessage.Type.STATUS_CHANGED)) {
				fireStatusChanged(sum.getName(), sum.getStatus());
			} else if (sum.getType().equals(StatusUpdateMessage.Type.SHARD_CHANGED)) {
				fireShardChanged(sum.getName(), sum.getStatus());
			}
			return true;
		} else if (mesg instanceof ModReplyMessage) {
			ModReplyMessage mod = (ModReplyMessage) mesg;
			if (mod.getOp().equals(Op.POPUP)) {
				firePopup(mod.getMessage());
				return true;
			}
		}
		return false;
	}

	@Override
	public void simulatorDisconnect(Exception e) {
		simulator.close();
		if (e != null) {
			LOG.log(Level.SEVERE, "Simulator disconnected.", e);
		} else {
			LOG.info("Simulator disconnected");
		}
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).disconnected(e);
		}
		spawns.clear();
		playerSpawnId = -1;
		ignored = null;
		zone = new Zone();
		terrainPath = null;
		movementExecutor.shutdownNow();
	}

	public void setArmed(Armed armed) throws NetworkException {
		final GameQueryMessageWithReply msg = new GameQueryMessageWithReply("visWeapon",
				String.valueOf(armed.toCode()));
		QueryReplyMessage reply = (QueryReplyMessage) simulator.sendMessage(msg);
		if (reply.isError()) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
					"Failed to arm. " + reply.getErrorMessage());
		}
	}

	public void jump() {
		LOG.info("Sending jump");
		simulator.sendMessage(new PlayerJumpMessage());
	}

	public Spawn getPlayerSpawn() {
		return playerSpawn;
	}

	public void setClientLoading(boolean loading) {
		if (this.loading != loading) {
			LOG.info(String.format("Setting client loading to %s", loading));
			simulator.sendMessage(new GameQueryMessageWithReply("client.loading", String.valueOf(loading)) {
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
			this.loading = loading;
		} else {
			LOG.warning(String.format("Attempt to set client loading to %s when it is already %s", loading, loading));
		}
	}

	private void configureZoneFromSetMapMessage(Zone zone, SetMapMessage setMap) {
		LOG.info("Setting map " + setMap);
		zone.setId(setMap.getZoneDefId());
		zone.setMapName(setMap.getMapName());
		zone.setDescription(setMap.getZoneIdString());
		zone.setPageSize(setMap.getPageSize());
		zone.setEnvironmentType(setMap.getEnvironment());
		zone.setTerrainConfig(setMap.getTerrain());
	}

	private Simulator.ReplyAction checkQueryReply(QueryReplyMessage qrm) throws NetworkException {
		if (qrm.isError()) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR, qrm.getErrorMessage());
		}
		if (qrm.getReplies().size() == 1 && !qrm.getReplies().get(0).getStrings().isEmpty()
				&& qrm.getReplies().get(0).getStrings().get(0).equals("OK")) {
			// SceneryItem added OK
			return Simulator.ReplyAction.RETURN;
		} else {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR, qrm.toString());
		}
	}

	private SceneryItem createPropWithAssetAsPath(SimulatorMessage mesg) {
		String name = ((SceneryQueryReplyMessage) mesg).getAsset();
		if (!name.endsWith(".csm.xml")) {
			throw new IllegalArgumentException("Expected asset path to end with .csm.xml, got " + mesg);
		}
		if (!name.startsWith("Prop/")) {
			throw new IllegalArgumentException("Expected asset path to start Props/");
		}
		name = name.substring(0, name.length() - 9).substring(6);
		return createAsset(mesg, name);
	}

	private SceneryItem createPropWithAssetAsName(SimulatorMessage mesg) {
		return createAsset(mesg, ((SceneryQueryReplyMessage) mesg).getAsset());
	}

	private SceneryItem createAsset(SimulatorMessage mesg, String asset) {
		SceneryQueryReplyMessage sqrm = (SceneryQueryReplyMessage) mesg;
		SceneryItem prop = new SceneryItem(asset);
		prop.setId(sqrm.getId());
		prop.setLocked(sqrm.isLocked());
		prop.setPrimary(sqrm.isPrimary());
		prop.setLocation(new Point3D(sqrm.getX(), sqrm.getY(), sqrm.getZ()));
		prop.setScale(new Point3D(sqrm.getScaleX(), sqrm.getScaleY(), sqrm.getScaleZ()));
		prop.setRotation(new Point4D(sqrm.getRotateX(), sqrm.getRotateY(), sqrm.getRotateZ(), sqrm.getRotateW()));
		return prop;
	}

	private void checkConnected() throws IllegalStateException {
		if (simulator == null && !simulator.isConnected()) {
			throw new IllegalStateException("Not connected.");
		}
	}

	private void fireEmote(long id, String sender, String emote) {
		for (ClientListener l : listeners) {
			l.emote(id, sender, emote);
		}
	}

	private void fireInventoryUpdate() {
		for (ClientListener l : listeners) {
			l.inventoryUpdate();
		}
	}

	private void firePropDeleted(SceneryItem prop) {
		LOG.info(String.format("Prop deleted %s", prop));
		for (ClientListener l : listeners) {
			l.propDeleted(prop);
		}
	}

	private void firePlayerLoggedIn(String name) {
		for (ClientListener l : listeners) {
			l.playerLoggedIn(name);
		}
	}

	private void firePlayerLoggedOut(String name) {
		for (ClientListener l : listeners) {
			l.playerLoggedOut(name);
		}
	}

	private void fireFriendAdded(String name) {
		for (ClientListener l : listeners) {
			l.friendAdded(name);
		}
	}

	private void fireStatusChanged(String name, String status) {
		for (ClientListener l : listeners) {
			l.statusChanged(name, status);
		}
	}

	private void fireShardChanged(String name, String status) {
		for (ClientListener l : listeners) {
			l.shardChanged(name, status);
		}
	}

	private void firePopup(String message) {
		for (ClientListener l : listeners) {
			l.popup(message);
		}
	}

	private void firePropAddedOrUpdated(SceneryItem prop) {
		LOG.info(String.format("Prop added or updated %s. Firing to %d listeners", prop, listeners.size()));
		for (ClientListener l : listeners) {
			l.propAddedOrUpdated(prop);
		}
	}

	private void fireChatMessage(String sender, String recipient, ChannelType channel, String string) {
		for (ClientListener l : listeners) {
			l.chatMessage(sender, recipient, channel, string);
		}
	}

	private void saveIgnored() throws NetworkException {
		SquirrelTable eo = new SquirrelTable();
		for (String i : ignored) {
			eo.put(i, true);
		}
		GameQueryMessageWithReply msg = new GameQueryMessageWithReply("pref.setA", "chat.ignoreList", eo.toString());
		QueryReplyMessage reply = (QueryReplyMessage) simulator.sendMessage(msg);
		if (reply.isError()) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
					"Failed to save ignore list. " + reply.getErrorMessage());
		}
	}

	private void handleSpawnUpdate(long spawnId, SimulatorMessage mesg)
			throws NumberFormatException, IllegalStateException {
		// try {
		SpawnUpdateMessage spawnMessage = (SpawnUpdateMessage) mesg;
		// LOG.info("Handling spawn update " + spawnMessage);
		// if (spawnId < 1) {
		// // spawnId = playerSpawnId;
		// if (lastGoodSpawnId != -1) {
		// LOG.warning("USING WORKAROUND FOR ZERO SPAWN-ID (CHANGING TO " +
		// lastGoodSpawnId
		// + "). IT DOESNT ALWAYS WORK, EXPECT WEIRDNESS");
		// spawnId = lastGoodSpawnId;
		// } else {
		// LOG.severe(String.format("Spawn ID is < 1 for " + spawnMessage,
		// spawnId));
		// throw new IllegalStateException("Bad spawn ID " + spawnId);
		// }
		// } else {
		// lastGoodSpawnId = spawnId;
		// }

		Spawn spawn = spawns.get(spawnId);
		for (SpawnUpdate su : spawnMessage.getUpdates()) {

			if (spawn == null) {
				try {
					spawn = createSpawn(spawnId);
				} catch (IllegalArgumentException iae) {
					//
					LOG.log(Level.WARNING, String.format("Skipping spawn update %s", su), iae);
					continue;
				}
			}

			if (su instanceof ZoneUpdate) {
				ZoneUpdate ini = (ZoneUpdate) su;
				Point3D oldLocation = spawn.serverLocation.clone();
				spawn.serverLocation.x = ini.getX();
				spawn.serverLocation.y = Float.MIN_VALUE;
				spawn.serverLocation.z = ini.getZ();
				spawn.instance = ini.getInstance();
				spawn.zone = ini.getZone();
				LOG.info(String.format("Spawn %s ", ini));
				if (!spawn.isReady()) {
					LOG.info("Spawn now ready");
					spawnReady(spawn);
				} else {
					LOG.info("Spawn was already ready. Firing location change.");
					spawn.fireServerLocationChanged(oldLocation, true);
				}
			} else if (su instanceof Velocity) {
				Velocity vel = (Velocity) su;
				spawn.setVelocity(vel.getRotation(), vel.getHeading(), vel.getSpeed());
			} else if (su instanceof Position) {
				Position pos = (Position) su;
				Point3D oldLocation = spawn.serverLocation.round();
				spawn.serverLocation.x = pos.getX();
				spawn.serverLocation.z = pos.getZ();
				if (!oldLocation.equals(spawn.serverLocation)) {
					spawn.fireServerLocationChanged(oldLocation, false);
				}
			} else if (su instanceof Elevation) {
				Elevation el = (Elevation) su;
				spawn.elevation = el.getElevation();
				spawn.fireRecalcElevation();
			}
		}

		// default:
		// if (spawn == null) {
		// spawn = createSpawn(spawnId);
		// }
		// switch (spawnMessage.getSubType()) {
		// case STOP:
		// // TODO not convinced by this
		// SpawnUpdateMessage.SpawnStop stop = (SpawnUpdateMessage.SpawnStop)
		// spawnMessage.getSubData();
		// oldLocation = spawn.location.round();
		// spawn.serverLocation.x = stop.getX();
		// spawn.serverLocation.z = stop.getZ();
		// spawn.serverLocation.y = stop.getY();
		// LOG.info(String.format("STOP message for spawn %s at %s",
		// spawn.getId(), spawn.getLocation()));
		//
		// // Fire one event with some actual movement
		// if (!oldLocation.equals(spawn.serverLocation)) {
		// spawn.fireServerLocationChanged(oldLocation, false);
		// }
		//
		// // And a second event with no movement to signal stopping
		// spawn.fireServerLocationChanged(spawn.serverLocation, false);
		// break;
		// case APPEARANCE:
		// SpawnUpdateMessage.SpawnAppearance app =
		// (SpawnUpdateMessage.SpawnAppearance) spawnMessage.getSubData();
		// if (spawn.getPersona().getDisplayName() != null) {
		// spawnsByName.remove(spawn.getPersona().getDisplayName());
		// }
		// spawn.getPersona().setDisplayName(app.getDisplayName());
		// spawnsByName.put(app.getDisplayName(), spawn);
		// spawn.getPersona().setSubName(app.getSubName());
		// spawn.getPersona().getCoin().setCoin(app.getCoin());
		// spawn.getPersona().setExp(app.getExperience());
		// spawn.getPersona().setCredits(app.getCredits());
		// spawn.getPersona().getAppearance().parse(app.getAppearance());
		// spawn.getPersona().setEntityId(spawnId);
		//
		// updateEquipment(spawn, app.getEquipment());
		//
		// // Seems we don't get an IN_OUT for spawns that are already
		// // near,
		// // so lets do it when we get their appearance instead
		// if (!spawn.isReady()) {
		// spawnReady(spawn);
		// }
		//
		// spawn.fireAppearanceChange();
		// break;
		// case STATS:
		// SpawnUpdateMessage.SpawnStats ech = (SpawnUpdateMessage.SpawnStats)
		// spawnMessage.getSubData();
		// if (SpawnUpdateMessage.StatsType.STATS.equals(ech.getType())) {
		// spawn.getPersona().setArmour(ech.getArmour());
		// spawn.getPersona().setBaseCon(ech.getBaseCon());
		// spawn.getPersona().setBaseDex(ech.getBaseDex());
		// spawn.getPersona().setBasePsy(ech.getBasePsy());
		// spawn.getPersona().setBaseSpi(ech.getBaseSpi());
		// spawn.getPersona().setBaseStr(ech.getBaseStr());
		// spawn.getPersona().setConstitution(ech.getCon());
		// spawn.getPersona().setDexterity(ech.getDex());
		// spawn.getPersona().setHealth(ech.getHealth());
		// spawn.getPersona().setPsyche(ech.getPsy());
		// spawn.getPersona().setSpirit(ech.getSpi());
		// spawn.getPersona().setStrength(ech.getStr());
		// spawn.fireStatsChanged();
		// } else if (SpawnUpdateMessage.StatsType.ARMED.equals(ech.getType()))
		// {
		// Armed armed = Armed.values()[ech.getArmed()];
		// spawn.armed = armed;
		// spawn.fireArmedChanged(armed);
		// }
		// break;
		// case EQUIPMENT:
		// SpawnUpdateMessage.SpawnEquipment eq =
		// (SpawnUpdateMessage.SpawnEquipment) spawnMessage.getSubData();
		// LOG.info(String.format("Equipment update. %s", spawnMessage));
		// updateEquipment(spawn, eq.getEquipment());
		// spawn.fireEquipmentChanged();
		// break;
		// default:
		// LOG.warning(String.format("Unhandled spawn update message. %s",
		// spawnMessage.toString()));
		// break;
		// }

		// }
		// } catch (ParseException pe) {
		// LOG.log(Level.SEVERE, "Failed to parse spawn update message.", pe);
		// }
	}

	private void spawnReady(Spawn spawn) {
		if (readySpawns.contains(spawn)) {
			throw new IllegalStateException("Spawn " + spawn.getId() + " is already ready.");
		}
		LOG.info(String.format("Spawn %d ready.", spawn.getId()));
		spawn.ready = true;
		readySpawns.add(spawn);
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).spawned(spawn);
		}
	}

	private void DUMPING_GROUND() {
		// s.sendMessage(new ItemQueryMessage(20523) {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got ability " + reply.toString());
		// return true;
		// }
		// });
		// s.sendMessage(new ItemQueryMessage(20524) {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got ability " + reply.toString());
		// return true;
		// }
		// });
		// s.sendMessage(new ItemQueryMessage(20525) {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got ability " + reply.toString());
		// return true;
		// }
		// });
		// s.sendMessage(new ItemQueryMessage(20527) {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got ability " + reply.toString());
		// return true;
		// }
		// });
		// s.sendMessage(new RequestSpawnUpdateMessage(playerSpawnId));
		// s.sendMessage(new GameQueryMessageWithReply("ab.remainingcooldowns")
		// {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got cooldowns");
		// return true;
		// }
		// });
		// s.sendMessage(new GameQueryMessageWithReply("client.loading", "true")
		// {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Ack of client loading");
		// return true;
		// }
		// });
		// s.sendMessage(new GameQueryMessageWithReply("friends.list") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got friends");
		// return true;
		// }
		// });
		// s.sendMessage(new GameQueryMessageWithReply("friends.getstatus") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got friend status");
		// return true;
		// }
		// });
		// s.sendMessage(new GameQueryMessageWithReply("clan.info") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got clan");
		// return true;
		// }
		// });
		//
		// // Clan list
		// s.sendMessage(new GameQueryMessageWithReply("clan.list") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got clan list");
		// return true;
		// }
		// });
		// Admin Check
		// s.sendMessage(new GameQueryMessageWithReply("admin.check") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got admin check: " + reply);
		// return true;
		// }
		// });
		//
		// // Item equipment?
		// s.sendMessage(new GameQueryMessageWithReply("item.contents", "eq") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got item contents eq: " + reply);
		// return true;
		// }
		// });
		// Ability ownage?
		// s.sendMessage(new GameQueryMessageWithReply("ab.ownage.ist") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got ability ownage: " + reply);
		// return true;
		// }
		// });
		// Account fulfill
		// s.sendMessage(new GameQueryMessageWithReply("account.fulfill") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got account fulfill: " + reply);
		// return true;
		// }
		// });
		// Map marker
		// s.sendMessage(new GameQueryMessageWithReply("account.fulfill") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Got account fulfill: " + reply);
		// return true;
		// }
		// });
		// Stop loading!
		// s.sendMessage(new GameQueryMessageWithReply("client.loading",
		// "false") {
		// @Override
		// public boolean isWaitForReply() {
		// return false;
		// }
		//
		// @Override
		// protected boolean onReply(SimulatorMessage reply) {
		// LOG.info("Ack of client loading false");
		// return true;
		// }
		// });
	}

	private Spawn createSpawn(long spawnId) {
		if (spawnId < 1)
			throw new IllegalArgumentException(String.format("Invalid spawn ID %d", spawnId));
		simulator.sendMessage(new RequestSpawnUpdateMessage(spawnId));
		Spawn spawn = new Spawn(spawnId);
		if (spawnId != playerSpawnId) {
			final Persona persona = new Persona();
			persona.setAppearance(new Appearance());
			spawn.setPersona(persona);
		}
		spawns.put(spawnId, spawn);
		return spawn;
	}

	private void destroySpawn(Spawn spawn) {
		LOG.info(String.format("Spawn %s destroyed.", spawn));
		spawn.fireDestroyed();
		if (spawn.getId() != playerSpawnId) {
			spawns.remove(spawn.getId());
			spawnsByName.remove(spawn.getPersona().getDisplayName());
			readySpawns.remove(spawn);
		}
	}

	private void handleServiceTokenLogin(InputStream in) {

		/*
		 * The token supplied to Start.exe must be in the format
		 *
		 * <sessionCookieName>:<sessionCookieValue>:<uid>
		 *
		 * Because this didn't come from the API, there will be NO X-CSRF-Token.
		 * As with username/password authentication, we then pass this on to the
		 * server that calls back to the website to validate the user (possibly
		 * creating the game account if it doesn't already exist).
		 *
		 */

		JsonElement jelement = new JsonParser().parse(new InputStreamReader(in));
		JsonObject obj = jelement.getAsJsonObject();

		String[] spl = authToken.split(":");
		String uid = spl[2];
		String sessionId = spl[1];
		String sessionName = spl[0];

		String name = obj.has("name") ? obj.get("name").getAsString() : "UNKNOWN";
		String tkn = "NONE:" + sessionId + ":" + sessionName + ":" + uid;
		SimulatorMessage msg = simulator.sendMessage(new TokenLoginMessage(name, tkn));
		if (msg instanceof LobbyErrorMessage) {
			throw new NetworkException(NetworkException.ErrorType.INCORRECT_USERNAME_OR_PASSWORD,
					((LobbyErrorMessage) msg).getMessage());
		}
	}

	private void handleServiceLogin(InputStream in, String username) {
		JsonElement jelement = new JsonParser().parse(new InputStreamReader(in));
		JsonObject obj = jelement.getAsJsonObject();
		String token = mXCSRFToken + ":" + obj.get("sessid").getAsString() + ":" + obj.get("session_name").getAsString()
				+ ":" + obj.get("user").getAsJsonObject().get("uid").getAsString();

		SimulatorMessage msg = simulator.sendMessage(new TokenLoginMessage(username, token));
		if (msg instanceof LobbyErrorMessage) {
			throw new NetworkException(NetworkException.ErrorType.INCORRECT_USERNAME_OR_PASSWORD,
					((LobbyErrorMessage) msg).getMessage());
		}
	}

	private void handleServiceToken(InputStream in, String username, char[] password) throws IOException {
		Gson gson = new Gson();

		JsonElement jelement = new JsonParser().parse(new InputStreamReader(in));
		JsonObject obj = jelement.getAsJsonObject();
		JsonElement tokenEl = obj.get("token");
		mXCSRFToken = tokenEl == null ? null : tokenEl.getAsString();
		if (mXCSRFToken == null) {
			throw new NetworkException(ErrorType.FAILED_TO_GET_TOKEN, "Failed to retrieve XCSRF token");
		}

		// Now we can authenticate the username and password

		mXCSRFToken = null;
		URL replyUrl = new URL(new URL(simulator.getAuthData()), "rest/user/login.json");
		HttpURLConnection connection = (HttpURLConnection) replyUrl.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("User-Agent", HTTP_USER_AGENT);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Cookie", "X-CSRF-Token=" + mXCSRFToken);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		String content = "username=" + URLEncoder.encode(username, "UTF-8") + "&password="
				+ URLEncoder.encode(new String(password), "UTF-8");
		connection.getOutputStream().write(content.getBytes("UTF-8"));
		connection.getOutputStream().flush();
		if (connection.getResponseCode() == 200) {
			handleServiceLogin(connection.getInputStream(), username);
		} else {
			throw new NetworkException(ErrorType.GENERAL_NETWORK_ERROR,
					String.format("Service responded with error code %d (%s)", connection.getResponseCode(),
							connection.getResponseMessage()));
		}
	}

	private List<SceneryItem> doSceneryUpdate(final SceneryItem item, final GameQueryMessageWithReply msg) {
		final List<SceneryItem> g = new ArrayList<>();
		simulator.sendAndAwaitReplies(msg, new Simulator.ReplyCallback() {
			@Override
			public Simulator.ReplyAction onReply(SimulatorMessage mesg) throws NetworkException {
				if (mesg instanceof SceneryQueryReplyMessage) {
					LOG.info(String.format("Do Scenery update %s", mesg));
					SceneryItem prop = createPropWithAssetAsName(mesg);
					if (prop.getId() == item.getId()) {
						// QUESTION Is it possible to get a response for a
						// different
						g.add(prop);
					}
					// We just skip this reply so it is handled as by {@link
					// #receivedMessage}
					// and sent to all the listeners
				} else if (mesg instanceof QueryReplyMessage && ((QueryReplyMessage) mesg).getId() == msg.getId()) {
					// TODO not sure about this error checking, needs testing
					// with various errors, such as permissions
					QueryReplyMessage qrm = (QueryReplyMessage) mesg;
					return checkQueryReply(qrm);
				}
				return Simulator.ReplyAction.SKIP;
			}
		});
		if (g.isEmpty()) {
			throw new NetworkException(NetworkException.ErrorType.GENERAL_NETWORK_ERROR,
					"Expect new prop response, didn't get one.");
		}
		return g;
	}

	private void doMove(boolean force) throws NetworkException {
		Icelib.removeMe("Client now sending movement %d %d %s", moveDeg, rotDeg, speed);
		MovementMessage mesg = null;
		synchronized (moveLock) {
			mesg = new MovementMessage(Math.round(location.x), Math.round(location.y), Math.round(location.z), moveDeg,
					rotDeg, speed);
			lastSentLoc = location.clone();
			lastSentDeg = rotDeg;
			lastSentMoveDeg = moveDeg;
			lastSentSpeed = speed;
		}
		simulator.sendMessage(mesg);
		if (!force) {
			movementFuture = movementExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					if (!lastSentLoc.equals(Client.this.location) || lastSentDeg != Client.this.rotDeg
							|| speed != lastSentSpeed || lastSentMoveDeg != Client.this.moveDeg) {
						doMove(false);
					} else {
						movementFuture = null;
					}
				}
			}, 250, TimeUnit.MILLISECONDS);
		}
	}

	private void updateEquipment(Spawn spawn, Map<Slot, Long> eq) {
		LOG.info(String.format("Updating equipment in spawn %d (%s)", spawn.getId(),
				spawn.getPersona().getDisplayName()));
		spawn.getPersona().removeAllEquipment();
		for (Map.Entry<Slot, Long> et : eq.entrySet()) {
			spawn.getPersona().addToInventory(et.getValue());
			spawn.getPersona().equip(et.getKey(), et.getValue());
		}
	}

	private void doConnectorSimulator(String simulatorHost, int simulatorPort) throws NetworkException {
		try {
			simulator = new Simulator(simulatorHost, simulatorPort);
			simulator.addListener(this);
			simulator.connectToSimulator();
		} catch (UnresolvedAddressException murle) {
			throw new NetworkException(NetworkException.ErrorType.DNS_ERROR,
					String.format("Could not resolve server hostname %s", simulatorHost), murle);
		}
	}

	public Map<String, String> getURLS() {
		Map<String, String> urls = new HashMap<>();
		LobbyQueryMessageWithReply urlsQuery = new LobbyQueryMessageWithReply("mod.getURL");
		QueryReplyMessage urlsReply = (QueryReplyMessage) simulator.sendMessage(urlsQuery);
		for (Reply row : urlsReply.getReplies()) {
			System.out.println("row:: " + row.getReplyNumber());
			for (String r : row.getStrings()) {
				System.out.println("  " + r);

			}
		}
		return urls;
	}

	public Router getRouter() {
		return router;
	}
}
