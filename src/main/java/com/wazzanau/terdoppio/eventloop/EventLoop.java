package com.wazzanau.terdoppio.eventloop;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLoop<E> {

	public interface Handler<E> {
		public void handle(E event);
	}

	private final static Logger logger = LoggerFactory.getLogger(EventLoop.class);

	private final BlockingQueue<E> events;
	private volatile Thread eventHandlingThread = null;
	private final Handler<E> handler;
	private ScheduledExecutorService schedExecutor;

	public EventLoop(Handler<E> handler) {
		events = new LinkedBlockingDeque<E>();
		this.handler = handler;
	}

	public synchronized void start() {
		schedExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {		
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("evtLoopSchedThread");
				return t;
			}
		});

		Runnable evtLoop = new Runnable() {
			@Override
			public void run() {

				while (true) {
					try {
						// dequeue one event and handle it
						E event = events.take();

						if (handler != null) {
							try {
								handler.handle(event);
							} catch (Exception e) {
								// we don't want a bad handler to mess with us
								logger.warn("Exception in event loop handler: " + e.getMessage(), e);
							}
						}

					} catch (InterruptedException e) {
						logger.info("Event loop handling thread interrupted");
						break;

					} finally {
						EventLoop.this.stop(); // will stop the sched thread and will cleanup the events in the queue, if any
					}
				}

				logger.info("Event loop handling thread terminated");
			}
		};

		eventHandlingThread = new Thread(evtLoop);
		eventHandlingThread.setName("evtLoopHndThread");
		eventHandlingThread.start();
	}



	public synchronized void stop() {
		if (eventHandlingThread != null || schedExecutor != null) {
			throw new IllegalStateException("Cannot stop an event loop that has not been started");
		}

		if (eventHandlingThread != null) {
			eventHandlingThread.interrupt();
			eventHandlingThread = null;
		}

		if (schedExecutor != null) {
			schedExecutor.shutdown();
			schedExecutor = null;
		}

		events.clear();
	}

	public boolean putEvent(E event) {
		return events.offer(event);
	}

	public CancellableTask schedule(final E event, long delay, TimeUnit unit) {
		final CancellableRunnable cancRun = new CancellableRunnable() {
			@Override
			public void cancellableRun() {
				putEvent(event);
			}
		};

		final ScheduledFuture<?> fut = schedExecutor.schedule(cancRun, delay, unit);

		return new CancellableTask() {
			@Override
			public boolean isCanceled() {
				return fut.isCancelled() && cancRun.isCancelled();
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				boolean futCanc = fut.cancel(mayInterruptIfRunning);
				boolean taskCanc = cancRun.cancel();
				return futCanc || taskCanc;
			}

			@Override
			public boolean isDone() {
				return fut.isDone() && cancRun.isDone();
			}
		};
	}

	private final ConcurrentMap<String, CancellableTask> namedTasks = new ConcurrentHashMap<String, CancellableTask>();

	/**
	 * Named task are reschedulable tasks identified by their name. Scheduling a new "named" will cancel all the previously
	 * scheduling task with the same name.
	 * @param taskName
	 * @param event
	 * @param delay
	 * @param unit
	 * @return
	 */
	public synchronized CancellableTask scheduleNamedTask(String taskName, final E event, long delay, TimeUnit unit) {
		
		
		
		final CancellableRunnable cancRun = new CancellableRunnable() {
			@Override
			public void cancellableRun() {
				putEvent(event);
			}
			
			@Override
			protected void postExecute() {
				namedTasks.remove(taskName);
			}
		};

		final ScheduledFuture<?> fut = schedExecutor.schedule(cancRun, delay, unit);

		final CancellableTask ctask = new CancellableTask() {
			@Override
			public boolean isCanceled() {
				return fut.isCancelled() && cancRun.isCancelled();
			}

			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				boolean futCanc = fut.cancel(mayInterruptIfRunning);
				boolean taskCanc = cancRun.cancel();
				return futCanc || taskCanc;
			}

			@Override
			public boolean isDone() {
				return fut.isDone() && cancRun.isDone();
			}
		};
		
		return ctask;
	}


}
