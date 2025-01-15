package com.nl.pm.server.common;

import com.nl.pm.server.common.enums.RoleTypeEnum;
import com.nl.pm.server.exception.BaseServiceException;
import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.service.IUserService;
import com.nl.pm.server.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@Component
public class SecurityContextUtils {
    @Autowired
    private IUserService iUserService;

    public Integer getCurrentUserAreaId(){
        return TokenTools.getCurrentAreaId();
    }

    public String getCurrentUsername() {
        String currentUsername = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication !=null) {
            Object credentials = authentication.getCredentials();
            Object details = authentication.getDetails();
            Object principal = authentication.getPrincipal();
            String name = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            currentUsername = name;
        }
        return currentUsername;
    }

    public UserModel getCurrentUser() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        String currentUsername = getCurrentUsername();
        UserModel userModel = iUserService.queryUserByUserName(currentUsername);
        return userModel;
    }

    public Integer getCurrentUserId() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        UserModel currentUser = getCurrentUser();
        return currentUser.getId();
    }

    public RoleTypeEnum getCurrentUserRole() throws BaseServiceException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        UserModel currentUser = getCurrentUser();
        String roleCode = currentUser.getRoleCode();
        return RoleTypeEnum.convertToEnum(roleCode);
    }
}
