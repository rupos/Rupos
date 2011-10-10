package org.processmining.plugins.bpmn.exporting;

import javax.swing.JComponent;


import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.plugins.bpmn.BPMNTraslatePanel;
import org.processmining.plugins.bpmn.BPMNtoPNConnection;

@Plugin(name = "Visualize BPMN to PN", parameterLabels = { "BPMNext Diagram" }, returnLabels = { "BPMN Diagram ext Visualization" }, returnTypes = { JComponent.class })
@Visualizer
public class BPMNtoPNVisualization {

	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent visualize(PluginContext context, BPMNDiagram bpmndiagram) {
		
		try {
			BPMNtoPNConnection connection = context.getConnectionManager().getFirstConnection(
					BPMNtoPNConnection.class, context, bpmndiagram);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			
			Petrinet netx = connection.getObjectWithRole(BPMNtoPNConnection.PNET);
			String error = connection.getObjectWithRole(BPMNtoPNConnection.ERRORLOG);
			Progress progress = context.getProgress();
			BPMNTraslatePanel panel = new BPMNTraslatePanel(context, progress, netx,bpmndiagram,  error);
			return panel;

		} catch (ConnectionCannotBeObtained e) {
			// No connections available
			context.log("Connection does not exist", MessageLevel.DEBUG);
			
			return ProMJGraphVisualizer.instance().visualizeGraph(context, bpmndiagram);
		}
		
		
		
	}

}
