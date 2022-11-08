# FileUpload1

## 利用链

```
DiskFileItem#readObject()
	DiskFileItem#getOutputStream()
		OutputStream#write()			
```

利用链其实很简单
注意点有几个

1、该类序列化的时候`dfos`不能为空。

2、`dfos`修饰符！反序列化为空。
利用过程其实通俗易懂，不做更多解释。但是这里触发起我一个思考？我们要怎样控制这里的文件名呢？（如果清楚这条链子其实可以知道这里根本是控制不了文件名的变量的）
在readObject中看到其有
![image-20221016214618419](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210162146491.png)

让我不禁想找一下以前的版本！
果然以前的版本没有这条限制。（`commons-fileupload<1.3`）
在尝试的过程中发现并没有成功（`在write()`会产生报错），然后百度了一下，这里需要jdk低版本。（`经典换上jdk7u21`），果然成功了。
所以总结一下就是：该版本如果要真正的利用那必须jdk版本足够低，并且`readObject`并没有过滤`\0`。

# FileUpload2

在经过上面的分析之后，我发现在如果`cachedContent`如果为空的话，还可以进行copy。一开始想的太复杂了，想着利用反射修改（`想修改written`，但是是抽象类，如果修改`cachedContent`会发现最后在writeObject又会重置为空，但是不为`null`），弄了半天都没操作成功。
最后发现默认初始值为0，直接操作`threshold`为`-1`即可成功。
