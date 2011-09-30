package org.processmining.models.graphbased.directed.bpmn;


import java.util.Collection;
import java.util.Set;


import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.FlowAssociation;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;

import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;

public interface BPMNDiagramExt extends BPMNDiagram {

	//Artifacts
	Artifacts addArtifacts(String label, ArtifactType artifactType);
	
	Artifacts addArtifacts(String label, ArtifactType artifactType, SubProcess parent);
	
	Artifacts addArtifacts(String label, ArtifactType artifactType, Swimlane parentSwimlane);

	Artifacts removeArtifact(Artifacts artifacts);

	Collection<Artifacts> getArtifacts();
	
	//FlowAssociation
	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target);
	
	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target, SubProcess parent);
	
	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target, Swimlane parentSwimlane);

	Set<FlowAssociation> getFlowAssociation();
	
	/*//Association
	Association addAssociation(AbstractGraphElement source, AbstractGraphElement target);

	Set<Association> getAssociation();*/

	
}
