package io.shunters.collector.component.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class GraphiteMetricsReporter implements InitializingBean {
	
	private MetricRegistry metricRegistry;	
	
	private String carbonHost;
	
	private int carbonPort;	
	
	private String prefix;

	private boolean hostNameIncluded = false;

	public void setHostNameIncluded(boolean hostNameIncluded) {
		this.hostNameIncluded = hostNameIncluded;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	public void setCarbonHost(String carbonHost) {
		this.carbonHost = carbonHost;
	}


	public void setCarbonPort(int carbonPort) {
		this.carbonPort = carbonPort;
	}


	public void setMetricRegistry(MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		
		String localHostName =  InetAddress.getLocalHost().getHostName();
		localHostName = localHostName.replaceAll("\\.", "_");

		String prefixedWith = (hostNameIncluded) ? this.prefix + "-" + localHostName : this.prefix;
		
		Graphite graphite = new Graphite(new InetSocketAddress(this.carbonHost, this.carbonPort));
		GraphiteReporter reporter = GraphiteReporter.forRegistry(this.metricRegistry)
		                                                  .prefixedWith(prefixedWith)
		                                                  .convertRatesTo(TimeUnit.SECONDS)
		                                                  .convertDurationsTo(TimeUnit.MILLISECONDS)
		                                                  .filter(MetricFilter.ALL)
		                                                  .build(graphite);
		reporter.start(30, TimeUnit.SECONDS);	
	}
}
