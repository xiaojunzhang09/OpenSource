package com.ibeetl.code.ch03.mutiple;

import com.ibeetl.code.ch03.mutiple.reject.MutiDiscardOldestPolicy;
import com.ibeetl.code.ch03.mutiple.simple.HighAlwaysPolicy;
import com.ibeetl.code.ch03.mutiple.simple.SimpleQos;
import com.ibeetl.code.ch03.mutiple.stat.SimpleStatistics;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) {
		QosQueueWrapper qosQueueWrapper1 = new QosQueueWrapper("1", SimpleQos.fine(), new ArrayBlockingQueue(20));
		QosQueueWrapper qosQueueWrapper2 = new QosQueueWrapper("2", SimpleQos.error(), new ArrayBlockingQueue(20));


		//        QosPolicy qosPolicy = new SimpleQosPolicy();
		QosPolicy qosPolicy = new HighAlwaysPolicy(0);

		qosPolicy.setStatistics(new SimpleStatistics());
		//构造一个总长20个的队列，总长度不应该大于各个队列实际之和
		MultiBlockingQueue<Runnable> mutiQosQueue = new MultiBlockingQueue(40, qosPolicy, qosQueueWrapper1,
				qosQueueWrapper2);

		//        RejectedExecutionHandler rejectedExecutionHandler = new DiscardPolicy();
		RejectedExecutionHandler rejectedExecutionHandler = new MutiDiscardOldestPolicy();
		//        RejectedExecutionHandler rejectedExecutionHandler = new MutiQosQueue.MutiClearPolicy();

		ThreadQosPoolExecutor tp = new ThreadQosPoolExecutor(20, 20, 1, TimeUnit.SECONDS, mutiQosQueue,
				rejectedExecutionHandler);

		new Thread(new Runnable() {
			@Override
			public void run() {

				for (int i = 0; i < 10025; i++) {
					tp.submit(new Task("2", new SampleWorkTask("2", i, 1)));
					tp.submit(new Task("1", new SampleWorkTask("1", i, 1)));
				}


			}
		}).start();
		pause(2000);
		int remain = tp.shutdownNow().size();
		System.out.println(tp.isShutdown() + " remain " + remain);
		//        tp.shutdown();
		System.out.println(mutiQosQueue);
		Statistics statistics = qosPolicy.getStatistics();
		statistics.printStatistics();


	}


	static void pause(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
	}

	static class SampleWorkTask implements Runnable {
		String id;
		int index;
		long pause;

		public SampleWorkTask(String id, int index, long pause) {
			this.id = id;
			this.index = index;
			this.pause = pause;

		}

		@Override
		public void run() {
			System.out.println("run " + id + ":" + index);
			pause(pause);

		}

		public String toString() {
			return id + ":" + index;
		}
	}


}
