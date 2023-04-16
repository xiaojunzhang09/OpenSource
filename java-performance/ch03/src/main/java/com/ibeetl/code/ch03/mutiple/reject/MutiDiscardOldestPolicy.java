package com.ibeetl.code.ch03.mutiple.reject;

import com.ibeetl.code.ch03.mutiple.MultiBlockingQueue;
import com.ibeetl.code.ch03.mutiple.ThreadQosPoolExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 最常用的，放弃一个元素，如果当前队列是高优先级，且未满，则放弃一个低优先级的队列
 */
public class MutiDiscardOldestPolicy implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		ThreadQosPoolExecutor.MutilFutureTask futureTask = (ThreadQosPoolExecutor.MutilFutureTask) r;
		if (!executor.isShutdown()) {
			MultiBlockingQueue queue = ((MultiBlockingQueue) executor.getQueue());

			ThreadQosPoolExecutor.MutilFutureTask oldest = (ThreadQosPoolExecutor.MutilFutureTask) queue
					.poll(futureTask.getTask().getId());
			executor.execute(r);


		}
	}
}