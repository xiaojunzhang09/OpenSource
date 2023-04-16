package com.ibeetl.code.ch03;

import java.util.HashMap;
import java.util.Map;

/**
 *  模拟死循环，jstack 观察线程栈，或者使用jvisualvm 查看calc-thread线程的状态
 *  @author xiandafu ,公众号 闲谈java开发
 */
public class ThreadTest1 {
  static int max = 10;
  public static void  main(String[] args){
    Thread thread =  new Thread("calc-thread"){
      public void run(){
        while(true){
          Map map = readData();
          //模拟手误，应该是!map.isEmpty()
          if(map.isEmpty()){
            break;
          }
        }
      }
    };
    thread.start();
  }

  public static Map readData(){

    Map map = new HashMap();
    for(int i=0;i<1000;i++){
      map.put(i," value-"+i);
    }
//    sleep(100);
    return map;
  }

  public static void sleep(int time){
    try {
      Thread.sleep(time);
    }catch(Exception ex){

    }

  }

}
