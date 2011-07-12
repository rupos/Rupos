package org.processmining.plugins.petrinet.replayfitness;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;


@Plugin(name = "Performance Petrinet Log Connection Factory", parameterLabels = { "Performace", "Log", "Petrinet" }, returnTypes = ReplayPerformanceRuposConnection.class, returnLabels = "Performance Rupos Petrinet Log connection", userAccessible = false)
@ConnectionObjectFactory
public class ReplayPerformanceRuposConnection extends AbstractConnection {

	public static String PERFORMANCE = "Performance";
	public static String PNET = "Petrinet";
	public static String XLOG = "XLog";

	public ReplayPerformanceRuposConnection(TotalPerformanceResult performance, XLog log, Petrinet net) {
		super("PerformanceRuposConnection");
		put(PERFORMANCE, performance);
		put(XLOG, log);
		put(PNET, net);
	}
}