package org.processmining.plugins.petrinet.replayfitness;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.connections.impl.AbstractConnection;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;


@Plugin(name = "Conformance Petrinet Log Connection Factory", parameterLabels = { "Conformace", "Log", "Petrinet" }, returnTypes = ReplayConformanceRuposConnection.class, returnLabels = "Conformance Rupos Petrinet Log connection", userAccessible = false)
@ConnectionObjectFactory
public class ReplayConformanceRuposConnection extends AbstractConnection {

	public static String FITNESS = "Fitness";
	public static String PNET = "Petrinet";
	public static String XLOG = "XLog";

	public ReplayConformanceRuposConnection(TotalConformanceResult fitness, XLog log, Petrinet net) {
		super("ConfromanceRuposConnection");
		put(FITNESS, fitness);
		put(XLOG, log);
		put(PNET, net);
	}
}