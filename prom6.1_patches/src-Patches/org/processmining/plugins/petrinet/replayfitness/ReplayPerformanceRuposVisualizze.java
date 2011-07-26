package org.processmining.plugins.petrinet.replayfitness;


import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.StringVisualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;


@Visualizer
@Plugin(name = "Performance Result Visualizer", parameterLabels = "Performance Rupos Analisys", returnLabels = "Visualized Performance", returnTypes = JComponent.class)
public class ReplayPerformanceRuposVisualizze {

	@PluginVariant(requiredParameterLabels = { 0 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(PluginContext context, TotalPerformanceResult tovisualize) {
		if(context instanceof UIPluginContext){
			try {
				ReplayPerformanceRuposConnection connection = context.getConnectionManager().getFirstConnection(
						ReplayPerformanceRuposConnection.class, context, tovisualize);

				// connection found. Create all necessary component to instantiate inactive visualization panel
				XLog log = connection.getObjectWithRole(ReplayPerformanceRuposConnection.XLOG);
				Petrinet netx = connection.getObjectWithRole(ReplayPerformanceRuposConnection.PNET);
				

				return getVisualizationPanel(context, netx, log, tovisualize);

			} catch (ConnectionCannotBeObtained e) {
				// No connections available
				context.log("Connection does not exist", MessageLevel.DEBUG);
				return null;
			}
		}else {

			return StringVisualizer.visualize(context, tovisualize.toString());
		}

	}



	private JComponent getVisualizationPanel(PluginContext context,
			Petrinet net, XLog log, TotalPerformanceResult tovisualize) {
		Progress progress = context.getProgress();
		
		ReplayPerformanceRuposPanel panel = new ReplayPerformanceRuposPanel(context, net, log, progress, tovisualize);
		
		return panel;


	}
	

}
