package org.processmining.plugins.bpmn;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExtFactory;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.jgraph.ProMJGraphVisualizer;

@Plugin(name = "Visualize BPMNext", parameterLabels = { "BPMNext Diagram" }, returnLabels = { "BPMN Diagram ext Visualization" }, returnTypes = { JComponent.class })
@Visualizer
public class BPMNextVisualization {

	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent visualize(PluginContext context, BPMNDiagramExt bpmndiagram) {
		
		BPMNDiagramExt bpmndiagr = BPMNDiagramExtFactory.cloneBPMNDiagram(bpmndiagram);
		//BPMNDiagramExt bpmndiagr = bpmndiagram;
		 Activity a = bpmndiagr.getActivities().iterator().next();
		
		Artifacts source = bpmndiagr.addArtifacts("prova", ArtifactType.TEXTANNOATION,a.getParentSwimlane());
		bpmndiagr.addFlowAssociation(source, a, a.getParentSwimlane());
		
		Activity sx = bpmndiagr.addActivity("ed", false, true, false, false, false,a.getParentSwimlane());
		bpmndiagr.addFlow(a, sx, a.getParentSwimlane(), "pii");
		
		return ProMJGraphVisualizer.instance().visualizeGraph(context, bpmndiagr);
	}

}
