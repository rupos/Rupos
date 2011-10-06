package org.processmining.models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;



import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
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
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;

// objects of this type should be represented in the framework by the
// BPMNDiagram interface.
@SubstitutionType(substitutedType = BPMNDiagramExt.class)
public class BPMNDiagramExtImpl extends BPMNDiagramImpl
implements BPMNDiagramExt {


	protected final Set<Artifacts> artifacts;
	protected final Set<FlowAssociation> flowsassociation;
	//protected final Set<Association> association;

	public BPMNDiagramExtImpl(String label) {
		super(label);
		artifacts= new LinkedHashSet<Artifacts>();
		flowsassociation= new LinkedHashSet<FlowAssociation>();

	}

	
	protected BPMNDiagramExtImpl getEmptyClone() {
		return new BPMNDiagramExtImpl(getLabel());
	}


	protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph) {
		BPMNDiagramExt bpmndiagram = (BPMNDiagramExt) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		//mapping=(HashMap<DirectedGraphElement, DirectedGraphElement>) super.cloneFrom(graph);

		for (Swimlane s : bpmndiagram.getSwimlanes()) {
			if(mapping.containsKey(s)){
				mapping.put(s, addSwimlane(s.getLabel(), (Swimlane) mapping.get(s)));
			}else{
				mapping.put(s, addSwimlane(s.getLabel(), null));
			}
				
		}
		for (SubProcess s : bpmndiagram.getSubProcesses()) {
			if (s.getParentSubProcess() != null){
				if(mapping.containsKey(s.getParentSubProcess())){
				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed(), (SubProcess) mapping.get(s.getParentSubProcess())));
				}
			}else if (s.getParentSwimlane() != null){
				if(mapping.containsKey(s.getParentSwimlane())){
				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed(), (Swimlane) mapping.get(s.getParentSwimlane())));
				}
			}else

				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed()));
		}
		for (Activity a : bpmndiagram.getActivities()) {
			if (a.getParentSubProcess() != null){
				if(mapping.containsKey(a.getParentSubProcess())){
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed(), (SubProcess) mapping.get(a.getParentSubProcess())));
				}
			}else if (a.getParentSwimlane() != null){
				if(mapping.containsKey(a.getParentSwimlane())){
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed(), (Swimlane) mapping.get(a.getParentSwimlane())));
				}
			}else
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed()));
		}
		
		for (Event e : bpmndiagram.getEvents()) {
			if (e.getParentSubProcess() != null){
				if(mapping.containsKey(e.getParentSubProcess())){
				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								(SubProcess) mapping.get(e.getParentSubProcess()), e.getBoundingNode()));
				}
			}else if (e.getParentSwimlane() != null){
				if(mapping.containsKey(e.getParentSwimlane())){
				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								(Swimlane)	 mapping.get(e.getParentSwimlane()), e.getBoundingNode()));
				}
			}else
				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								e.getBoundingNode()));
		}
		for (Gateway g : bpmndiagram.getGateways()) {
			if (g.getParentSubProcess() != null){
				if(mapping.containsKey(g.getParentSubProcess())){
				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType(),(SubProcess) mapping.get(g.getParentSubProcess())));
				}
			}else if (g.getParentSwimlane() != null){
				if(mapping.containsKey(g.getParentSwimlane())){
				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType(),(Swimlane)	 mapping.get( g.getParentSwimlane())));
				}
			}else
				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType()));
		}

		for (Flow f : bpmndiagram.getFlows()) {
			if (f.getParentSubProcess() != null){
				if(mapping.containsKey(f.getParentSubProcess())){
				mapping.put(
						f,
						addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
								(SubProcess) mapping.get(f.getParentSubProcess()), f.getLabel()));
				}
			}else if (f.getParentSwimlane() != null){
				if(mapping.containsKey(f.getParentSwimlane())){
				mapping.put(
						f,
						addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
							(Swimlane)	 mapping.get(f.getParentSwimlane()), f.getLabel()));
				}
			}else
				mapping.put(
						f,
						addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
								f.getLabel()));
		}
		
		for (Artifacts a : bpmndiagram.getArtifacts()) {
			
			mapping.put(a, addArtifacts(a.getLabel(), a.getArtifactType(), (Swimlane)	 mapping.get(a.getParentSwimlane())));
		}
		for (FlowAssociation  f : bpmndiagram.getFlowAssociation()) {
			mapping.put(f, addFlowAssociation((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
					(Swimlane)	 mapping.get(f.getParentSwimlane())));
		}
		getAttributeMap().clear();
		AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
		

		/*for (Association  f : bpmndiagram.getAssociation()) {
			mapping.put((DirectedGraphElement)f, (DirectedGraphElement)addAssociation((AbstractGraphElement) mapping.get(f.getSource()), (AbstractGraphElement) mapping.get(f.getTarget())));
		}*/


		//getAttributeMap().clear();
		/*	AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}*/
		return mapping;
	}

	@SuppressWarnings("rawtypes")
	public void removeEdge( DirectedGraphEdge edge) {
		if (edge instanceof Flow) {
			flows.remove(edge);
		} else {if (edge instanceof FlowAssociation) {
			flowsassociation.remove(edge);
		} else {
			assert (false);
		}
		}
		graphElementRemoved(edge);
	}

	public Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> getEdges() {
		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
		edges.addAll(flows);
		edges.addAll(flowsassociation);
		return edges;
	}

	public Set<BPMNNode> getNodes() {
		Set<BPMNNode> nodes = new HashSet<BPMNNode>();
		nodes.addAll(activities);
		nodes.addAll(subprocesses);
		nodes.addAll(events);
		nodes.addAll(gateways);
		nodes.addAll(artifacts);
		nodes.addAll(swimlanes);
		return nodes;
	}

	public void removeNode(DirectedGraphNode node) {
		if (node instanceof Activity) {
			removeActivity((Activity) node);
		} else if (node instanceof SubProcess) {
			removeSubProcess((SubProcess) node);
		} else if (node instanceof Event) {
			removeEvent((Event) node);
		} else if (node instanceof Gateway) {
			removeGateway((Gateway) node);
		} else if (node instanceof Artifacts) {
			removeArtifact((Artifacts) node);
		}else if (node instanceof Swimlane) {
			removeSwimlane((Swimlane) node);
		}else {
			assert (false);
		}
	}




	public Artifacts addArtifacts(String label, ArtifactType artifactType,
			SubProcess parent) {
		Artifacts a = new Artifacts(this, label,artifactType , parent);
		artifacts.add(a);
		graphElementAdded(a);
		return a;
	}


	public Artifacts removeArtifact(Artifacts artifact) {
		removeSurroundingEdges(artifact);
		return removeNodeFromCollection(artifacts, artifact);
	}


	public Collection<Artifacts> getArtifacts() {

		return this.artifacts;
	}


	public FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target,
			SubProcess parent) {
		FlowAssociation f = new FlowAssociation(source, target, parent);
		flowsassociation.add(f);
		graphElementAdded(f);
		return f;
	}


	public Set<FlowAssociation> getFlowAssociation() {

		return Collections.unmodifiableSet(flowsassociation);
	}


	public Artifacts addArtifacts(String label, ArtifactType artifactType) {
		// TODO Auto-generated method stub
		Artifacts a = new Artifacts(this, label,artifactType);
		artifacts.add(a);
		graphElementAdded(a);
		return a;
	}


	public Artifacts addArtifacts(String label, ArtifactType artifactType,
			Swimlane parentSwimlane) {
		Artifacts a = new Artifacts(this, label,artifactType,parentSwimlane);
		artifacts.add(a);
		graphElementAdded(a);
		return a;
	}


	public FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target) {

		FlowAssociation f = new FlowAssociation(source, target);
		flowsassociation.add(f);
		graphElementAdded(f);
		return f;
	}


	public FlowAssociation addFlowAssociation(BPMNNode source, BPMNNode target,
			Swimlane parentSwimlane) {


		FlowAssociation f = new FlowAssociation(source, target, parentSwimlane);
		flowsassociation.add(f);
		graphElementAdded(f);
		return f;
	}



	/*@Override
	public Association addAssociation(AbstractGraphElement source,
			AbstractGraphElement target) {
		// TODO Auto-generated method stub
		Association f = new Association(source, target);
		association.add(f);
		graphElementAdded(f);
		return f;
	}

	@Override
	public Set<Association> getAssociation() {
		// TODO Auto-generated method stub
		return this.association;
	}*/

}
