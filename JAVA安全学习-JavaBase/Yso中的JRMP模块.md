# Yso中的JRMP模块

## payloads/JRMPListener

https://xz.aliyun.com/t/10036 (主要是通过反序列化让对方服务器开启一个jrmp监听服务)通常与`exploit/JRMPClient`结合使用。

## payloads/JRMPClient

https://codeantenna.com/a/Z8xxjSXDf5 (对方服务器反序列化后，会连接我们自己的服务器，我们服务器会向对方发起payload进行攻击)  `通常结合exploit/JRMPListener使用`。开启一个监听的作用。（DGC lookup）

## exploit/JRMPClient

https://www.cnblogs.com/yyhuni/p/15069426.html (当服务器反序列化payloads/JRMPListener，即会开启端口监听。再使用exploit/JRMPClient模块发送payload，服务器就会把payload进行反序列化，从而完成进行攻击。)

## exploit/JRMPListener

在自己服务器上使用，作为一个监听的作用并且会向对方发送恶意反序列化数据。通常结合`payloads/JRMPClient`使用。

## 总结

`攻击方法1`：payloads/JRMPListener + exploit/JRMPClient（客户端攻击服务端）DGC

`攻击方法2`：payloads/JRMPClient + exploit/JRMPListener  

(

1.在自己服务器上使用exploit/JRMPListener来开启监听

2.把payloads/JRMPClient发送给对方服务器，对方服务器反序列化后会反向连接我们的服务器，进行连接之间我们服务器会发送payload给对方服务器进行反序列化执行命令。)(服务端攻击客户端)DGC



