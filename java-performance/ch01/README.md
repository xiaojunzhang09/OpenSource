**提升系统性能最好的方式是代码优化，没有使用JMH程序，性能优化不会成功**

# 书中章节
```
第1章 Java代码优化	
1.1　可优化的代码	
1.2　性能监控	
1.3　JMH
    1.3.1　使用JMH	
    1.3.2　JMH常用设置	
    1.3.3　注意事项	
    1.3.4　单元测试	
```


# 主要代码说明

* com.ibeetl.code.ch01.jmh.MyBenchmark  使用jmh对比俩种方式的性能
* com.ibeetl.code.ch01.nojmh.PerformanceAreaTest  使用非jmh方式
* com.ibeetl.code.ch01.jmhsample.jmh 包含了几个常见使用jmh容易出错的地方


# 节选

```
   架构师在优化Java系统性能的过程中，可以做出很多重要决策以全面提升系统的性能。例如使用更高版本的JDK，引入Redis或Redis+JVM缓存，甚至考虑将JVM缓存分成多级，比如热点缓存+普通数据缓存等。
在数据上可以考虑数据库分库分表或一主多从，考虑引入中间件提供表的路由。引入分布式事务管理器或状态机来保证事务一致。对于大数据的查询，可以考虑用Elasticsearch或Hive大数据系统建立统一的数据查询接口。架构师需要考虑如何把数据库的数据同步到大数据系统，以及Redis缓存中。
系统交互上使用消息中间件实现异步通信，也可以使用RPC进行远程调用。架构师还可以把单体系统改成微服务系统，这种架构的改变“牵一发而动全身”。
一个千人研发团队，通常只有十几位架构师，因而架构级别的调整掌握在少数架构师手里。千人研发团队就有千位普通程序员，作为一个普通程序员，很少有机会参与系统架构级别的优化，甚至暂时不能理解架构上的调整。在开发新功能或审查组内的代码时，优化系统的方式主要是优化自己或他人写的代码。代码是系统的基石，没有良好的代码，系统架构就不牢固。 
本章通过一个代码片段来说明代码的优化过程，为后续各章提供系统优化指南。

```


```aidl
我们可以修改MyBenchmark，添加需要测试的代码。现在可以创建一个性能测试的jar文件，运行以下Maven命令：

mvn clean install

该命令会在target目录下生成一个benchmarks.jar，包含运行性能测试所需的任何文件。在命令行中运行以下命令：

java -jar target/benchmarks.jar  MyBenchmark

JMH将启动，默认情况下运行MyBenchmark类中的所有被@Benchmark标注的方法。
可以通过命令行调整JMH参数：
	-wi 5：预热5次；
	-i 5 -r 3s：运行5次，每次3秒；
	-f：进程数；
	运行特定的测试，可以是具体类名，也可以是.*Benchmark.*这样的正则表达式，比如.*Benchmark.*，运行所有类名或方法名带有“Benchmark”的方法；
	-t：线程数；
	-bm：测试模式，如thrpt、avgt、sample、all。

$ java -jar target/microbenchmarks.jar -f 1 -wi 5 -i 5 -r 3s .*Benchmark.*

更多的参数可以查看org.openjdk.jmh.runner.options.CommandLineOptions构造函数


```