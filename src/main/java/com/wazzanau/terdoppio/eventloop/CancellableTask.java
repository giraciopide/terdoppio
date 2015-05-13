package com.wazzanau.terdoppio.eventloop;

public interface CancellableTask {
	public boolean isCanceled();
	public boolean isDone();
	boolean cancel(boolean mayInterruptIfRunning);
}
