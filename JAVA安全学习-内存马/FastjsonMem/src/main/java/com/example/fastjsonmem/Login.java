package com.example.fastjsonmem;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Login {
    @RequestMapping(value = "/fastjson", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject test(@RequestBody String data) {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase","true");
        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "true");
        JSONObject obj = JSON.parseObject(data);
        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("message", "success");
        result.put("data", "Hello " + obj.get("name"));
        return result;
    }
}

