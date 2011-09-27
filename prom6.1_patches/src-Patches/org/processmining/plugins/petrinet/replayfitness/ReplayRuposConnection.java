package org.processmining.plugins.petrinet.replayfitness;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

@ConnectionObjectFactory
@Plugin(name = "Petrinet Log Connection Factory", parameterLabels = { "Performace/Conformance", "Log", "Petrinet" }, returnTypes = ReplayRuposConnection.class, returnLabels = "Rupos Petrinet Log connection", userAccessible = false)
public class ReplayRuposConnection extends AbstractConnection {

	public static String PERFORMANCE = "Performance";
	public static String CONFORMANCE = "Conformance";
	public static String PNET = "Petrinet";
	public static String XLOG = "XLog";
	

	public ReplayRuposConnection(TotalPerformanceResult performance, XLog log, Petrinet net) {
		super("PerformanceRuposConnection");
		put(PERFORMANCE, performance);
		put(XLOG, log);
		put(PNET, net);
	}
	
	public ReplayRuposConnection(TotalConformanceResult conformance, XLog log, Petrinet net) {
		super("ConformanceRuposConnection");
		put(CONFORMANCE, conformance);
		put(XLOG, log);
		put(PNET, net);
	}  
	@PluginVariant(requiredParameterLabels = { 0, 1 ,2})
	public static ReplayRuposConnection connect(PluginContext context, TotalPerformanceResult performance, XLog log, Petrinet net){
		ReplayRuposConnection connection = new ReplayRuposConnection(performance, log, net);
		//context.getFutureResult(0).setLabel(connection.getLabel());
		return connection;
	}
	
	@PluginVariant(requiredParameterLabels = { 0, 1 ,2})
	public static ReplayRuposConnection connect(PluginContext context, TotalConformanceResult conformance, XLog log, Petrinet net){
		ReplayRuposConnection connection = new ReplayRuposConnection(conformance, log, net);
		//context.getFutureResult(0).setLabel(connection.getLabel());
		return connection;
	}
	
}

