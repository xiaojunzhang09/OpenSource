package com.ibeetl.code.ch03;

/**
 * 错误的单例，但在并发情况下会有哪些错误，分析起来很有意思，能说完所有的可能错误，就说明对并发了解了
 * 《effective java》第一章介绍如何创建单例对象
 * @author 公众号 闲谈java开发
 */
public class Instance {
  static Instance ins = null;
  private Instance(){

  }
  public static Instance instance(){
    if(ins==null){
      ins = new Instance();
      ins.init();
    }
    return ins;
  }

  private void init(){
    // Instance对象初始化
  }

}
