package com.ibeetl.code.ch03.mutiple.jmh;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openjdk.jmh.annotations.Level.Iteration;

/**
 * 验证jdk线程池和mutilqueue线程池的性能
 * 1）通过jmh表输出查看 添加任务性能
 * 2) 通过SuccessTotal和FailureTotal查看 处理数量
 * 20并发线程
 * ThreadPool.jdk      avgt       1577.741          us/op
 * ThreadPool.qosPool  avgt       1582.447          us/op
 *
 *
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
@Threads(50)
@Fork(1)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class JmhThreadSinglePool extends BaseJmhQosThreadPool {

	//单线程池
	ThreadPoolExecutor jdkTp;
	AtomicInteger jdkTpSuccessTotal;
	AtomicInteger jdkTpFailureTotal;

	//多线程池
	ThreadPoolExecutor[] mutlJdkTp;
	AtomicInteger mutlJdkTpSuccessTotal;
	AtomicInteger mutlJdkTpFailureTotal;


	/**
	 * jdk线程池
	 * @return
	 */
	@Benchmark
	public int jdk() {
		Future future = jdkTp.submit(new JdkPoolWorker());
		pause(sleep);
		return jdkTpSuccessTotal.get();
	}

	/**
	 * 多个jdk线程池
	 * @return
	 */
	@Benchmark
	public int mutilPoolJdk() {
		ThreadPoolExecutor tp = mutlJdkTp[new Random().nextInt(20)];
		Future future = tp.submit(new JdkPoolWorker());
		pause(sleep);
		return mutlJdkTpSuccessTotal.get();
	}


	@Setup(Iteration)
	public void init() {
		//        this.maxQueueNum =5000;
		super.init();
		initJdkPool();
		initJdkMutilPool();

	}


	protected void initJdkPool() {
		jdkTpSuccessTotal = new AtomicInteger();
		jdkTpFailureTotal = new AtomicInteger();

		LinkedBlockingQueue queue = new LinkedBlockingQueue(1000);
		RejectedExecutionHandler jdkPoolRejectedExecutionHandler = new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				jdkTpFailureTotal.incrementAndGet();
			}
		};
		jdkTp = new ThreadPoolExecutor(threadPoolMax, threadPoolMax, 60, TimeUnit.SECONDS, queue,
				jdkPoolRejectedExecutionHandler);

	}

	protected void initJdkMutilPool() {
		mutlJdkTp = new ThreadPoolExecutor[maxQueueNum];
		mutlJdkTpSuccessTotal = new AtomicInteger();
		mutlJdkTpFailureTotal = new AtomicInteger();
		RejectedExecutionHandler jdkMutlPoolRejectedExecutionHandler = new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				mutlJdkTpFailureTotal.incrementAndGet();
			}
		};

		for (int i = 0; i < maxQueueNum; i++) {
			LinkedBlockingQueue queue = new LinkedBlockingQueue(size);
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadPoolMax, threadPoolMax, 60,
					TimeUnit.SECONDS, queue, jdkMutlPoolRejectedExecutionHandler);
			mutlJdkTp[i] = threadPoolExecutor;
		}


	}

	@TearDown(Iteration)
	public void close() {
		System.out.println("Shutdown");
		if (jdkTp != null) {
			jdkTp.shutdownNow();
			System.out.println("jdkTpSuccess " + jdkTpSuccessTotal.get());
			System.out.println("jdkTpFailure " + jdkTpFailureTotal.get());
		}

		if (mutilPool != null) {
			mutilPool.shutdownNow();

			System.out.println("mutilPoolTpSuccess " + mutilPoolSuccessTotal.get());
			System.out.println("mutilPoolTpFailure " + mutilPoolFailureTotal.get());
			;
		}

		if (mutlJdkTp != null) {
			for (int i = 0; i < mutlJdkTp.length; i++) {
				mutlJdkTp[i].shutdownNow();
			}

			System.out.println("mutilPoolTpSuccess " + mutlJdkTpSuccessTotal.get());
			System.out.println("mutilPoolTpFailure " + mutlJdkTpFailureTotal.get());
		}


	}

	class JdkPoolWorker implements Runnable {

		@Override
		public void run() {
			jdkTpSuccessTotal.incrementAndGet();
		}
	}


	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(JmhThreadSinglePool.class.getSimpleName()).forks(1).build();
		new Runner(opt).run();
	}
}
