package com.nl.pm.server.service.impl;

import com.nl.pm.server.service.ISystemJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import java.security.GeneralSecurityException;
@SpringBootTest
class SystemJobImplTest {

    @Autowired
    public ISystemJob iSystemJob;

    @Test
    void sendEmailToAllUser() throws MessagingException, GeneralSecurityException {
        iSystemJob.sendEmailToAllUser();
    }
}