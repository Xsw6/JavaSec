package com.example.shirocve20103863.config;

import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public IniRealm getIniRealm(){
        return new IniRealm("classpath:shiro.ini");
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(IniRealm iniRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(iniRealm);
        return securityManager;
    }

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean filter = new ShiroFilterFactoryBean();
        filter.setSecurityManager(defaultWebSecurityManager);

        Map<String,String> filterMap = new HashMap<>();
        filterMap.put("/secret.html", "authc,roles[admin]");
        filterMap.put("/xsw6/xs.html", "authc,roles[admin]");
        filterMap.put("/**", "anon");

        filter.setFilterChainDefinitionMap(filterMap);

        filter.setLoginUrl("/login.html");
        filter.setUnauthorizedUrl("/unauthorized.html");

        return filter;
    }
}

