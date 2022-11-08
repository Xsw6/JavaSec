# Jackson-TemplatesImpl-DefaultTyping的配置

[学习](https://www.leadroyal.cn/p/594/)

## DefaultTyping的配置

1. JAVA_LANG_OBJECT 当类里的属性声明为一个Object时，会对该`属性`进行序列化和反序列化，并且明确规定类名。（当然，这个Object本身也得是一个可被序列化/反序列化的类）
2. OBJECT_AND_NON_CONCRETE 。除了上文 提到的特征，当类里有Interface 、AbstractClass 时，对其进行序列化和反序列化。（当然，这些类本身需要是合法的、可以被序列化/反序列化的对象）。
3. NON_CONCRETE_AND_ARRAYS ，除了上文提到的特征，还支持上文全部类型的Array类型。
4. NON_FINAL ，包括上文提到的所有特征，而且包含即将被序列化的类里的全部、非final的属性，也就是相当于整个类、除final外的的属性信息都需要被序列化和反序列化。

## 调试反序列化流程

1. 先进行反序列化类的newInstance()，调用无参构造函数。
2. `在当反序列化对象中没有getter、setter方法时，反射直接赋值。如果有setter方法直接调用setter方法进行赋值`。

这是我简单的调试结果触发的两个点。（`下图为关键过程`）（这里仅仅为没有设置`DefaultTyping的配置`的代码）
![image-20221018113558550](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210181135675.png)

## 漏洞

如果跟过fastjson就会发现这个流程其实缩短了很多（比较片面的感觉吧......）。这也许可能就是jackson为什么比faastjson速度更快的原因：`代码量减少了很多`。（不确定，当作胡言乱语hhhh）

那么下面可以试一下如何利用这个漏洞，直接上TemplateImpl链（`但是fastjson调用的getter方法`），但是上面调试好像并没有经过getter方法？

接着翻阅了许多资料发现`DefaultTyping的配置`还是没有彻底弄清楚。也是上面的调试只是调试了最基本的部分。接下来的调试我尝试了进行在一个反序列化类中包含了Obejct属性。并且我在这个Obejct属性中的getter、setter方法中都放入了`Runtime.getRuntime.exec("calc");`。

## 根本造成漏洞利用原因

在某个反序列化的过程中开启了：`DefaultTyping的配置`
跟一下流程吧。

其实还是上述流程，只不过值得注意的是BeanDeserializer#vanillaDeserialize()方法中，最后有个While循环。
但是通过调试仍然发现其触发方法依旧在`构造方法中`。（因为还是没有写getter、以及setter方法）。

然后再调试TmeplatesImpl。再经过上面的调试分析之后可以清楚的知道，看看poc就知道是什么情况

![image-20221018141128779](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210181411846.png)

但是TemplatesImpl中却没有setOutputProperties。这可怎么办？但是奇怪的是payload可以执行。
于是又回过头去看最基本的，发现问题出现再 ObejctMapper# _readMapAndClose方法中。

`JsonDeserializer<Object> deser = this._findRootDeserializer(ctxt, valueType);`关键代码。

慢慢进入调试会发现：
![image-20221018165400832](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210181654912.png)

逻辑很容易读懂。如果含有getter方法机会进入constructSetterlessProperty方法（其中也获得了对应的getter方法，将其封装成`SetterlessProperty`方法这里是为了最后的触发），而useGettersAsSetter字面翻译即可懂他什么意思。
![image-20221018170419490](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202210181704552.png)

最后导致触发。

## 总结

就凭着自己感觉写写了。

1. 区别与Fastjson，这里如果调用的参数如果不是属性也不存在setter方法，最后会利用反射调用其getter方法赋值。（fastjson具体是什么样，真的忘了.....）
2. 然后需要开启enableDefaultTyping()配置。
3. 其实流程没有仔细去弄懂，跟fastjson差不多，都是得到一个反序列化器去反序列化，问题就是恢复属性这些东西调用了getter、setter方法而已。