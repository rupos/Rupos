package org.processmining.plugins.bpmn.exporting;


import java.util.Map;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.plugins.bpmn.BPMNtoPNConnection;
import org.processmining.plugins.petrinet.replay.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replay.performance.TotalPerformanceResult;
import org.processmining.plugins.petrinet.replay.util.ReplayAnalysisConnection;



@Visualizer
@Plugin(name = "Visualizer BPMN Measure", parameterLabels = "BPMN visualizze Conformance or Performance Rupos Analisys", returnLabels = "BPMN Measure Visualized", returnTypes = JComponent.class)
public class BPMNMeasureVisulizze {

	

	@PluginVariant(requiredParameterLabels = { 0 },variantLabel="BPMN")
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(UIPluginContext context, TotalPerformanceResult tovisualize) {
		Progress progress = context.getProgress();
		BPMNMeasuresPanelPerformance panel = new BPMNMeasuresPanelPerformance(context,   tovisualize);
		return panel;
	}


	@PluginVariant(requiredParameterLabels = { 0 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(UIPluginContext context, TotalConformanceResult resultc) {
		
			
		Progress progress = context.getProgress();

		
		BPMNMeasuresPanelConformance panel = new BPMNMeasuresPanelConformance(context, resultc);
		return panel;

	}


}
