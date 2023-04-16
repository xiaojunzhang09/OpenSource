package com.ibeetl.code.ch03.mutiple;

import java.util.concurrent.*;

public class ThreadQosPoolExecutor extends ThreadPoolExecutor {
	public ThreadQosPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}


	@Override
	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new MutilFutureTask<T>(runnable, value);
	}

	public static class MutilFutureTask<V> extends FutureTask<V> {
		Task task = null;

		public MutilFutureTask(Runnable runnable, V result) {
			super(runnable, result);
			task = (Task) runnable;
		}

		public Task getTask() {
			return task;
		}

		public String toString() {
			return task.toString();
		}
	}

}
