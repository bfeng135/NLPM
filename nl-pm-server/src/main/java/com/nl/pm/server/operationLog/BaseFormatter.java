package com.nl.pm.server.operationLog;

import com.nl.pm.server.security.tools.TokenTools;
import com.nl.pm.server.common.SecurityContextUtils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
@Component
public class BaseFormatter implements ILogFormatter{
    @Autowired
    private SecurityContextUtils securityContextUtils;

    @Override
    public void beforeContext(LogFormatterContext context) throws IOException {
        Object[] parameters = context.getParameters();
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        Iterator<Object> iterator = Arrays.stream(parameters).iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            Gson gson = new Gson();
            String jsonStr = gson.toJson(next);
            responseStrBuilder.append(jsonStr);
        }
        context.setContent(responseStrBuilder.toString());

    }

    @Override
    public void afterContext(LogFormatterContext context) {
        String currentUsername = securityContextUtils.getCurrentUsername();
        Integer currentAreaId = TokenTools.getCurrentAreaId();

        context.setUsername(currentUsername);
        context.setAreaId(currentAreaId);
    }
}
