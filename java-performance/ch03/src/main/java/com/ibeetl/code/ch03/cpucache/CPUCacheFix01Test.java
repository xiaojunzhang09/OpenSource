package com.ibeetl.code.ch03.cpucache;

/**
 * 一个经典的多线程编程陷阱，由于stop可能从cpu缓存里去读，主线程通过设置stop并不能停止线程B
 * 可以使用volatile保证可见性
 * @author 公众号 闲谈java开发
 */
public class CPUCacheFix01Test {
	private static volatile   boolean stop = false;

	public static void main(String[] args){

		Thread a = new Thread("B"){
		  public void run(){
			  while (!stop) {


			  }
			System.out.println("exit b");
		  }
		};
		System.out.println("start");
    	a.start();
		pause(100);
		stop = true;
		System.out.println("exit");
  }

  public static synchronized void check(int a ){
	  return ;
  }
  public static void pause(int time){
    try {
      Thread.sleep(time);
    }catch(Exception ex){
    }
  }

  static class Tester{
		final int b ;
		public Tester(int a){
			b =a;
		}
		public synchronized  int get(){
			return b;
		}
  }
}
