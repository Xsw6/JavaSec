# serialVersionUID

https://www.cnblogs.com/duanxz/p/3511695.html

A为某台机器序列化 B为某台机器反序列化

情况1：如果A、B端，serialVersionUID不同则会报错

情况2：如果A、B端，serialVersionUID想同，但是B端代码相对于A端有多的字段，不影响结果，最终影响反序列化结果的为B端。未赋值的字段为Java构造器默认的值

情况3：如果A、B端，serialVersionUID相同，但是A端代码想对于B端有多的字段，

**其实这三种情况就可以充足的说明再serialVersionUID相同的情况下，最终影响反序列化结果的为B端，未赋值的字段为Java构造器默认的值。**

