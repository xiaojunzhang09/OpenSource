**高性能程序重视谨慎处理字符串，数字。**

# 书中章节
```aidl	
第2章 字符串和数字操作	
    2.1　构造字符串	
    2.2　字符串拼接	
    2.3　字符串格式化	
    2.4　字符串查找	
    2.5　替换	
    2.6　intern方法	
    2.7　UUID	49
    2.8　StringUtils类	
    2.9　前缀树过滤	
    2.10　数字装箱	
    2.11　BigDecimal	
```

# 主要代码说明

* com.ibeetl.code.ch02.StringConcatTest  字符串拼接，正常使用"+"号也有很好的性能
* com.ibeetl.code.ch02.StringFormatTest  字符串格式化输出
* com.ibeetl.code.ch02.StringPoolTest intern方法
* com.ibeetl.code.ch02.StringSearch 字符串搜索
* com.ibeetl.code.ch02.StringReplaceTest 字符串替换
* com.ibeetl.code.ch02.SplitTest 字符串拆分，如果程序出现了字符串split，那么一定没有面向对象设计
* com.ibeetl.code.ch02.KeywordSearch 前缀树
* com.ibeetl.code.ch02.UUIDTest 高并发下UUID的使用
* com.ibeetl.code.ch02.BigDecimalTest 使用Long代替BigDecimal
* com.ibeetl.code.ch02.StringUtils 一些第三方工具，如apache common，hutool


# 节选

```aidl
很多系统都采用UUID来获取一个唯一的字符串，UUID比那些使用数据库序列或者Key生成服务器来获取不重复字符串的方式的效率高得多。不幸的是，JDK自带的UUID算法UUID.randomUUID 存在高并发情况下性能变慢的情况。如下代码解释了这个原因：

   public static UUID randomUUID() {
        SecureRandom ng = Holder.numberGenerator;

        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6]  &= 0x0f;  /* clear version        */
        randomBytes[6]  |= 0x40;  /* set to version 4     */
        randomBytes[8]  &= 0x3f;  /* clear variant        */
        randomBytes[8]  |= 0x80;  /* set to IETF variant  */
        return new UUID(randomBytes);
    }
SecureRandom.nextBytes在高并发下存在问题，这是因为为了获得安全随机数，操作系统会根据主机环境，如温度、网络数据包、磁盘读取、鼠标移动等预先生成一部分随机数，存放在/dev/random 文件中。如果并发量大，则会导致随机数不足。如果系统通过JVM的CPU采样，发现UUID.randomUUID中的总耗时远大于CPU耗时，则说明遇到了随机数不足，nextBytes方法调用将被阻塞。这个结论根据JDK版本和供应商，以及运行的操作系统的不同而不同，比如Open JDK8以上的版本默认是用了/dev/random文件和NativePRNG算法(其他算法还有NativePRNGNonBlocking，SHA1PRNG)计算随机数。，可以参考jre/lib/security/java.security文件了解当前JDK的随机数文件的路径和使用的算法。
一种替代办法是使用非阻塞的种子文件，配置系统属性，随机数使用uradom：
    "-Djava.security.egd=file:/dev/./urandom"
```