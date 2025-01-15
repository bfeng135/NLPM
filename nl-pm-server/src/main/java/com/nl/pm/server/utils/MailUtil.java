package com.nl.pm.server.utils;

import com.nl.pm.server.common.email.SendEmailBaseConfig;
import com.nl.pm.server.registry.entity.UserEntity;
import com.sun.mail.util.MailSSLSocketFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.*;

@Component
public class MailUtil {
    @Value("${system.send.email.nickname}")
    private String nickname;


    /**
     *
     * @param flag smtp是否需要认证
     * @param host 邮件服务器主机名
     * @param username 用户名
     * @param password 密码
     * @param title  标题
     * @param content  内容
     * @param cc  是否抄送
     * @param copyUser  抄送人
     * @param to  是否发送
     * @param receiveUser  接收人
     */
    public static void sendEmailToAllUser(String flag,String host, String username, String password,
                                              String title,String content,
                                              Boolean cc,List<UserEntity> copyUser,
                                              Boolean to,List<UserEntity> receiveUser) throws MessagingException, GeneralSecurityException {

        SendEmailBaseConfig config = new SendEmailBaseConfig(flag,host,username,password,title,content,false,null,
                cc,copyUser,null,to,receiveUser,null);

        config.sendEmailToAllUser();
    }


    /**
     *
     * @param flag smtp是否需要认证
     * @param host 邮件服务器主机名
     * @param username 用户名
     * @param password 密码
     * @param title  标题
     * @param content  内容
     * @param cc  是否抄送
     * @param copyUser  抄送人
     * @param to  是否发送
     * @param receiveUser  接收人
     */
    public static void sendEmailToAreaOrGroup(String flag,String host, String username, String password,
                                              String title,List<Map<String,List<UserEntity>>> content,
                                              Boolean cc,List<UserEntity> copyUser,
                                              Boolean to,List<UserEntity> receiveUser) throws GeneralSecurityException, MessagingException {

        SendEmailBaseConfig config = new SendEmailBaseConfig(flag,host,username,password,title," ",true,content,
                cc,copyUser,null,to,receiveUser,null);

        config.sendEmailToAllUser();


    }

    /**
     * 发送邮件给同步项目管理员
     * @param flag smtp是否需要认证
     * @param host 邮件服务器主机名
     * @param username 用户名
     * @param password 密码
     * @param title  标题
     * @param content  内容
     * @param cc  是否抄送
     * @param copyUser  抄送人
     * @param to  是否发送
     * @param receiveUser  接收人
     */
    public static void sendEmailToSyncProjectManager(String flag,String host, String username, String password,
                                              String title,String content,
                                              Boolean cc,List<UserEntity> copyUser,
                                              Boolean to,List<UserEntity> receiveUser) throws GeneralSecurityException, MessagingException {

        SendEmailBaseConfig config = new SendEmailBaseConfig(flag,host,username,password,title,content,true,null,
                cc,copyUser,null,to,receiveUser,null);

        System.out.println("————————————开始发送邮件-同步项目名称————————————");
        config.sendEmailToSyncProjectManager(content);
        System.out.println("————————————发送邮件完毕-同步项目名称————————————");

    }

}
