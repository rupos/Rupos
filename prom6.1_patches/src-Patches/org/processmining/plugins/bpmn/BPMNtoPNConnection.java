package org.processmining.plugins.bpmn;

import java.util.LinkedHashMap;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public class BPMNtoPNConnection extends AbstractStrongReferencingConnection{

	public static String BPMN = "BPMN";
	public static String PNET = "Petrinet";
	public static String ERRORLOG = "ErrorLog";
	public static String MAPARCTOPLACE = "MapArcToPlace";
	
	
	
	public BPMNtoPNConnection(BPMNDiagram bpmn, PetrinetGraph net,
			String error) {
		super("BPMNtoPNConnection");
		put(BPMN, bpmn);
		put(PNET, net);
		put(ERRORLOG, error);
		
	}
	public BPMNtoPNConnection(BPMNDiagram bpmn, PetrinetGraph net,
			String error, LinkedHashMap<String, Place> placeMap) {
		super("BPMNtoPNConnection");
		put(BPMN, bpmn);
		putStrong(MAPARCTOPLACE, placeMap);
		put(PNET, net);
		put(ERRORLOG, error);
	}

	@PluginVariant(requiredParameterLabels = { 0, 1 ,2})
	public static BPMNtoPNConnection connect(PluginContext context,BPMNDiagram bpmn, PetrinetGraph net,
			String error){
		BPMNtoPNConnection connection = new BPMNtoPNConnection(bpmn,  net,error);
		//context.getFutureResult(0).setLabel(connection.getLabel());
		return connection;
	}



}
