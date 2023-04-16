package com.ibeetl.code.ch03.mutiple.reject;

import com.ibeetl.code.ch03.mutiple.MultiBlockingQueue;
import com.ibeetl.code.ch03.mutiple.ThreadQosPoolExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class DiscardPolicy implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		MultiBlockingQueue queue = ((MultiBlockingQueue) executor.getQueue());
		ThreadQosPoolExecutor.MutilFutureTask task = (ThreadQosPoolExecutor.MutilFutureTask) r;
		System.out.println("discard oldest:" + task.getTask());

	}
}