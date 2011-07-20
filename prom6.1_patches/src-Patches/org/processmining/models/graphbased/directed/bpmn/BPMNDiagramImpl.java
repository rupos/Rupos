package org.processmining.models.graphbased.directed.bpmn;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

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

// objects of this type should be represented in the framework by the
// BPMNDiagram interface.
@SubstitutionType(substitutedType = BPMNDiagram.class)
public class BPMNDiagramImpl extends AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>
implements BPMNDiagram {

	protected final Set<Event> events;
	protected final Set<Activity> activities;
	protected final Set<SubProcess> subprocesses;
	protected final Set<Gateway> gateways;
	protected final Set<Flow> flows;
	protected final Set<Artifacts> artifacts;
	protected final Set<FlowAssociation> flowsassociation;
	//protected final Set<Association> association;
	
	public BPMNDiagramImpl(String label) {
		super();
		events = new LinkedHashSet<Event>();
		activities = new LinkedHashSet<Activity>();
		subprocesses = new LinkedHashSet<SubProcess>();
		gateways = new LinkedHashSet<Gateway>();
		flows = new LinkedHashSet<Flow>();
		artifacts= new LinkedHashSet<Artifacts>();
		flowsassociation= new LinkedHashSet<FlowAssociation>();
		//association= new LinkedHashSet<Association>();
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected BPMNDiagramImpl getEmptyClone() {
		return new BPMNDiagramImpl(getLabel());
	}

	
	protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph) {
		BPMNDiagram bpmndiagram = (BPMNDiagram) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		for (Activity a : bpmndiagram.getActivities()) {
			mapping.put(a, addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(), a
					.isBMultiinstance(), a.isBCollapsed(), a.getParent()));
		}
		for (SubProcess s : bpmndiagram.getSubProcesses()) {
			mapping.put(s, addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(), s
					.isBMultiinstance(), s.isBCollapsed(), s.getParent()));
		}
		for (Event e : bpmndiagram.getEvents()) {
			mapping.put(e, addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
					e.getParent(), e.getBoundingNode()));
		}
		for (Gateway g : bpmndiagram.getGateways()) {
			mapping.put(g, addGateway(g.getLabel(), g.getGatewayType(), g.getParent()));
		}
		for (Artifacts a : bpmndiagram.getArtifacts()) {
			mapping.put(a, addArtifacts(a.getLabel(), a.getArtifactType(), a.getParent()));
		}
		for (FlowAssociation  f : bpmndiagram.getFlowAssociation()) {
			mapping.put(f, addFlowAssociation((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
					(SubProcess) f.getParent()));
		}/*for (Association  f : bpmndiagram.getAssociation()) {
			mapping.put((DirectedGraphElement)f, (DirectedGraphElement)addAssociation((AbstractGraphElement) mapping.get(f.getSource()), (AbstractGraphElement) mapping.get(f.getTarget())));
		}*/
		for (Flow f : bpmndiagram.getFlows()) {
			mapping.put(f, addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
					(SubProcess) f.getParent()));
		}

		getAttributeMap().clear();
		AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
		return mapping;
	}

	@SuppressWarnings("unchecked")
	public void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof Flow) {
			flows.remove(edge);
		} else {if (edge instanceof FlowAssociation) {
			flowsassociation.remove(edge);
		} else {
			assert (false);
		}}
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
		} else {
			assert (false);
		}
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parent) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed, parent);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, SubProcess parent) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed, parent);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parent, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parent, exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent) {
		Flow f = new Flow(source, target, parent);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public Gateway addGateway(String label, GatewayType gatewayType, SubProcess parent) {
		Gateway g = new Gateway(this, label, gatewayType, parent);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Collection<Activity> getActivities() {
		return activities;
	}

	public Collection<SubProcess> getSubProcesses() {
		return subprocesses;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public Set<Flow> getFlows() {
		return Collections.unmodifiableSet(flows);
	}

	public Collection<Gateway> getGateways() {
		return gateways;
	}

	public Activity removeActivity(Activity activity) {
		removeSurroundingEdges(activity);
		return removeNodeFromCollection(activities, activity);
	}

	public Activity removeSubProcess(SubProcess subprocess) {
		//TODO: it is probably necessary to remove all nodes that are contained in the subprocess as well 
		removeSurroundingEdges(subprocess);
		return removeNodeFromCollection(subprocesses, subprocess);
	}

	public Event removeEvent(Event event) {
		removeSurroundingEdges(event);
		return removeNodeFromCollection(events, event);
	}

	public Gateway removeGateway(Gateway gateway) {
		removeSurroundingEdges(gateway);
		return removeNodeFromCollection(gateways, gateway);
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
