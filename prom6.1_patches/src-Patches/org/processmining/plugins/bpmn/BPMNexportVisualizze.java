package org.processmining.plugins.bpmn;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.petrinet.replayfitness.ReplayConformanceRuposConnection;
import org.processmining.plugins.petrinet.replayfitness.ReplayPerformanceRuposConnection;



@Visualizer
@Plugin(name = "BPMN export Visualizer", parameterLabels = "BPMN export visualizze Conformance or Performance Rupos Analisys", returnLabels = "BPMN export Visualized", returnTypes = JComponent.class)
public class BPMNexportVisualizze {

	@PluginVariant(requiredParameterLabels = { 0 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(UIPluginContext context, BPMNexportResult tovisualize) {
		if(context instanceof UIPluginContext){
			if(tovisualize.getTotalconformanceresult()!=null){
				try {
					ReplayConformanceRuposConnection connection = context.getConnectionManager().getFirstConnection(
							ReplayConformanceRuposConnection.class, context, tovisualize.getTotalconformanceresult());

					// connection found. Create all necessary component to instantiate inactive visualization panel
					XLog log = connection.getObjectWithRole(ReplayConformanceRuposConnection.XLOG);
					Petrinet netx = connection.getObjectWithRole(ReplayConformanceRuposConnection.PNET);
				
					return getVisualizationPanel(context, netx, log, tovisualize);

				} catch (ConnectionCannotBeObtained e) {
					// No connections available
					context.log("Connection does not exist", MessageLevel.DEBUG);
					return null;
				}
			}else{
				if(tovisualize.getTotalPerformanceresult()!=null){
					try {
						ReplayPerformanceRuposConnection connection = context.getConnectionManager().getFirstConnection(
								ReplayPerformanceRuposConnection.class, context, tovisualize.getTotalPerformanceresult());

						// connection found. Create all necessary component to instantiate inactive visualization panel
						XLog log = connection.getObjectWithRole(ReplayPerformanceRuposConnection.XLOG);
						Petrinet netx = connection.getObjectWithRole(ReplayPerformanceRuposConnection.PNET);
					
						return getVisualizationPanelPerf(context, netx, log, tovisualize);

					} catch (ConnectionCannotBeObtained e) {
						// No connections available
						context.log("Connection does not exist", MessageLevel.DEBUG);
						return null;
					}
				}else return null;
			}

		}else{
			return null;
		}

	}


	private JComponent getVisualizationPanelPerf(UIPluginContext context,
			Petrinet netx, XLog log, BPMNexportResult tovisualize) {
		Progress progress = context.getProgress();
		BPMNexportPanelPerformance panel = new BPMNexportPanelPerformance(context, netx, log, progress, tovisualize);
		return panel;
	}


	private JComponent getVisualizationPanel(UIPluginContext context,
			Petrinet net, XLog log,BPMNexportResult  tovisualize) {
		Progress progress = context.getProgress();

		
			BPMNexportPanelConformance panel = new BPMNexportPanelConformance(context, net, log, progress, tovisualize);
			return panel;


	}


}
