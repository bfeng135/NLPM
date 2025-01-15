package com.nl.pm.server.security.provider;

import com.nl.pm.server.registry.IUserRegistry;
import com.nl.pm.server.registry.entity.UserEntity;
import com.nl.pm.server.security.tools.TokenTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class BaseAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private TokenTools tokenTools;
    @Autowired
    private IUserRegistry iUserRegistry;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserEntity user = iUserRegistry.loadUserByUsername(username);
        if(user==null){
            authentication = new UsernamePasswordAuthenticationToken(username, password);
            return authentication;
        }
        String pwd = user.getPassword();
        if(pwd.equals(password)) {
            authentication = new UsernamePasswordAuthenticationToken(username, password,null);
        }else{
            authentication = new UsernamePasswordAuthenticationToken(username, password);
        }

        UsernamePasswordAuthenticationToken result = null;

        if (authentication != null && authentication.isAuthenticated()) {
            //此处赋予权限，暂定为空
            final List<GrantedAuthority> grantedAuths = new ArrayList<>();
            final UserDetails userDetails = new User(authentication.getName(), authentication.getCredentials().toString(),
                    grantedAuths);
            result = new UsernamePasswordAuthenticationToken(userDetails,
                    authentication.getCredentials(), grantedAuths);
            result.setDetails(authentication.getDetails());
            return result;
        }
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
