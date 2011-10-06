package org.processmining.plugins.bpmn.exporting;




import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExtImpl;

import org.processmining.plugins.bpmn.TraslateBPMNResult;
import org.processmining.plugins.petrinet.replayfitness.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.performance.TotalPerformanceResult;
import org.processmining.plugins.xpdl.Xpdl;


@Plugin(name = "BPMNexportXPDLwithAnalisysDetails", parameterLabels = { "TraslateBPMNResult", "TotalConformanceResult" , "TotalPerformanceResult"}, returnLabels = { "BPMN conformance traslate" }, returnTypes = {
		BPMNexportResult.class }, userAccessible = true)
		public class BPMNexportXPDL {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMN")
	@PluginVariant(requiredParameterLabels = { 0, 1 }, variantLabel = "Exporting  File Conformance")
	public Object exportBPMNexportXPDL(PluginContext context, TraslateBPMNResult traslateBpmnresult, TotalConformanceResult totalconformanceresult) throws Exception {


		BPMNDiagramExt newbpmn = BPMNexportUtil.exportConformancetoBPMN(traslateBpmnresult,totalconformanceresult.getTotal());

	
		
		
		BPMNexportResult result = new BPMNexportResult(traslateBpmnresult, totalconformanceresult);
		result.setBPMNtraslate(newbpmn);
		

		return result;

	}

	

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMN")
	@PluginVariant(requiredParameterLabels = { 0, 2 }, variantLabel = "Exporting  File Performance")
	public Object exportBPMNexportXPDL(PluginContext context, TraslateBPMNResult traslateBpmnresult, TotalPerformanceResult totalPerformanceresult) throws Exception {


		
		BPMNDiagramExt newbpmn = BPMNexportUtil.exportPerformancetoBPMN(traslateBpmnresult,totalPerformanceresult.getListperformance().get(0).getList(),totalPerformanceresult.getListperformance().get(0).getMaparc());
		
		
		


		//return objects;
		BPMNexportResult result = new BPMNexportResult(traslateBpmnresult, totalPerformanceresult);
		result.setBPMNtraslate(newbpmn);
		

		return result;

	}



}




