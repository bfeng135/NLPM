package com.nl.pm.server;

import com.nl.pm.server.service.ISystemJob;
import com.nl.pm.server.service.model.SystemJobModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.CollectionUtils;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableScheduling //开启定时任务
@EnableSwagger2
public class NlPmServerApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(NlPmServerApplication.class, args);

		//检查邮件发送
		ISystemJob systemJob = context.getBean(ISystemJob.class);
		try {
			initSendEmail(systemJob);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private static void initSendEmail(ISystemJob systemJob) throws Exception {
		List<SystemJobModel> list = systemJob.queryAllSystemJob();
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		for (SystemJobModel model:list){
			if(model.getEnable()){
				systemJob.initSendEmail(model);
			}
		}
	}

}
