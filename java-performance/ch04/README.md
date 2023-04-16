
# 书中章节
```aidl	
第4章	代码性能优化	
4.1　int转String	
4.2　使用Native方法	
4.3　日期格式化	
4.4　switch优化	
4.5　优先使用局部变量	
4.6　预处理	
4.7　预分配	
4.8　预编译	
4.9　预先编码	
4.10　谨慎使用Exception
4.11　批处理	
4.12　展开循环	
4.13　静态方法调用
4.14　高速Map存取	
4.15　位运算	
4.16　反射	
4.17　压缩	
4.18　可变数组
4.19　System.nanoTime()	
4.20　ThreadLocalRandom	
4.21　Base64	
4.22　辨别重量级对象
4.23　池化技术	
4.24　实现hashCode	
4.25　错误优化策略	
    4.25.1　final无法帮助内联	
    4.25.2　subString内存泄漏	
    4.25.3　循环优化	
    4.25.4　循环中捕捉异常	
```

# 主要代码说明

* com.ibeetl.code.ch04.BitTest  位操作性能优化
* com.ibeetl.code.ch04.CharArrayTest  优化小技巧，使用local 变量
* com.ibeetl.code.ch04.DateFormatTest 使用thread local存放线程不安全的SimpleDateFormat
* com.ibeetl.code.ch04.EnumMapTest  使用EnumMap代替普通Map
* com.ibeetl.code.ch04.ForDeadCodeTest 俩个for循环嵌套性能测试，使用Blackhole对象避免JIT优化导致测试无效果
* com.ibeetl.code.ch04.ForIntTest for循环折叠优化。
* com.ibeetl.code.ch04.ForRemove 另外一个for循环折叠优化。
* com.ibeetl.code.ch04.ForTest 嵌套for循环性能误测试。正确结论参考ForDeadCodeTest
* com.ibeetl.code.ch04.IdentityHashMapTest Map必须依赖key的hashcode，IdentityHashMap不需要。
* com.ibeetl.code.ch04.Int2StringTest int转string是个耗时操作，这里有个小技巧。
* com.ibeetl.code.ch04.MessageFormatTest  自己手写一个高性能格式化函数
* com.ibeetl.code.ch04.PreEncodeTest  预编码
* com.ibeetl.code.ch04.PreHandleTest  预处理
* com.ibeetl.code.ch04.RandomTest  随机函数性能优化
* com.ibeetl.code.ch04.ReflectTest  大部分框架都必须拥有的反射优化
* com.ibeetl.code.ch04.StaticMethodCall  静态方法调用比虚方法的性能好一丢丢，而final方法并没有性能改观
* com.ibeetl.code.ch04.SwitchStringTest  switch 优化，这个记得之前在JD的时候，也用过，效果还挺好
* com.ibeetl.code.ch04.SwitchStringTest  switch 优化，这个记得之前在JD的时候，也用过，效果还挺好
* com.ibeetl.code.ch04.TimeTest 时间计时都使用System.nanoTime()
* com.ibeetl.code.ch04.TryCatchTest 使用try catch 代价很低。
* com.ibeetl.code.ch04.ZipTest 压缩性能测试
* com.ibeetl.code.ch04.after6.LotsOfStrings 节约空间的一种办法
* com.ibeetl.code.ch04.HeavyweightObjectTest 识别重量级对象，谨慎使用
* com.ibeetl.code.ch04.PoolTest 使用apache common pool2 复用重量级对象，其他方式还有ThreadLocal，Queue等







# 节选


JDK提供了System.currentTimeMillis()方法用于获取距1970年1月1号的经过时间，精准度为毫秒。该方法返回值的精度与指定的操作系统有关，比如在Windows系统中，有可能误差达到10毫秒。在精确计时的需求下，不应该使用此方法用于时间顺序判断，以及用来度量逝去的时间。以下是一个常规度量时间逝去的方法：

```java
long start = System.currentTimeMillis();
callService();
long end = System.currentTimeMillis();
long time = end-start;
```


由于currentTimeMillis()不精准，很可能导致测试的callService方法消耗的时间也不精准，比如callService方法的性能很稳定，也没有垃圾回收发生，但统计出的time可能不一样。
衡量消耗的时间最好使用System.nanoTime()方法，nanoTime不是现实时间，而是一个虚拟机提供的计时时间，精确到纳秒。用户可以通过修改计算机时间或服务器自动校准时间来影响currentTimeMillis的返回值，但无法修改nanoTime()的返回值。
```java
public class ElapsedTime {
	public static void main(String... args) throws InterruptedException {
		long startTime = System.nanoTime();
		Thread.sleep(1002 * 2);
		long difference = System.nanoTime() - startTime;
		//转化成毫秒
		long millis =  TimeUnit.NANOSECONDS.toMillis(difference);
		//转化成秒
		long seconds =  TimeUnit.NANOSECONDS.toSeconds(difference);
	}
}
```

关于重量级对象节选


```aidl
在代码中，如果把重量级对象当成轻量级对象使用，先创建一个全新的重量级对象在调用其API，，那么API性能会急剧下降。一方面，构造这些重量级对象需要较多初始化过程；另一方面，重量级对象通常在反复使用中会缓存一些中间计算过程，如Beetl会缓存模板解析结果，ObjectMapper会缓存JsonDeserializer到_rootDeserializers中，重量级对象通常包含Map、Set、ThreadLocal这些成员变量。如果每次创建一个新的重量级对象，再调用其API，那么缓存无法生效。
```

```aidl
前面提到的重量级对象的特征是构造复杂，在使用此对象的过程中，对象会缓存中间处理过程，如Jackson的ObjectMapper、Beetl的GroupTemplate，还有Gson序列化工具等。还有一类重量级对象是网络相关的客户端对象，如数据库连接池（下一章会介绍数据库连接池HikariCP），以及Redis客户端Jedis使用JedisPool，它们是重量级对象的原因是建立网络链接是非常耗时的操作，应用希望一旦连接建立后，就会保持此连接。这时就需要使用对象池化技术，对象池化技术提供了对象重用机制。
```