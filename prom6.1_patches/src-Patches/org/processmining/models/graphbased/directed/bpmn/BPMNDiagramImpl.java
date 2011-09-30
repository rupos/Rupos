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
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;

// objects of this type should be represented in the framework by the
// BPMNDiagram interface.
@SubstitutionType(substitutedType = BPMNDiagram.class)
public class BPMNDiagramImpl extends AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>
		implements BPMNDiagram {

	protected final BPMNVendorSettings vendorSpecificSettings;
	protected final Set<Event> events;
	protected final Set<Activity> activities;
	protected final Set<SubProcess> subprocesses;
	protected final Set<Gateway> gateways;
	protected final Set<Flow> flows;
	protected final Set<Swimlane> swimlanes;

	public BPMNDiagramImpl(String label) {
		super();
		events = new LinkedHashSet<Event>();
		activities = new LinkedHashSet<Activity>();
		subprocesses = new LinkedHashSet<SubProcess>();
		gateways = new LinkedHashSet<Gateway>();
		flows = new LinkedHashSet<Flow>();
		vendorSpecificSettings = new BPMNVendorSettings();
		swimlanes = new LinkedHashSet<Swimlane>();
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected BPMNDiagramImpl getEmptyClone() {
		return new BPMNDiagramImpl(getLabel());
	}

	/*protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph) {
		BPMNDiagram bpmndiagram = (BPMNDiagram) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		for (Activity a : bpmndiagram.getActivities()) {
			if (a.getParentSubProcess() != null)
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed(), a.getParentSubProcess()));
			else if (a.getParentSwimlane() != null)
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed(), a.getParentSwimlane()));
			else
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed()));
		}
		for (SubProcess s : bpmndiagram.getSubProcesses()) {
			if (s.getParentSubProcess() != null)

				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed(), s.getParentSubProcess()));
			else if (s.getParentSwimlane() != null)

				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed(), s.getParentSwimlane()));
			else

				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed()));
		}
		for (Event e : bpmndiagram.getEvents()) {
			if (e.getParentSubProcess() != null)

				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								e.getParentSubProcess(), e.getBoundingNode()));

			else if (e.getParentSwimlane() != null)

				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								e.getParentSwimlane(), e.getBoundingNode()));

			else
				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								e.getBoundingNode()));
		}
		for (Gateway g : bpmndiagram.getGateways()) {
			if (g.getParentSubProcess() != null)

				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType(), g.getParentSubProcess()));
			else if (g.getParentSwimlane() != null)

				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType(), g.getParentSwimlane()));
			else
				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType()));
		}

		for (Flow f : bpmndiagram.getFlows()) {
			if (f.getParentSubProcess() != null)
				mapping.put(
						f,
						addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
								f.getParentSubProcess(), f.getLabel()));
			else if (f.getParentSwimlane() != null)
				mapping.put(
						f,
						addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
								f.getParentSwimlane(), f.getLabel()));
			else
				mapping.put(
						f,
						addFlow((BPMNNode) mapping.get(f.getSource()), (BPMNNode) mapping.get(f.getTarget()),
								f.getLabel()));
		}
		for (Swimlane s : bpmndiagram.getSwimlanes()) {
			mapping.put(s, addSwimlane(s.getLabel(), s.getParentSwimlane()));
		}
		getAttributeMap().clear();
		AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
		return mapping;
	}*/
	
	protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> graph) {
		BPMNDiagram bpmndiagram = (BPMNDiagram) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		for (Swimlane s : bpmndiagram.getSwimlanes()) {
			if(mapping.containsKey(s)){
				mapping.put(s, addSwimlane(s.getLabel(), (Swimlane) mapping.get(s)));
			}else{
				mapping.put(s, addSwimlane(s.getLabel(), null));
			}
				
		}
		
		for (Activity a : bpmndiagram.getActivities()) {
			if (a.getParentSubProcess() != null){
				if(mapping.containsKey(a.getParentSubProcess())){
				mapping.put(
						a,
						addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
								a.isBMultiinstance(), a.isBCollapsed(), (SubProcess) mapping.get(a.getParentSwimlane())));
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
		for (SubProcess s : bpmndiagram.getSubProcesses()) {
			if (s.getParentSubProcess() != null){
				if(mapping.containsKey(s.getParentSubProcess())){
				mapping.put(
						s,
						addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(),
								s.isBMultiinstance(), s.isBCollapsed(), (SubProcess) mapping.get(s.getParentSwimlane())));
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
		for (Event e : bpmndiagram.getEvents()) {
			if (e.getParentSubProcess() != null){
				if(mapping.containsKey(e.getParentSubProcess())){
				mapping.put(
						e,
						addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(),
								(SubProcess) mapping.get(e.getParentSwimlane()), e.getBoundingNode()));
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
				mapping.put(g, addGateway(g.getLabel(), g.getGatewayType(),(SubProcess) mapping.get(g.getParentSwimlane())));
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
								(SubProcess) mapping.get(f.getParentSwimlane()), f.getLabel()));
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
		
		getAttributeMap().clear();
		AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
		return mapping;
	}

	@SuppressWarnings("rawtypes")
	public void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof Flow) {
			flows.remove(edge);
		} else {
			assert (false);
		}
		graphElementRemoved(edge);
	}

	public Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> getEdges() {
		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = new HashSet<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>();
		edges.addAll(flows);
		return edges;
	}

	public Set<BPMNNode> getNodes() {
		Set<BPMNNode> nodes = new HashSet<BPMNNode>();
		nodes.addAll(activities);
		nodes.addAll(subprocesses);
		nodes.addAll(events);
		nodes.addAll(gateways);
		nodes.addAll(swimlanes);
		return nodes;
	}

	public void removeNode(DirectedGraphNode node) {
		if (node instanceof Activity) {
			removeActivity((Activity) node);
		} else if (node instanceof SubProcess) {
			removeSubProcess((SubProcess) node);
		} else if (node instanceof Swimlane) {
			removeSwimlane((Swimlane) node);
		} else if (node instanceof Event) {
			removeEvent((Event) node);
		} else if (node instanceof Gateway) {
			removeGateway((Gateway) node);
		} else {
			assert (false);
		}
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, Swimlane parentSwimlane) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
				parentSwimlane);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed, SubProcess parentSubProcess) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed,
				parentSubProcess);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, SubProcess parentSubProcess) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				parentSubProcess);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public SubProcess addSubProcess(String label, boolean looped, boolean adhoc, boolean compensation,
			boolean multiinstance, boolean collapsed, Swimlane parentSwimlane) {
		SubProcess s = new SubProcess(this, label, looped, adhoc, compensation, multiinstance, collapsed,
				parentSwimlane);
		subprocesses.add(s);
		graphElementAdded(s);
		return s;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			SubProcess parentSubProcess, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSubProcess, exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Event addEvent(String label, EventType eventType, EventTrigger eventTrigger, EventUse eventUse,
			Swimlane parentSwimlane, Activity exceptionFor) {
		Event e = new Event(this, label, eventType, eventTrigger, eventUse, parentSwimlane, exceptionFor);
		events.add(e);
		graphElementAdded(e);
		return e;
	}

	public Flow addFlow(BPMNNode source, BPMNNode target, SubProcess parent, String label) {
		Flow f = new Flow(source, target, parent, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public Flow addFlow(BPMNNode source, BPMNNode target, Swimlane parent, String label) {
		Flow f = new Flow(source, target, parent, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public Flow addFlow(BPMNNode source, BPMNNode target, String label) {
		Flow f = new Flow(source, target, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public Gateway addGateway(String label, GatewayType gatewayType, SubProcess parentSubProcess) {
		Gateway g = new Gateway(this, label, gatewayType, parentSubProcess);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Gateway addGateway(String label, GatewayType gatewayType, Swimlane parentSwimlane) {
		Gateway g = new Gateway(this, label, gatewayType, parentSwimlane);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Gateway addGateway(String label, GatewayType gatewayType) {
		Gateway g = new Gateway(this, label, gatewayType);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Swimlane addSwimlane(String label, Swimlane parentSwimlane) {
		Swimlane s = new Swimlane(this, label, parentSwimlane);
		swimlanes.add(s);
		graphElementAdded(s);
		return s;
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

	public Swimlane removeSwimlane(Swimlane swimlane) {
		removeSurroundingEdges(swimlane);
		return removeNodeFromCollection(swimlanes, swimlane);
	}

	public Collection<Swimlane> getSwimlanes() {
		return swimlanes;
	}

	public BPMNVendorSettings getVendorSpecificSettings() {
		return vendorSpecificSettings;
	}

}
