# Codelql挖掘shiro

[学习](https://www.anquanke.com/post/id/255721)（在简单看了下基础就来弄这个，其实codeql是什么以及怎么熟练的使用都还没有完全的掌握......先看看吧），按照自己思路随便写写。

## 环境

搭建了很久。再创建数据库的时候。

第一个问题出现再创建数据库：[解决办法](https://blog.csdn.net/qq_38376348/article/details/108962790)
第二个问题出现这样的报错：`java.lang.OutOfMemoryError: PermGen space`：[解决办法](https://jira.atlassian.com/browse/BAM-14032)
第三个问题:安装svn（再编译的时候会发现找不到svn,不确定对构建数据库是否影响）

推荐使用[这里的](https://www.wangan.com/p/7fygf3002b70f418#%E6%95%B0%E6%8D%AE%E5%BA%93%E6%9E%84%E5%BB%BA)构建数据库的方法，这里我成功了。

## 寻找source点

那自然是找getcookie的点了。

```sql
import java
import semmle.code.java.dataflow.FlowSources

from MethodAccess m, DataFlow::Node source
where m.getMethod().getName() = "getCookie" and source.asExpr() = m
select m, source.asExpr()
```

## 寻找sink点

```sql
import java
import semmle.code.java.dataflow.FlowSources

from MethodAccess m, DataFlow::Node sink
where m.getMethod().getName() = "readObject" and sink.asExpr() = m
select m, sink.asExpr()
```

那么接下来就是判断这两者是不是连通的状态了。
根据文章的点可以知道简单的重载是连接不通的。

## 连通

```sql
import java
import semmle.code.java.dataflow.FlowSources
import semmle.code.java.security.UnsafeDeserializationQuery
import DataFlow::PathGraph

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
}

from DataFlow::PathNode source, DataFlow::PathNode sink, TestShiro ts
where ts.hasFlowPath(source, sink)
select source, sink
```

然后作者使用这个函数isAdditionalTaintStep()将其连接。

```sql
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
select source, sink
```

emmm但是我看结果只能看到流通的链子。但是没有完整的一个流程还是需要自己去找。
算了算了再去学学基础吧。

哈哈哈我又回来了。

## 解决问题

没有显示完整的利用链，只需要在`ql`代码头部加上。

```sql
/**
 * @kind path-problem
 */
```

