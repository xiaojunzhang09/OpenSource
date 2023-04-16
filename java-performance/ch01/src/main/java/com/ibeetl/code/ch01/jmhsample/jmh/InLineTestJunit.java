package com.ibeetl.code.ch01.jmhsample.jmh;

import org.junit.Assert;
import org.junit.Test;

/**
 * 所有的jmh测试，都应该写一个单元测试，保证功能正确先。
 * 比如。一个非常棒的模板引擎单元测试，各个模板开源都使用他。 https://github.com/xiandafu/jte-template-benchmark/blob/master/src/test/java/com/mitchellbosecke/benchmark/ExpectedOutputTest.java
 * @author 公众号 闲谈java开发
 */
public class InLineTestJunit {
  @Test
  public void test(){
    //Inline是jmh测试，需要验证add和addInline性能之前，先保证俩个方法单元测试通过
    Inline inline = new Inline();
    inline.init();
    int expectd = inline.x+inline.y;
    int ret = inline.add();
    int ret2 = inline.addInline();
    Assert.assertEquals(expectd,ret);
    Assert.assertEquals(expectd,ret2);
  }
}
