package org.processmining.plugins.petrinet.replayfitness.conformance;



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
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.petrinet.replayfitness.util.ReplayRuposConnection;


@Visualizer
@Plugin(name = "Conformance Result Visualizer", parameterLabels = "Conformance Rupos Analisys", returnLabels = "Visualized Fitness", returnTypes = JComponent.class)
public class ReplayConformancePluginRuposVisualizze {

	@PluginVariant(requiredParameterLabels = { 0 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(PluginContext context, TotalConformanceResult tovisualize) {
		if(context instanceof UIPluginContext){
			try {
				ReplayRuposConnection connection = context.getConnectionManager().getFirstConnection(
						ReplayRuposConnection.class, context, tovisualize);

				// connection found. Create all necessary component to instantiate inactive visualization panel
				XLog log = connection.getObjectWithRole(ReplayRuposConnection.XLOG);
				Petrinet netx = connection.getObjectWithRole(ReplayRuposConnection.PNET);
				
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
			Petrinet net, XLog log, TotalConformanceResult tovisualize) {
		Progress progress = context.getProgress();

		ReplayConformanceRuposPanel panel = new ReplayConformanceRuposPanel(context, net, log, progress, tovisualize);
		return panel;


	}


}
