package com.ibeetl.code.ch03.mutiple.jmh;

import com.ibeetl.code.ch03.mutiple.*;
import com.ibeetl.code.ch03.mutiple.simple.FastSimpleQosPolicy;
import com.ibeetl.code.ch03.mutiple.simple.SimpleQos;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openjdk.jmh.annotations.Level.Iteration;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(10)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class BaseJmhQosThreadPool {
	protected int threadPoolMax = 60;
	//队列数目
	protected int maxQueueNum = 1000;
	//每个队列的容量
	protected int size = 20;
	//休眠一定纳秒数，避免生产过多，导致消费失败
	protected int sleep = 5;


	//MutilQueue
	protected ThreadQosPoolExecutor mutilPool;

	protected AtomicInteger mutilPoolSuccessTotal;
	protected AtomicInteger mutilPoolFailureTotal;


	@Benchmark
	public int qosPool() {
		String id = new Random().nextInt(maxQueueNum) + "";
		Future future = mutilPool.submit(new Task(String.valueOf(id), new MutilPoolWorker()));
		pause(5);
		return mutilPoolSuccessTotal.get();
	}


	void pause(long time) {
		try {
			TimeUnit.NANOSECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Setup(Iteration)
	public void init() {
		initMutilQosPool();
	}


	@TearDown(Iteration)
	public void close() {
		mutilPool.shutdownNow();
		System.out.println("mutilPoolTpSuccess " + mutilPoolSuccessTotal.get());
		System.out.println("mutilPoolTpFailure " + mutilPoolFailureTotal.get());
		if (mutilPoolFailureTotal.get() != 0) {
			throw new IllegalStateException("出现失败任务，调整sleep,failure total " + mutilPoolFailureTotal.get());
		}

		MultiBlockingQueue<Runnable> mutiQosQueue = (MultiBlockingQueue) mutilPool.getQueue();
		mutiQosQueue.getQosPolicy().getStatistics().printStatistics();

	}


	protected void initMutilQosPool() {

		mutilPoolSuccessTotal = new AtomicInteger();
		mutilPoolFailureTotal = new AtomicInteger();
		List<QosQueueWrapper> queueWrappers = new ArrayList<>();


		//70%正常，20% 告警，10% error
		for (int i = 0; i < maxQueueNum * 0.70; i++) {
			QosQueueWrapper qosQueueWrapper1 = new QosQueueWrapper(String.valueOf(i), SimpleQos.fine(),
					new ArrayBlockingQueue(size));
			queueWrappers.add(qosQueueWrapper1);
		}
		//warn
		for (int i = (int) (maxQueueNum * 0.70); i < maxQueueNum * 0.9; i++) {
			QosQueueWrapper qosQueueWrapper1 = new QosQueueWrapper(String.valueOf(i), SimpleQos.warn(),
					new ArrayBlockingQueue(size));
			queueWrappers.add(qosQueueWrapper1);
		}
		//error
		for (int i = (int) (maxQueueNum * 0.90); i < maxQueueNum; i++) {
			QosQueueWrapper qosQueueWrapper1 = new QosQueueWrapper(String.valueOf(i), SimpleQos.error(),
					new ArrayBlockingQueue(size));
			queueWrappers.add(qosQueueWrapper1);
		}


		QosPolicy qosPolicy = getQosPolicy();
		//        QosPolicy qosPolicy = new HighAlwaysPolicy(0);

		MultiBlockingQueue<Runnable> mutiQosQueue = new MultiBlockingQueue(maxQueueNum * size, qosPolicy,
				queueWrappers.toArray(new QosQueueWrapper[0]));

		RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				mutilPoolFailureTotal.incrementAndGet();
			}
		};

		mutilPool = new ThreadQosPoolExecutor(threadPoolMax, threadPoolMax, 60, TimeUnit.SECONDS, mutiQosQueue,
				rejectedExecutionHandler);

	}

	protected QosPolicy getQosPolicy() {
		return new FastSimpleQosPolicy();
	}


	public class MutilPoolWorker implements Runnable {

		@Override
		public void run() {
			mutilPoolSuccessTotal.incrementAndGet();
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(BaseJmhQosThreadPool.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}

}
