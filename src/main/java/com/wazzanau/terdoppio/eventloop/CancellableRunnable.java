package com.wazzanau.terdoppio.eventloop;


public abstract class CancellableRunnable implements Runnable {
	
	private volatile boolean cancelled;
	private volatile boolean cancellable;
	private volatile boolean done;

	public CancellableRunnable() {
		cancelled = false;
		cancellable = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public boolean isDone() {
		return done;
	}

	public synchronized boolean cancel() {
		if (cancellable) {
			cancelled = true;
			cancellable = false;
			done = true;
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (!cancelled) {
			cancellable = false; // this is set before calling run, to prevent the race in succeeding to cancel, but run is ran nonetheless.
			cancelled = false;
			cancellableRun();
			done = true;
		}
		
		postExecute();
	}
	
	/**
	 * Guaranteed to be called before cancellableRun(), default implementation does nothing
	 */
	protected void preExcecute() {
		// does nothing by default
	}
	
	protected abstract void cancellableRun();
	
	/**
	 * Guaranteed to be called after cancellableRun() default implementation does nothing
	 */
	protected void postExecute() {
		// does nothing by default
	}
}
