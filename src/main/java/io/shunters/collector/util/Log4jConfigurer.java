package io.shunters.collector.util;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;

public class Log4jConfigurer implements InitializingBean {

	public static final String DEFAULT_LOG4J_XML = "log4j.xml";
	
	static Log4jConfigurer instance;
	
	private String confPath;

	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}

	public static void loadLog4j()
	{
		Log4jConfigurer log4jConfigurer = new Log4jConfigurer();
		log4jConfigurer.setConfPath(DEFAULT_LOG4J_XML);
		try {
			log4jConfigurer.afterPropertiesSet();
		}catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		ClassPathResource classPathResource = new ClassPathResource(confPath);
		URL url = classPathResource.getURL();
		DOMConfigurator.configure(url);
		System.out.println("log4j url: " + url.toString());

		DOMConfigurator.configure(url);

		final Logger log = LoggerFactory.getLogger(Log4jConfigurer.class);
		log.debug("log4j loaded...");	
	}
}

