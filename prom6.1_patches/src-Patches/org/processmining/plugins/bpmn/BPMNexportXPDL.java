package org.processmining.plugins.bpmn;




import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import org.processmining.plugins.petrinet.replayfitness.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;
import org.processmining.plugins.xpdl.Xpdl;


@Plugin(name = "BPMNexportXPDLwithAnalisysDetails", parameterLabels = { "TraslateBPMNResult", "TotalConformanceResult" , "TotalPerformanceResult"}, returnLabels = { "BPMN conformance traslate" }, returnTypes = {
		BPMNexportResult.class }, userAccessible = true)
		public class BPMNexportXPDL {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMN")
	@PluginVariant(requiredParameterLabels = { 0, 1 }, variantLabel = "Exporting  File Conformance")
	public Object exportBPMNexportXPDL(PluginContext context, TraslateBPMNResult traslateBpmnresult, TotalConformanceResult totalconformanceresult) throws Exception {


		BPMNDiagram newbpmn = BPMNexportUtil.exportConformancetoBPMN(traslateBpmnresult,totalconformanceresult.getTotal());

		Xpdl newxpdl =	BPMNexportUtil.exportToXpdl(context,traslateBpmnresult.getXpdl(),traslateBpmnresult,newbpmn);

		Object[] objects = new Object[2];
		objects[0] = newbpmn;
		objects[1] = newxpdl;
		
		BPMNexportResult result = new BPMNexportResult(traslateBpmnresult, totalconformanceresult);
		result.setBPMNtraslate(newbpmn);
		result.setXpdltraslate(newxpdl);

		return result;

	}

	

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "GOS", email = "Di.unipi", pack = "BPMN")
	@PluginVariant(requiredParameterLabels = { 0, 2 }, variantLabel = "Exporting  File Performance")
	public Object exportBPMNexportXPDL(PluginContext context, TraslateBPMNResult traslateBpmnresult, TotalPerformanceResult totalPerformanceresult) throws Exception {


		//BPMNDiagram newbpmn= exportPerformancetoBPMN(traslateBpmnresult,totalPerformanceresult.getListperformance().get(0).getList(),totalPerformanceresult.getListperformance().get(0).getMaparc());

		BPMNDiagram newbpmn = BPMNexportUtil.exportPerformancetoBPMN(traslateBpmnresult,totalPerformanceresult.getListperformance().get(0).getList(),totalPerformanceresult.getListperformance().get(0).getMaparc());
		
		Xpdl newxpdl =	BPMNexportUtil.exportToXpdl(context,traslateBpmnresult.getXpdl(),traslateBpmnresult,newbpmn);

		Object[] objects = new Object[2];
		objects[0] = newbpmn;
		objects[1] = newxpdl;

		//return objects;
		BPMNexportResult result = new BPMNexportResult(traslateBpmnresult, totalPerformanceresult);
		result.setBPMNtraslate(newbpmn);
		result.setXpdltraslate(newxpdl);

		return result;

	}



}




