package io.shunters.collector.component.runner;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunLogEventServer {
	
	@Test
	public void run() throws Exception
	{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/META-INF/spring/spring-all-context.xml");
		System.out.println("http server is running now");
		
		Thread.sleep(Long.MAX_VALUE);
	}

}
