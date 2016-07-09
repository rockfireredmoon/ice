/**
 * Copyright (C) 2014-2016 Emerald Icemoon (emerald.icemoon@gmail.com)
 * 
 * License: http://www.gnu.org/licenses/gpl.html GPL version 3 or higher
 */
package org.icelib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QueueExecutor extends ThreadPoolExecutor {

	public static class DaemonThreadFactory implements ThreadFactory {
		private final String name;

		public DaemonThreadFactory(String name) {
			this.name = name;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, name);
			thread.setDaemon(true);
			return thread;
		}
	}

	public interface Listener {

		void submitted(QueueExecutor queue, Runnable r);

		void executed(QueueExecutor queue, Runnable r);
	}

	public interface QueueTask {

	}

	public class QueueFutureTask<V> extends FutureTask<V> {
		private Callable<V> callable;
		private Runnable runnable;

		public QueueFutureTask(Callable<V> callable) {
			super(callable);
			this.callable = callable;
		}

		public QueueFutureTask(Runnable runnable, V result) {
			super(runnable, result);
			this.runnable = runnable;
		}

		@Override
		public String toString() {
			return callable == null ? runnable.toString() : callable.toString();
		}
	}

	private int total;
	private List<Listener> listeners = new ArrayList<>();

	public QueueExecutor() {
		this("Queue");
	}

	//
	public QueueExecutor(String name) {
		super(1, 1, 0, TimeUnit.DAYS, new LinkedBlockingQueue<Runnable>(), new DaemonThreadFactory(name));
	}

	public QueueExecutor(int threads) {
		super(1, threads, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public int getTotal() {
		return total;
	}

	public void addListener(Listener l) {
		listeners.add(l);
	}

	public void removeListener(Listener l) {
		listeners.remove(l);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new QueueFutureTask<>(runnable, value);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new QueueFutureTask<>(callable);
	}

	@Override
	public void execute(Runnable command) {
		super.execute(command);
		total++;
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).submitted(this, command);
		}
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		total--;
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).executed(this, r);
		}
	}
}
