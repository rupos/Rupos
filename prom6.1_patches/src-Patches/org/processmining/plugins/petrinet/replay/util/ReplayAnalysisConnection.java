package org.processmining.plugins.petrinet.replay.util;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.petrinet.replay.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replay.performance.TotalPerformanceResult;

@ConnectionObjectFactory
@Plugin(name = "Petrinet Log Connection Factory", parameterLabels = { "Performace/Conformance", "Log", "Petrinet" }, returnTypes = ReplayAnalysisConnection.class, returnLabels = "Rupos Petrinet Log connection", userAccessible = false)
public class ReplayAnalysisConnection extends AbstractConnection {

	public static String PERFORMANCE = "Performance";
	public static String CONFORMANCE = "Conformance";
	public static String PNET = "Petrinet";
	public static String XLOG = "XLog";
	

	public ReplayAnalysisConnection(TotalPerformanceResult performance, XLog log, Petrinet net) {
		super("PerformanceRuposConnection");
		put(PERFORMANCE, performance);
		put(XLOG, log);
		put(PNET, net);
	}
	
	public ReplayAnalysisConnection(TotalConformanceResult conformance, XLog log, Petrinet net) {
		super("ConformanceRuposConnection");
		put(CONFORMANCE, conformance);
		put(XLOG, log);
		put(PNET, net);
	}  
	@PluginVariant(requiredParameterLabels = { 0, 1 ,2})
	public static ReplayAnalysisConnection connect(PluginContext context, TotalPerformanceResult performance, XLog log, Petrinet net){
		ReplayAnalysisConnection connection = new ReplayAnalysisConnection(performance, log, net);
		//context.getFutureResult(0).setLabel(connection.getLabel());
		return connection;
	}
	
	@PluginVariant(requiredParameterLabels = { 0, 1 ,2})
	public static ReplayAnalysisConnection connect(PluginContext context, TotalConformanceResult conformance, XLog log, Petrinet net){
		ReplayAnalysisConnection connection = new ReplayAnalysisConnection(conformance, log, net);
		//context.getFutureResult(0).setLabel(connection.getLabel());
		return connection;
	}
	
}

