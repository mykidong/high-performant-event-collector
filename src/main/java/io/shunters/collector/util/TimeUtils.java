package io.shunters.collector.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {

	private static Logger log = LoggerFactory.getLogger(TimeUtils.class);

	public static void pause(long delayInNanos){
		long start = System.nanoTime();
		while(start + delayInNanos >= System.nanoTime());
	}

	public static void sleep(long sleepInMillis)
	{
		try {
			Thread.sleep(sleepInMillis);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void printElapsedTime(long startTime, String name)
	{
		long endTime = System.currentTimeMillis();
		log.info("name: [" + name + "], elapsed time: [" + (endTime - startTime) + "]ms");
	}
}
