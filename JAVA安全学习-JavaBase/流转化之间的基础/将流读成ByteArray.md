# 将流读成ByteArray

## 第一种

```

    public static byte[] toByteArray(InputStream in) throws IOException {
        byte[] classBytes;
        classBytes = new byte[in.available()];
        in.read(classBytes);
        in.close();
        return classBytes;
    }
```

## 第二种

```
    public static byte[] toByteArray(InputStream in) throws IOException {
			int len =0;
        byte[] bytes = new byte[4096];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while((len=in.read(bytes))!=-1){
            byteArrayOutputStream.write(bytes,0,len);
        }
        return  byteArrayOutputStream.toByteArray();
    }
```

