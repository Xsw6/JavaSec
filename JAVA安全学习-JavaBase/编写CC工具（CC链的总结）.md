# 编写CC工具

当然也会重新学习一下：
相信这回学习一定会收获很多。

## 有意思的总结

`哪些链子会报错`：CC1、CC2、CC3、CC4()、CCK1、CCK2

`依赖版本4的链子`：CC2、CC4、CCK2、CCK4

`恶意类加载`：CC2、CC3、CC4、CCK1、CCK2、

`命令执行`：CC1、CC5、CC6、CC7、CCK3、CCK4

`哪些链子不会报错`：CC5、CC6、CC7、CCK3、CCK4

## CC1

一句话总结：readObject触发动态代理后.....

```
->AnnotationInvocationHandler.readObject()
      ->mapProxy.entrySet().iterator()  //动态代理类
          ->AnnotationInvocationHandler.invoke()
            ->LazyMap.get()
```

## CC2

```
->PriorityQueue.readObject()
      ->PriorityQueue.heapify()
          ->PriorityQueue.siftDown()
            ->PriorityQueue.siftDownUsingComparator()
                ->TransformingComparator.compare()
                    ->InvokerTransformer.transform()
                        ->TemplatesImpl.newTransformer()
                        ->…………
```

## CC3

与前面不同的地方是：利用TrAXFilter来触发`_transformer = (TransformerImpl) templates.newTransformer();`

```
->AnnotationInvocationHandler.readObject()
      ->mapProxy.entrySet().iterator()  //动态代理类
          ->AnnotationInvocationHandler.invoke()
            ->LazyMap.get()
                ->ChainedTransformer.transform()
                    ->ConstantTransformer.transform()
                        ->InstantiateTransformer.transform()
                            ->TrAXFilter.TrAXFilter()
                                ->TemplatesImpl.newTransformer()
                                    ->…………
```

## CC4

CC2+CC3结合

```
->PriorityQueue.readObject()
      ->PriorityQueue.heapify()
          ->PriorityQueue.siftDown()
            ->PriorityQueue.siftDownUsingComparator()
                 ->TransformingComparator.compare()
                    ->ChainedTransformer.transform()
                        ->ConstantTransformer.transform()
                                    ->InstantiateTransformer.transform()
                             ->TrAXFilter.TrAXFilter()
                                 ->TemplatesImpl.newTransformer()
                                        ->…………
```

## CC5

一句话总结：BadAttributeValueExpException调用了toString从而触发....

```
->BadAttributeValueExpException.readObject()
      ->TiedMapEntry.toString()
          ->TiedMapEntry.getValue()
            ->LazyMap.get()
                ->ChainedTransformer.transform()
                    ->ConstantTransformer.transform()
                            ->InvokerTransformer.transform()
                                ->…………
```

## CC6

刚开始学的时候有些不能理解，现在再阅读过大量的代码后在回过头来观察发现，其实也不难理解。
![image-20221110230743821](https://cdn.jsdelivr.net/gh/zx-creat/myblog@master/img/202211102307889.png)

```
  ->Hashset.readObject()
            ->TiedMapEntry.hashCode()
                    ->TiedMapEntry.getValue()
                    ->LazyMap.get()
                      ->ChainedTransformer.transform()
                          ->ConstantTransformer.transform()
                              ->InvokerTransformer.transform()
                                  ->…………
```

## CC7

在Hashtable的readObject方法中会把每个key-value往table里面丢，从往table中丢第二个map的时候，就需要开始让它的key和之前的key进行对比，看看有没有重复以决定是新添加一个map还是覆盖原有的。

```
  ->Hashtable.readObject()
      ->Hashtable.reconstitutionPut()
            ->AbstractMapDecorator.equals
                ->AbstractMap.equals()
                  ->LazyMap.get.get()
                    ->ChainedTransformer.transform()
                      ->ConstantTransformer.transform()
                        ->InvokerTransformer.transform()
                          ->…………
```

## CCK1

相对来说非常简单了

```
->hashmap.readObject
	->TiedMapEntry.hashCode
    	....
```

## CCK2

与CC1的区别的就是利用了CC4版本的东西。(反射获取iMethodName)

## CCK3

反射获取iTransformers

```
->hashmap.readObject
	->TiedMapEntry.hashCode
    	....
```

## CCK4

与CCK3区别利用了CC4的版本。