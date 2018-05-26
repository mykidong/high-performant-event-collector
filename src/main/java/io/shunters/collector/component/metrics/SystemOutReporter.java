package io.shunters.collector.component.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

public class SystemOutReporter implements InitializingBean {
	
	private MetricRegistry metricRegistry;	

	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		ConsoleReporter reporter = ConsoleReporter.forRegistry(this.metricRegistry)
			       .convertRatesTo(TimeUnit.SECONDS)
			       .convertDurationsTo(TimeUnit.MILLISECONDS)
			       .build();
		reporter.start(1, TimeUnit.SECONDS);
	}
}
