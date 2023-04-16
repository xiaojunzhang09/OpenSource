package com.ibeetl.code.ch03.mutiple.reject;

import com.ibeetl.code.ch03.mutiple.MultiBlockingQueue;
import com.ibeetl.code.ch03.mutiple.ThreadQosPoolExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 清空队列
 */
public class MutiClearPolicy implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		ThreadQosPoolExecutor.MutilFutureTask futureTask = (ThreadQosPoolExecutor.MutilFutureTask) r;
		if (!executor.isShutdown()) {
			MultiBlockingQueue queue = ((MultiBlockingQueue) executor.getQueue());
			queue.clear(futureTask.getTask().getId());
			executor.execute(r);


		}
	}
}