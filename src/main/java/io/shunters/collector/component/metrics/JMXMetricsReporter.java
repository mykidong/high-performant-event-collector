package io.shunters.collector.component.metrics;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.InitializingBean;

public class JMXMetricsReporter implements InitializingBean {
	
	private MetricRegistry metricRegistry;	

	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		JmxReporter reporter = JmxReporter.forRegistry(this.metricRegistry).build();
		reporter.start();
	}
}
