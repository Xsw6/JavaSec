# # Thymeleaf

学习历程：了解了一下DispatcherServlet处理流程【学完已经忘了，只知道他是spring中处理请求的关键包括取得路径找到对应控制器、模板渲染等】

主要定位到`this.processDispatchResult(processedRequest, response, mappedHandler, mv, (Exception)dispatchException);`这里主要做了渲染页面

https://www.bilibili.com/video/BV1wV4y1A7hs/?spm_id_from=333.337.search-card.all.click&vd_source=e25ac6512e64c764a52bf014685fce29

https://zhuanlan.zhihu.com/p/385365380【如何获取参数传入参数的值】

todo（over）：

​	1、可以将poc中的`::`进行删除测试【失败，原因：没有进入StandardExpressionPreprocessor#preprocess】。
​	2、如果没有找到`__`和`__`中的字符串会如何处理。【StandardExpressionPreprocessor#checkPreprocessingMarkUnescaping没有细看，但是想了想肯定不能直接返回。不然就流不到Expression#execute】

​	3、测试`#、*、@`等符号【*】成功。

开始：

```
poc:"__${new java.util.Scanner(T(java.lang.Runtime).getRuntime().exec("calc").getInputStream()).next()}__::.x"
```

1、ThymeleafView.class#renderFragment()会处理`::` ，并且将poc加上`~{poc}`

2、StandardExpressionPreprocessor#preprocess()会进一步处理poc

- 查找字符在字符串中第一次出现`_`的位置
- 匹配`__`和`__`中的字符串
- 提取出`_`之前的字符串
- 提取出`__`和`__`中的字符串

3、StandardExpressionParser#parseExpression()去除提取`__`和`__`中的字符串中所有的空格

4、LiteralSubstitutionUtil#performLiteralSubstitution()将字符串进行处理，但是实际只是简单的返回，同时在ExpressionParsingUtil#ExpressionParsingState()也会根据这些符号获取相应的Expression【这里出现一些绕过方式】

`#`:expr = MessageExpression.parseMessageExpression(currentFragment.toString());其逻辑只会反向处理n个`()`之间的内容，但是如果一旦匹配成功一个完整的()就会进入下面的判断，如果是用以上poc那么只会处理出()，得到空的parameters，从而找不到expr。【是否有绕过呢？思路找一个不需要括号的。emmm不大现实】

`@`:瞟了一眼跟上面大差不差？

poc中的`.x`并没有用？*（todo）【解决：当没有返回名的时候 就需要利用到】

剩下的就略。

## fix

1、一开始并没有找到对应的diff文件（解决：对应版本找错了（通过idea报错信息可以看对应版本），网址输入的也有问题：[正确网址](https://github.com/thymeleaf/thymeleaf/compare/thymeleaf-spring5-3.0.11.RELEASE..thymeleaf-spring5-3.0.12.RELEASE)）

## 文章

1、https://www.cnblogs.com/nice0e3/p/16212784.html【膜拜】

## 总结反思

1、在跟完所有的流程后 没有自己去思考漏洞触发点 这也是去专门读分析文章发现的【将模板名字进行了处理导致表达式执行】
2、修复方式不一定是按照官方来看，比如文章中说的`redirect:`【这里还是流程没有跟完所以想不到maybe跟完也想不到】









