## Codeql基础学习

这篇完全就是跟着[这位师傅学习](https://github.com/safe6Sec/CodeqlNote)，就是做个记录（可惜说是抄袭吧hhhh）。（多手敲敲）主要就学习学习java方面的（`java需要编译`）

利用的数据库为之前学习的`shiro1.2.24`

## 基础查询

### 查询指定方法

```codeql
import java

from Method method
where method.hasname("readObject")
select method
```

缺点：发现好像不能显示找到的jdk自带的readObject（`可能对挖掘jdk链子有些限制`）

`method.getDeclaringType()`:可以打印方法的class name，发现有打印出来结果。（也就对上面做了解释）

### 根据Method name 和 interface name 查询

没去细细找例子。（之后预感再学习利用codeql分析javasec即可分析出结果）、`补充`：下面两个可能单看起来很模糊抽象。我是边学边写的，但是发现再分析shiro那个ql文件，好像一下懂了

### Call和Callable

### MethodAccess

方法调用类，MethodAccess call表示获取当前项目当中的所有方法调用。

## Source和Sink

中间可以看到我选择性的跳过了一些（其实感觉自己还是不熟练）
`Source`：就是漏洞起始点
`Sink`：就是漏洞触发点
再Codeql这样规定：

```codeql
class MyConfig extends DataFlow::Configuration {
  MyConfig() { this = "Myconfig" }
  override predicate isSource(DataFlow::Node source) {
    ....
    
  }

    override predicate isSink(DataFlow::Node sink) {
    ....
    
  }
}
//     from VulConfig config, DataFlow::PathNode source, DataFlow::PathNode sink
//     where config.hasFlowPath(source, sink)
//     select sink.getNode(), source, sink, "source are"
```

说说什么意思，codeql可以允许我们自己构造一个类，如果该类继承了` DataFlow::Configuration`就可以自动的利用看整个链子是否是连通的状态。
`isSource`：这个方法怎么写呢？
拿shiro中的例子：

```codeql
     override predicate isSource(DataFlow::Node source) {
     exists(MethodAccess call |
     call.getMethod().getName()="readValue" and
     source.asExpr()=call
     )
     }
     
     override predicate isSink(DataFlow::Node sink) {
     exists(MethodAccess call |
     call.getMethod().getName()="readObject" and
     sink.asExpr()=call
     )
     }
     }
```

emmm首先

第一点：`MethodAccess`再回过头去看`safe6Sec`师傅的文章。（这里也是自己做了下解释）
第二点：`source.asExpr()`（个人感觉理解代码是讲source方法设置成了我们设定的`readValue`方法）

然后就是固定写法，探测出整个链子：

```codeql
//     from VulConfig config, DataFlow::PathNode source, DataFlow::PathNode sink
//     where config.hasFlowPath(source, sink)
//     select sink.getNode(), source, sink, "source are"
```

真的太牛皮了！！只能这么说。就这样就能挖掘出一整条链子。创作codeql的这些人得有多牛逼！！！！！

## 数据流断的原因以及isAddtionalStep技巧

还是再之前学习shiro1.2.24的时候，文章中也有提到数据流断的原因。现在学起来感觉太抽象。

回过头来看看师傅们怎么再shiro1.2.24上怎么连接起的？

`https://www.anquanke.com/post/id/255721#h3-11`
说了原因就是因为Codeql判断this.getcookie与cookie.getValue不是同一个值。
可以来测试一下，果然没有找到。
![image-20221021174455554](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210211745705.png)

这里解决办法师傅提供了两种：

第一种：直接从readValue到readObject探测链子。

第二种：利用isAdditionalTaintStep函数将断开的链子强行连接起来。

```codeql
/**
 * @kind path-problem
 */
import java
import semmle.code.java.dataflow.FlowSources
import semmle.code.java.security.UnsafeDeserializationQuery
import DataFlow::PathGraph

/**
 * 根据分析的连贯性，定位第一个节点和第二个节点
 */
predicate isCookie(Expr expSrc, Expr expDest) {
    exists(MethodAccess ma | 
        expSrc.getType().toString() = "Cookie" // 第一个节点类型是Cookie
        and expDest = ma
        and ma.getMethod().getName() = "getValue"   // 第二个节点的函数名
        and ma.getMethod().getDeclaringType().toString() = "Cookie" // 第二个节点函数的返回类型
        )
}

class TestShiro extends TaintTracking::Configuration {
    TestShiro() { this = "TestShiro" }

    override predicate isSource(DataFlow::Node source) {
        exists(MethodAccess m| 
            m.getMethod().getName() = "getCookie" and source.asExpr() = m)
    }

    override predicate isSink(DataFlow::Node sink) {
        exists(MethodAccess m |
            m.getMethod().getName() = "readObject" and sink.asExpr() = m)
    }

    override predicate isAdditionalTaintStep(DataFlow::Node node1, DataFlow::Node node2) {
        isCookie(node1.asExpr(), node2.asExpr())
    }
}

from DataFlow::PathNode source, DataFlow::PathNode sink, TestShiro ts
where ts.hasFlowPath(source, sink)
select sink.getNode(), source, sink, "source are"
```

看到这里我认为我可以写一个完整的探测shiro的链子。
