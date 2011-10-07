package org.processmining.plugins.bpmn.exporting;




import java.util.Map;


import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

import org.processmining.plugins.bpmn.BPMNtoPNConnection;

import org.processmining.plugins.petrinet.replayfitness.conformance.ConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.performance.PerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.performance.TotalPerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.util.ReplayRuposConnection;



@Plugin(name = "BPMNMeasureswithAnalisysDetails", parameterLabels = {  "TotalConformanceResult" , "TotalPerformanceResult" , "ConformanceResult" , "Petrinets"}, returnLabels = { "BPMN  traslate" }, returnTypes = {
		BPMNDiagramExt.class }, userAccessible = true)
		public class MeasuresIntoBPMNPlugin {
	
	private BPMNDiagram bpmnext;

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMNMeasures")
	@PluginVariant(requiredParameterLabels = { 0 }, variantLabel = "Exporting  Total Conformance to BPMN")
	public Object exportBPMNexportXPDL(PluginContext context, TotalConformanceResult totalconformanceresult) throws Exception {

		try {
			ReplayRuposConnection connection = context.getConnectionManager().getFirstConnection(
					ReplayRuposConnection.class, context, totalconformanceresult);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			// log = connection.getObjectWithRole(ReplayRuposConnection.XLOG);
			Petrinet net= connection.getObjectWithRole(ReplayRuposConnection.PNET);
		
			BPMNtoPNConnection connection2 = context.getConnectionManager().getFirstConnection(
					BPMNtoPNConnection.class, context, net);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			
		    BPMNDiagram bpmn = connection2.getObjectWithRole(BPMNtoPNConnection.BPMN);
		    Map<String,Place> placeMap = connection2.getObjectWithRole(BPMNtoPNConnection.MAPARCTOPLACE);
			
			bpmnext = BPMNDecorateUtil.exportConformancetoBPMN(bpmn, net, totalconformanceresult.getTotal(), placeMap);
			


		} catch (ConnectionCannotBeObtained e) {
			// No connections available
			context.log("Connection does not exist", MessageLevel.DEBUG);
			
		}
		

		return bpmnext;

	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMNMeasures")
	@PluginVariant(requiredParameterLabels = { 2,3 }, variantLabel = "Exporting  Conformance to BPMN")
	public Object exportBPMNexportXPDL(PluginContext context,Petrinet net, ConformanceResult conformanceresult) throws Exception {

		try {
			/*ReplayRuposConnection connection = context.getConnectionManager().getFirstConnection(
					ReplayRuposConnection.class, context, totalconformanceresult);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			// log = connection.getObjectWithRole(ReplayRuposConnection.XLOG);
			Petrinet net= connection.getObjectWithRole(ReplayRuposConnection.PNET);*/
		
			BPMNtoPNConnection connection2 = context.getConnectionManager().getFirstConnection(
					BPMNtoPNConnection.class, context, net);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			
		    BPMNDiagram bpmn = connection2.getObjectWithRole(BPMNtoPNConnection.BPMN);
		    Map<String,Place> placeMap = connection2.getObjectWithRole(BPMNtoPNConnection.MAPARCTOPLACE);
			
			bpmnext = BPMNDecorateUtil.exportConformancetoBPMN(bpmn, net, conformanceresult, placeMap);
			


		} catch (ConnectionCannotBeObtained e) {
			// No connections available
			context.log("Connection does not exist", MessageLevel.DEBUG);
			
		}
		

		return bpmnext;

	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMNMeasures")
	@PluginVariant(requiredParameterLabels = { 1 }, variantLabel = "BPMN Performance traslate")
	public Object exportBPMNexportXPDL(PluginContext context,  TotalPerformanceResult totalPerformanceresult) throws Exception {

		try {
			ReplayRuposConnection connection = context.getConnectionManager().getFirstConnection(
					ReplayRuposConnection.class, context, totalPerformanceresult);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			//XLog log = connection.getObjectWithRole(ReplayRuposConnection.XLOG);
			Petrinet net= connection.getObjectWithRole(ReplayRuposConnection.PNET);
		
			BPMNtoPNConnection connection2 = context.getConnectionManager().getFirstConnection(
					BPMNtoPNConnection.class, context, net);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			
		   BPMNDiagram bpmn = connection2.getObjectWithRole(BPMNtoPNConnection.BPMN);
		   Map<String, Place>  placeMap = connection2.getObjectWithRole(BPMNtoPNConnection.MAPARCTOPLACE);
			
		   //cambiare con total
		   return BPMNDecorateUtil.exportPerformancetoBPMN(bpmn,  totalPerformanceresult.getListperformance().get(0), placeMap,net);
			
			 

		} catch (ConnectionCannotBeObtained e) {
			// No connections available
			context.log("Connection does not exist", MessageLevel.DEBUG);
			return null;
		}

	}
	
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMNMeasures")
	@PluginVariant(requiredParameterLabels = { 1 }, variantLabel = "BPMN Performance traslate")
	public Object exportBPMNexportXPDL(PluginContext context,  PerformanceResult Performanceresult,Petrinet net) throws Exception {

		try {
			
			
		
			BPMNtoPNConnection connection2 = context.getConnectionManager().getFirstConnection(
					BPMNtoPNConnection.class, context, net);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			
		   BPMNDiagram bpmn = connection2.getObjectWithRole(BPMNtoPNConnection.BPMN);
		   Map<String, Place>  placeMap = connection2.getObjectWithRole(BPMNtoPNConnection.MAPARCTOPLACE);
			
		   
		   return BPMNDecorateUtil.exportPerformancetoBPMN(bpmn,  Performanceresult, placeMap,net);
			
			 

		} catch (ConnectionCannotBeObtained e) {
			// No connections available
			context.log("Connection does not exist", MessageLevel.DEBUG);
			return null;
		}

	}


}




