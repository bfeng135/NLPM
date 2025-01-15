/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nl.pm.server.operationLog;

import com.nl.pm.server.registry.IOperationLogRegistry;
import com.nl.pm.server.registry.entity.OperationLogEntity;
import com.nl.pm.server.common.SpringApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 操作日志的切面配置
 *
 */
@Aspect
@EnableAspectJAutoProxy(exposeProxy = true,proxyTargetClass = true)
@Component
public class LogAspectConfig {
    @Autowired
    public IOperationLogRegistry logWriter;
    public static final Logger logger = LoggerFactory.getLogger(LogAspectConfig.class);
    @Pointcut("execution(public * com.nl.pm.server.controller.*.*(..)) && @annotation(com.nl.pm.server.operationLog.OperationLog)")
    public void entryPoint(){};

    /**
     * 前置通知
     * @param joinPoint
     */
    @Before("entryPoint()")
    public void beforMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        System.out.println("this method "+methodName+" begin. param<"+ args+">");
    }
    /**
     * 后置通知（无论方法是否发生异常都会执行,所以访问不到方法的返回值）
     * @param joinPoint
     */
    @After("entryPoint()")
    public void afterMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("this method "+methodName+" end.");
    }
    /**
     * 返回通知（在方法正常结束执行的代码）
     * 返回通知可以访问到方法的返回值！
     * @param joinPoint
     */
    @AfterReturning(value="entryPoint()",returning="result")
    public void afterReturnMethod(JoinPoint joinPoint,Object result){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("this method "+methodName+" end.result<"+result+">");
    }
    /**
     * 异常通知（方法发生异常执行的代码）
     * 可以访问到异常对象；且可以指定在出现特定异常时执行的代码
     * @param joinPoint
     * @param ex
     */
    @AfterThrowing(value="entryPoint()", throwing = "ex")
    public void afterThrowingMethod(JoinPoint joinPoint,Exception ex){
        String methodName = joinPoint.getSignature().getName();
        System.out.println("this method "+methodName+" end.ex message<"+ex+">");
    }
    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoin 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     * @param point
     */
    @Around(value="entryPoint()")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {

//        Object result = null;
//        String methodName = point.getSignature().getName();
//        try {
//            //前置通知
//            System.out.println("The method "+ methodName+" start. param<"+ Arrays.asList(point.getArgs())+">");
//            //执行目标方法
//            result = point.proceed();
//            //返回通知
//            System.out.println("The method "+ methodName+" end. result<"+ result+">");
//        } catch (Throwable e) {
//            //异常通知
//            System.out.println("this method "+methodName+" end.ex message<"+e+">");
//            throw new RuntimeException(e);
//        }
//        //后置通知
//        System.out.println("The method "+ methodName+" end.");
//        return result;


        LogAspectContext context;
        try {
            // 处理日志
            context = handleAround(point);
            context.formatter.beforeContext(context.context);
        }
        catch (Exception e) {
            logger.error("日志记录异常", e);
            context = null;
        }

        Object result = point.proceed();

        try {
            if (context != null) {
                context.context.setResult(result);
                context.formatter.afterContext(context.context);
                OperationLogEntity log = new OperationLogEntity();
                log.setId(UUID.randomUUID().toString());
                log.setModule(context.context.getModuleType());
                log.setOperate(context.context.getOperateType());
                log.setContent(context.context.getContent());
                log.setUserId(context.context.getUserId());
                log.setUsername(context.context.getUsername());
                log.setAreaId(context.context.getAreaId());
                log.setIp(getIpAddress(context.context.getRequest()));
                log.setOperateTime(new Date());
                //0:成功 1：更新版本成功
                logWriter.writeToDataBase(log);

            }
        }
        catch (Exception e) {
            logger.error("日志记录异常", e);
        }

        return result;





    }






    /**
     * around日志记录
     *
     * @param point point
     * @throws Exception SecurityException
     */
    public LogAspectContext handleAround(ProceedingJoinPoint point) throws Exception {
        LogAspectContext context = null;
        HttpServletRequest request = null;

        try {
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            request = sra.getRequest();
        }
        catch(Exception ex) {
            logger.error("日志推测Controller请求失败", ex);
        }

        Signature sig = point.getSignature();
        MethodSignature msig;
        if (sig instanceof MethodSignature) {
            msig = (MethodSignature) sig;
            Class<?> targetClass = point.getTarget().getClass();
            Method currentMethod = targetClass.getMethod(msig.getName(), msig.getParameterTypes());

            if (currentMethod != null) {
                // 获取注解对象
                OperationLog sevLog = currentMethod.getAnnotation(OperationLog.class);
                if (sevLog != null) {
                    Class<? extends ILogFormatter> formatterClass = sevLog.formatter();
                    if (formatterClass != null) {
                        ILogFormatter formatter = null;
                        try {
                            formatter = SpringApplicationContext.getBean(formatterClass);
                        }
                        catch (Exception ex) {
                            logger.error(ex.getMessage());
                        }

                        if (formatter == null) {
                            formatter = formatterClass.newInstance();
                        }

                        Object[] pointArgs = point.getArgs();


                        try {
                            String module = StringUtils.trimToNull(sevLog.module());
                            String operate = StringUtils.trimToNull(sevLog.operate());


                            context = new LogAspectContext();
                            context.formatter = formatter;
                            context.context = new LogFormatterContext(request, targetClass.getName(), currentMethod, pointArgs);
//                            UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//                            IUserInfoProvider provider = (IUserInfoProvider) request.getAttribute(Constants.SESSION_USER);
//                            if (provider != null) {
//                                RequestContext.
//                                context.context.setUserId(provider.getUserIdFromProvider());
//                            }
                            context.context.setModuleType(module);
                            context.context.setOperateType(operate);
                        }
                        catch (Exception ex) {
                            logger.error("转换操作日志出现异常", ex);
                        }
                    }
                }
            }
        }

        return context;
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     *
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     *
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     *
     * 用户真实IP为： 192.168.1.110
     *
     * @param request  request
     * @return String
     */
    public String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 通过IP地址获取MAC地址
     * @param ip String,127.0.0.1格式
     * @return mac String
     */
    public String getMACAddress(String ip) {
        String macAddress = "";
        final String LOOPBACK_ADDRESS = "127.0.0.1";

        try {
            if (LOOPBACK_ADDRESS.equalsIgnoreCase(ip)) {
                //如果为127.0.0.1,则获取本地MAC地址
                InetAddress inetAddress = InetAddress.getLocalHost();
                //貌似此方法需要JDK1.6。
                byte[] mac = NetworkInterface.getByInetAddress(inetAddress).getHardwareAddress();
                //下面代码是把mac地址拼装成String
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mac.length; i++) {
                    if (i != 0) {
                        sb.append("-");
                    }
                    //mac[i] & 0xFF 是为了把byte转化为正整数
                    String s = Integer.toHexString(mac[i] & 0xFF);
                    sb.append(s.length() == 1 ? 0 + s : s);
                }
                //把字符串所有小写字母改为大写成为正规的mac地址并返回
                macAddress = sb.toString();
            }
            else {
                //获取非本地IP的MAC地址
                macAddress = getMacInLinux(ip);
                if (StringUtils.isBlank(macAddress)){
                    macAddress = getMacInWindow(ip);
                }
            }
        }
        catch (Exception ex) {
            logger.error("获取客户端MAC地址失败", ex);
        }

        macAddress = StringUtils.trimToNull(macAddress);
        if (StringUtils.isBlank(macAddress)) {
            macAddress = ip;
        } else {
            macAddress = macAddress.trim().toUpperCase();
        }

        return macAddress;

    }

    /**
     * linux系统环境下，通过目标ip获取目标的mac地址
     * @param ip 目标ip
     * @return String
     */
    public String getMacInLinux(String ip){
        String result = "";
        StringBuilder sb = new StringBuilder();
        String[] cmd = {"/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a"};
        String regExp = "((([0-9,A-F,a-f]{1,2}:){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern = Pattern.compile(regExp);

        try {
            String line;
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null){
                sb.append(line);
            }
            String cmdResStr = sb.toString();
            if (StringUtils.isNotBlank(cmdResStr)){
                String lastIndexStr = cmdResStr.substring(cmdResStr.lastIndexOf(ip));
                Matcher matcher = pattern.matcher(lastIndexStr);
                if (matcher.find()){
                    result = matcher.group(1);
                }
            }
            br.close();
        } catch (IOException e) {
            logger.error("Linux获取客户端MAC地址，执行cmd失败, ip=" + ip);
        }
        return result;
    }

    /**
     * 在win环境下获取其他目标ip的mac地址
     * @param ip 目标ip
     * @return String
     */
    public String getMacInWindow(String ip){
        String line;
        String macAddress = "";
        final String MAC_ADDRESS_PREFIX = "MAC ADDRESS = ";

        try {
            //获取非本地IP的MAC地址
            Process p = Runtime.getRuntime().exec("nbtstat -A " + ip);
            InputStreamReader isr = new InputStreamReader(p.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                line = StringUtils.trimToNull(line);
                if (line != null) {
                    line = line.toUpperCase();
                    int index = line.indexOf(MAC_ADDRESS_PREFIX);
                    if (index != -1) {
                        macAddress = line.substring(index + MAC_ADDRESS_PREFIX.length());
                    }
                }
            }
            br.close();
        }
        catch (Exception ex) {
            logger.error("Win获取客户端MAC地址失败，ip="+ip);
        }
        return macAddress;
    }


    public static class LogAspectContext {
        public ILogFormatter formatter;
        public LogFormatterContext context;
    }


}
