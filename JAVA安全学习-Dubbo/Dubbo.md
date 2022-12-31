# Dubbo 

## CVE-2019-17564

我反反复复搭建环境....
最后是这样成功的。

```
git clone https://github.com/apache/dubbo-samples.git
git checkout 2.6.x
```

然后忽略报错.....
大体用的这篇文章：https://tyaoo.github.io/2021/06/30/Dubbo%E5%8F%8D%E5%BA%8F%E5%88%97%E5%8C%96%E6%BC%8F%E6%B4%9E%E7%A0%94%E7%A9%B6/

分析我看的这篇文章:https://mp.weixin.qq.com/s/CMA79NyeZN2e_nSxj8L-wQ

### 修复方式

再2.7.5版本中将skeleton由HttpInvokerServiceExporter变为了JsonRpcBasicServer，而在JsonRpcBasicServer中没有反序列化的相关操作了。

