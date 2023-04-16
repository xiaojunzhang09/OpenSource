
# 书中章节
```aidl	
第7章　JIT优化	
    7.1　编译Java代码	
    7.2　处理语法糖	
    7.3　解释执行和即时编译	
    7.4　C1和C2	
    7.5　代码缓存	
    7.6　JITWatch	
    7.7　内联	
    7.8　虚方法调用	

```

# 主要代码说明

* com.ibeetl.code.ch07.InlineTest  内联性能测试
* com.ibeetl.code.ch07.VTableTest  虚方法调用
* com.ibeetl.code.ch07.ClientTest  看看当JVM只使用解释执行的时候性能，应该和python和php差不多
* com.ibeetl.code.ch07.HelloWorld  分析JIT日志的一个程序
* com.ibeetl.code.ch07.VirtualCallTest  一个虚方法优化后再退出优化的例子


# 节选1

7.2　处理语法糖

Java在编译的时候通过com.sun.tools.javac.comp.Lower处理语法糖，下面列举了Java常用的语法糖。
1）条件编译
如下代码中FLAG是final类型，因此编译后可以省去这个代码：
```java

static final boolean  FLAG = false;
public void run(){
  if(FLAG){
    System.out.println("hello");
  }
}
```
通过com.sun.tools.javac.comp.Lower.visitIf来处理：
编译结果如下，run方法是个空方法：
```
public void run() {
}
```

如果了解com.sun.tools.javac.comp.Lower.visitIf源码，则可以在visitIf代码中打上断点，并修改第一节中的CompileString的getSouce方法，代码如下：
```
public static String getSource() {
        return " public class Test {"
                +"static final boolean  FLAG = false;" 
                + " public void run(){" 
                + "  if(FLAG){"
                + "    System.out.println(\"hello\");" 
                + "  }" 
                + "}"
                + "}";
    }
```
以Debug方式运行CompileString，可以看到JavaCompile进入了visitIf代码的cond.type.isFalse()分支：
```
public void visitIf(JCIf tree) {
  JCTree cond = tree.cond = translate(tree.cond, syms.booleanType);
  if (cond.type.isTrue()) {
    result = translate(tree.thenpart);
    addPrunedInfo(cond);
  } else if (cond.type.isFalse()) {
    if (tree.elsepart != null) {
      result = translate(tree.elsepart);
    } else {
      result = make.Skip();
    }
    addPrunedInfo(cond);
  } else {
    //Condition is not a compile-time constant.
    tree.thenpart = translate(tree.thenpart);
    tree.elsepart = translate(tree.elsepart);
    result = tree;
  }
}
```
JCTree是AST的抽象类，JCIf是其子类，本书限于篇幅没有更深入地讲解Javac的完整过程，本书介绍了通过Debug代码CompileString来学习Java如何编译源码的方法

# 节选2

码经过JIT编译后，会放入一个叫代码缓存（Code Cache）的地方。在JDK8 32位机器上、client模式下，代码缓存的固定大小为32MB，在64位机器上，代码缓存的大小为240MB。代码缓存对性能的影响非常大，如果缓存不够，那么一些优化后的代码不得不被清空以让其他优化代码进入代码缓存。
可以通过XX:+PrintFlagsFinal来打印平台所有参数的默认值，比如，笔者的Mac机器上有如下输出：
```
InitialCodeCacheSize                      = 2555904
ReservedCodeCacheSize                     = 251658240  
CodeCacheExpansionSize                    = 65536
```

代码缓存默认的初始化大小为2555904个字节，每次增长6536个字节，代码缓存大小为251658240个字节。
-XX:+PrintCodeCache用于打印代码缓存的使用情况，以下是在程序退出时将代码缓存的使用情况打印到控制台：
```
CodeCache: size=245760Kb used=1128Kb max_used=1147Kb free=244632Kb
bounds [0x0000000106e00000, 0x0000000107070000, 0x0000000115e00000]
total_blobs=291 nmethods=35 adapters=170
compilation: enabled
```
size表示代码缓存的大小，这并不是实际使用值，而是一个最大值，used表示实际占用的内存大小，max_used比used大，表示实际占用的内存大小，需要参考这个指标作为设定Code Cache大小的依据，free是size-used的值。
当代码缓存满的时候，JIT通常会清理一部分Code Cache，使用UseCodeCacheFlushing来进行控制：
```
UseCodeCacheFlushing = true
```
可以使用XX:-UseCodeCacheFlushing关闭自动清理，这样JIT将停止编译新的代码。
