
# 书中章节
```aidl

第3章 并发编程和异步编程		 
    3.1　不安全的代码	
    3.2　Java并发编程	
        3.2.1　volatile	
        3.2.2　synchronized	
        3.2.3　Lock	
        3.2.4　Condition	
        3.2.5　读写锁	
        3.2.6　Semaphore	
        3.2.7　栅栏	
    3.3　Java并发工具	
        3.3.1　原子变量	
        3.3.2　Queue	
        3.3.3　Future	
        3.3.4　ThreadLocal	
    3.4　Java线程池	
    3.5　异步编程	
        3.5.1　创建异步任务	
        3.5.2　任务完成后执行回调	
        3.5.3　串行执行	
        3.5.4　并行执行	
        3.5.5　接收任务处理结果	

```

# 主要代码说明

```
com.ibeetl.code.ch03.SimpleDateFromatTest 日期格式化并发问题
com.ibeetl.code.ch03.ThreadDeadTest 死锁例子，可以通过jstack,jvisualvm 观察死锁
com.ibeetl.code.ch03.ThreadTest1 一个死循环，可以通过jstack,jvisualvm 观察死锁
com.ibeetl.code.ch03.MyArrayBlockingQueue 使用lock 创建一个队列
com.ibeetl.code.ch03.Cache 一个缓存的简单例子
com.ibeetl.code.ch03.Instance 一个错误的单例实现，在我的window机器上，运行10万到100万次就会出现重排序，mac上运行千万次都未出现
com.ibeetl.code.ch03.ResortTest 从排序过程演示
com.ibeetl.code.ch03.cpucache.CPUCacheErrorTest cpu cach演示
com.ibeetl.code.ch03.cpucache.CPUCacheFix[01..05] 解决cpu cache的多种办法
com.ibeetl.code.ch03.FutureTest 多线程协作 CompletableFuture
com.ibeetl.code.ch03.event.EventBusTest 多线程协作，限于篇幅，书中未写的EventBus，适合事件驱动系统
com.ibeetl.code.ch03.mutiple.Main 限于篇幅，书中未写的MutipleQueue,用一个线程池管理多个队列,运行后可以看到高优先级队列优先被线程池处理
com.ibeetl.code.ch03.mutiple.jmh.JmhLargeNumQueue 限于篇幅，书中未写的MutipleQueue,性能测试一个线程池管理多大5万个队列的的性能


```


# 节选


再举一个线程不安全的代码的例子，在单例模式中，允许一些重量级对象延迟创建，比如数据库连接池，或者微服务中的服务列表的加载。这些对象只有在第一次访问的时候创建，代码如下：

```java
public class Instance {
  static Instance ins = null;
  private Instance(){}

  public static Instance instance(){
    if(ins==null){
      ins = new Instance();
      ins.init();
    }
    return ins;
  }

  private void init(){
    //Instance对象初始化
  }
}
```


当调用instance方法时，首先会检测ins是否已经创建，如果没有创建，则会创建ins实例，并且调用init方法。这部分代码在单线程下运行良好，但多线程下，会有各种问题。
第一个问题是在多线程访问下，有可能获取还未被正确初始化完毕的示例对象。在T4时刻，线程B得到一个没有初始化完毕的对象，如下表所示。

| 时　间 | 线程A                 | 线程B                                                        |
| ------ | --------------------- | ------------------------------------------------------------ |
| T1     | if(ins==null)         |                                                              |
| T2     | ins = new Instance(); |                                                              |
| T3     |                       | if(ins==null)，此时ins在T2时间被线程A构造，所以直接返回           |
| T4     |                       | 线程B获取一个还未被初始化的Instance对象（未调用init方法），错误 |
| T5     | ins.init()            |                                                              |

还有一种情况，Instance对象可能被构造多次，T2时刻调用构造函数，对于虚拟机来说，实际上分为三个指令：

* memory = allocate(); //指令1：分配对象的内存空间
* ctorInstance(memory); //指令2：初始化对象
* ins = memory; //指令3：设置Instance指向刚分配的内存地址

因此在构造ins的时候，对于线程B来说，仍然可能检测为空，从而再次创建了一个新的实例，如下表T3时刻所示，线程B会重复创建对象。

| 时　　间 | 线程A                   | 线程B                    |
| -------- | ----------------------- | ------------------------ |
| T1       | if(ins==null)//返回true |                          |
| T2       | memory = allocate();    |                          |
| T3       | ctorInstance(memory);   | if(ins==null)//返回true  |
| T4       | ins = memory;           | memory = allocate();     |
| T5       |                         | ctorInstance(memory);    |
|          |                         | ins = memory; //重复创建 |

在现代系统中，为了提高系统性能，有可能对指令进行重排序，例如以上构造对象的3个指令，有可能对2和3指令进行重排序。
重排序可能会使线程B得到另外一个结果，一个还未被构造完毕的Instance对象，从而产生类似第一个问题。在T6时刻，线程B使用了一个未初始化完毕的对象，如下表所示。


| 时　　间 | 线程A                             | 线程B                                     |
| -------- | --------------------------------- | ----------------------------------------- |
| T1       | if(ins==null)                     |                                           |
| T2       | memory = allocate();              |                                           |
| T3       | ins = memory; //重排序            |                                           |
| T4       |                                   | if(ins==null)                             |
| T5       |                                   | 不为空，线程A在T3时创建，但还未初始化完毕 |
| T6       |                                   | 错误地使用了未初始化实例                  |
| T7       | ctorInstance(memory);//初始化完成 |                                           |



# 

# 关于mutiple queue说明

篇幅有限，书中没有提到，这里补充说明一下

当需要异步处理一种业务逻辑，按照此业务分类，有不同的优先级异步处理策略，比如

* 异步的分布式调用，根据网络情况，故障节点分发较少请求，直到故障恢复
* 某些业务类型高优先级的异步处理，低优先级业务必须在高优先级处理完毕后才能处理。或者其他处理策略

通常最简单的方式是根据业务种类，使用多个线程池，为每个业务种类分配不同的线程池，缺点是线程池不能无限分配，且qos策略，线程池策略很难统一调整。因此可以使用**MutiBlockingQueue**

将多线程池结构替换成**多队列一个线程池**,并允许设置qos，可以扩展QosPolicy来决定如何获取任务，放弃任务，提供 Statistics来统计QosPolicy执行效果

* 运行Main 了解MutiQueue
* 运行JmhThreadSinglePool了解其性能
* 运行JmhLargeNumQueue 从100个队列到50000个队列的性能

主要的类如下说明

* QosPolicy， Qos策略类，用来实现不同Qos，系统提供SimpleQosPolicy，FastSimpleQosPolicy，HighAlwaysPolicy.基于计数的Qos
* Qos ，用来表达优先级，默认提供SimpleQos
* Statistics, 统计Qos执行效果，系统提供DefaultStatistics，SimpleStatistics，和基于时间的TimelineStatistics
* MutiBlockingQueue 系统核心类，用来封装多个队列，并将任务委托给QosPolicy
* ThreadQosPoolExecutor 继承了jdk的ThreadExecutor