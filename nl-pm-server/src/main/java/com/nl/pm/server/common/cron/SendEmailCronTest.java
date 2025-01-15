package com.nl.pm.server.common.cron;

import com.nl.pm.server.NlPmServerApplication;
import com.nl.pm.server.exception.BaseServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.text.ParseException;

@SpringBootTest(classes = NlPmServerApplication.class)
class SendEmailCronTest {
    @Autowired
    private SendEmailCron userMapper;
    @Test
    void sendEmailToAllUser() throws MessagingException, GeneralSecurityException, BaseServiceException, ParseException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        userMapper.sendEmailToAllUser();
    }

    @Test
    void sendEmailToLeader() throws MessagingException, GeneralSecurityException, BaseServiceException, ParseException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        userMapper.sendEmailToAreaAndGroup();
    }
}