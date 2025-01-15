package com.nl.pm.server.security;

import com.nl.pm.server.security.filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)//全局
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    JwtTokenFilter jwtTokenFilter;
    /**
     * 配置放行资源
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //放行swagger资源
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
        web.ignoring().antMatchers("/area/getAreaList");
        web.ignoring().antMatchers("/login");
        web.ignoring().antMatchers("/system/getSystemName");



    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

                http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .servletApi()
                .and()
                .csrf().disable();

                http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
