
# 书中章节
```aidl	
第5章 高性能工具
    5.1　高速缓存Caffeine	
        5.1.1　安装Caffeine	
        5.1.2　Caffeine的基本使用方法	
        5.1.3　淘汰策略	
        5.1.4　statistics功能	
        5.1.5　Caffeine高命中率	
        5.1.6　卓越的性能	
    5.2　映射工具Selma	
    5.3　JSON工具Jackson	
        5.3.1　Jackson的三种使用方式	
        5.3.2　Jackson树遍历	
        5.3.3　对象绑定	
        5.3.4　流式操作	
        5.3.5　自定义JsonSerializer	
        5.3.6　集合的反序列化	
        5.3.7　性能提升和优化	
    5.4　HikariCP	
        5.4.1　安装HikariCP	
        5.4.2　HikariCP性能测试	
        5.4.3　性能优化说明	
    5.5　文本处理工具Beetl	
        5.5.1　安装和配置	
        5.5.2　脚本引擎	
        5.5.3　Beetl的特点	
        5.5.4　性能优化	
    5.6　MessagePack	
    5.7　ReflectASM	
```

# 主要代码说明

* com.ibeetl.code.ch05.JacksonSample  Jackson使用的三种方式，对象绑定，底层的Node,跟底层的Parser，这种设计非常好
* com.ibeetl.code.ch05.CloneUtilTest  克隆对象的正确姿势
* com.ibeetl.code.ch05.BeetlSample  我自己开源的Beetl例子，演示了定制语法(while)的功能
* com.ibeetl.code.ch05.PackTest  MessagePack的例子，很多序列化工具都会具备一定压缩功能，虽然性能上降低，但压缩后的对象存放redis，网络上传输都划算
* com.ibeetl.code.ch05.caffeine.CaffeineApplicationTests Caffeine通常使用例子，也包括自定义Expire，使用FakeTicker调试高级功能


# 节选

```aidl

本节介绍现代系统中重要的组成部分—缓存。说到缓存，就要说一下现代计算机CPU的组成：CPU除中央处理器外还有一级缓存和二级缓存，甚至三级缓存；在CPU中，缓存的作用是弥补低速外部存储和高速处理的CPU之间不匹配的缺陷。在现代系统中，我们面对的是高并发快速响应的需求目标，但一直横亘在我们面前的难题是DataBase的速度不能大幅度地提升，也就无法实现目标的快速响应。借鉴现代计算机结构中的解决办法，在系统中开始引入缓存。
Caffeine的主要作者是Ben Manes，Ben是Google的前成员，也是ConcurrentLinkedHashMap的数据结构作者。Caffeine的开发原因是Ben想用Java8重写Guava Cache库，因此API在设计上与Guava Cache几乎一致，并且提供了Guava Cache的适配器，使得Guava Cache可以使用Caffeine，从而极为平滑地从Guava Cache迁移至Caffeine。
虽然说是重写了Guava Cache，但与Guava Cache使用了不同的设计，Caffeine使用了更先进的算法（Window-TinyLFU）和更优秀的数据结构（Striped Ring Buffer、TimeWheel），带来了极为灵活的配置、超强的性能，以及高命中率（最接近Optimal的命中率）。Caffeine不是一个分布式缓存，也不支持持久化。可能有人会说已经有了Redis、Memcached这些更好用的缓存，为什么依然要使用它呢？因为在一个系统中，我们设计缓存部分的时候，为了获得更好的性能和稳定性，不能只考虑分布式缓存，还应该考虑用Caffeine实现一级缓存，甚至是虚拟机内的多级缓存。
```

```aidl


MessagePack通常用来代替JSON，可以高效地传输数据和存储数据，它比JSON更紧凑，是一种二进制序列化格式，编码更精简高效。比如小整数被编码为单个字节，典型的短字符串除字符串本身外只需要一个额外的字节。
MessagePack主要用于结构化数据的缓存和存储：
（1）存在于Redis、Memcache中，因为它比JSON小，可以节省一些内存。
（2）持久化到数据库（NoSQL）中。
以下JSON数据占用了27个字节：

{"compact":true,"schema":0}

MessagePack则用18个字节存储上面的JSON数据：
（1）用一个字节0b1000xxxx表示元素数量少于16个的Map，字节高四位为固定值1000，低四位xxxx代表Map的长度。以上JSON数据应该是0b1000_0002，值为82。
（2）compact的长度为7，小于31，MessagePack使用一个字节0b101xxxxx表示，字节高三位固定为101，低五位xxxxx代表字符长度，因此编码为A7。
（3）MessagePack直接将true编码成单字节，值为C3。
（4）schema的长度为6，因此编码为A6。
（5）以0结尾。
最后，MessagePack的格式如下，占用18个字节：

82 A7 compack C3 A6 schema 0
```
