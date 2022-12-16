## 第一种

```java
public class HashCollision {

    public static String convert(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8); // 取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); // 取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }

        return unicode.toString();
    }

    /**
     * Returns a string with a hash equal to the argument.
     *
     * @return string with a hash equal to the argument.
     * @author - Joseph Darcy
     */
    public static String unhash(int target) {
        StringBuilder answer = new StringBuilder();
        if (target < 0) {
            // String with hash of Integer.MIN_VALUE, 0x80000000
            answer.append("\u0915\u0009\u001e\u000c\u0002");

            if (target == Integer.MIN_VALUE)
                return answer.toString();
            // Find target without sign bit set
            target = target & Integer.MAX_VALUE;
        }

        unhash0(answer, target);
        return answer.toString();
    }

    /**
     *
     * @author - Joseph Darcy
     */
    private static void unhash0(StringBuilder partial, int target) {
        int div = target / 31;
        int rem = target % 31;

        if (div <= Character.MAX_VALUE) {
            if (div != 0)
                partial.append((char) div);
            partial.append((char) rem);
        } else {
            unhash0(partial, div);
            partial.append((char) rem);
        }
    }


    //变体Unicode编码转换为正常Unicode编码
    public static String changechar(String url) {
        String chars=url;
        String newchar=chars.replace("/", "");
        return newchar;

    }
    //unicode编码转换为正常汉字
    private static String unicodeToCn(String unicode) {
        /** 以  u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("u");
        String returnStr = "";
        // 由于unicode字符串以  u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i], 16).intValue();
        }
        return returnStr;
    }

    public static void main(String[] args) {
        int i = "WelComeToNCTF2022".hashCode();
        System.out.println(i);
        System.out.println(convert(unhash(i)));
//        System.out.println(unicodeToCn(changechar(convert(unhash(-2040793675)))));
        System.out.println("\ud675\u0008\u000b\u001a".hashCode());
    }
}
```

## 第二种

```java
public class HashCode {
    public static void main(String[] args) {
        for (long i=0;i<99999999999L;i++){
            if(Long.toHexString(i).hashCode() =="WelComeToNCTF2022".hashCode())
            {
                System.out.println(Long.toHexString(i));
            }
        }
    }
}
```

