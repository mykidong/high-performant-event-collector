package io.shunters.collector.component.metrics;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class MetricRegistryFactory implements InitializingBean, FactoryBean<MetricRegistry> {
	
	private MetricRegistry metricRegistry;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.metricRegistry = new MetricRegistry();
	}

	@Override
	public MetricRegistry getObject() throws Exception {		
		return this.metricRegistry;
	}

	@Override
	public Class<?> getObjectType() {	
		return this.metricRegistry.getClass();
	}

	@Override
	public boolean isSingleton() {		
		return true;
	}
}
