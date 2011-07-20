package org.processmining.models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.Set;

import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.FlowAssociation;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;

public interface BPMNDiagram extends DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> {

	String getLabel();

	//Activities
	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed, SubProcess parent);

	Activity removeActivity(Activity activity);

	Collection<Activity> getActivities();

	//SubProcesses
	SubProcess addSubProcess(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parent);

	Activity removeSubProcess(SubProcess subprocess);

	Collection<SubProcess> getSubProcesses();

	//Events
	Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse, SubProcess parent,
			Activity exceptionFor);

	Event removeEvent(Event event);

	Collection<Event> getEvents();

	//Gateways
	Gateway addGateway(String label, GatewayType gatewayType, SubProcess parent);

	Gateway removeGateway(Gateway gateway);

	Collection<Gateway> getGateways();
	
	//Artifacts
	Artifacts addArtifacts(String label, ArtifactType artifactType, SubProcess parent);

	Artifacts removeArtifact(Artifacts artifacts);

	Collection<Artifacts> getArtifacts();
	
	//FlowAssociation
	FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target, SubProcess parent);

	Set<FlowAssociation> getFlowAssociation();
	
	/*//Association
	Association addAssociation(AbstractGraphElement source, AbstractGraphElement target);

	Set<Association> getAssociation();*/

	//Flows
	Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent);

	Set<Flow> getFlows();
}
