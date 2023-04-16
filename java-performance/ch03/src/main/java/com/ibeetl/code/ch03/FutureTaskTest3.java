package com.ibeetl.code.ch03;

import com.ibeetl.code.ch03.pool.PoolManager;
import com.ibeetl.code.ch03.pool.QueryTaskThreadPoolExecutor;

import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * 一个简单使用实例，必须注意，线程池是宝贵资源，必须统一管理（或者像spring那样统一配置）
 * @author 公众号 闲谈java开发
 */
public class FutureTaskTest3 {
	public static void main(String[] args) throws Exception {

		ThreadPoolExecutor nodeTaskPool = new ThreadPoolExecutor(
				5,
				10,
				1,
				TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(100)
				);

		ThreadPoolExecutor shadowTaskPool = new ThreadPoolExecutor(
				5,
				10,
				1,
				TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(100)
		);

		ThreadPoolExecutor compareTaskPool = new ThreadPoolExecutor(
				5,
				10,
				1,
				TimeUnit.MINUTES,
				new ArrayBlockingQueue<Runnable>(100)
		);




		System.out.println(Thread.currentThread());
		int a=3,b=4,c=6,d=11;


		Future<Integer> future1 = shadowTaskPool.submit(new Callable<Integer>() {
			@Override
			public Integer call() {
				System.out.println("future1 "+Thread.currentThread());
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return a+b;
			}
		});


		Future<Integer> future2 = nodeTaskPool.submit(new Callable<Integer>() {
			@Override
			public Integer call() {
				try {
					System.out.println("future2 "+Thread.currentThread());
					TimeUnit.MILLISECONDS.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return c+d;
			}
		});




		int ack1 = 0;
		int ack2 = 0;
		try{
			ack1 = future1.get(40,TimeUnit.MILLISECONDS);
			future2.cancel(true);
		}catch(TimeoutException timeoutException){
			System.out.println("use future2 ");
			ack2 = future2.get();
			future1.cancel(true);
			//再试一次
//      try{
//        ack1 = future1.get(20,TimeUnit.MILLISECONDS);
//      }catch(TimeoutException exception){
//        //取消，
//        future1.cancel(true);
//      }

		}

		if(future1.isDone()&&future2.isDone()){
			//比较俩个值
			System.out.println("compare ");
			compare(future1.get(),future2.get());
		}



		System.out.println("ack1 "+ack1);
		System.out.println("ack2 "+ack2);

		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}




	}

	static void compare(int a,int b){

	}

	static class NodeServer implements Callable<Boolean> {


		@Override
		public Boolean call() throws Exception {
			TimeUnit.MILLISECONDS.sleep(10);
			return true;
		}

	}

	static class ShadowServer implements Callable<Boolean> {

		@Override
		public Boolean call() throws Exception {
			TimeUnit.MILLISECONDS.sleep(100);
			return false;
		}

	}
}
