package org.processmining.plugins.bpmn;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.petrinet.replayfitness.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;
import org.processmining.plugins.xpdl.Xpdl;

public class BPMNexportResult {
	
	TraslateBPMNResult traslateBpmnresult=null;
	TotalConformanceResult totalconformanceresult=null;
	TotalPerformanceResult totalPerformanceresult=null;
	BPMNDiagram BPMNtraslate= null;
	Xpdl xpdltraslate=null;
	
	public BPMNexportResult(TraslateBPMNResult traslateBpmnresult,
			TotalConformanceResult totalconformanceresult) {
		this.traslateBpmnresult = traslateBpmnresult;
		this.totalconformanceresult = totalconformanceresult;
	}

	public BPMNexportResult(TraslateBPMNResult traslateBpmnresult,
			TotalPerformanceResult totalPerformanceresult) {
		super();
		this.traslateBpmnresult = traslateBpmnresult;
		this.totalPerformanceresult = totalPerformanceresult;
	}

	
	public BPMNDiagram getBPMNtraslate() {
		return BPMNtraslate;
	}

	public void setBPMNtraslate(BPMNDiagram bPMNtraslate) {
		BPMNtraslate = bPMNtraslate;
	}

	public Xpdl getXpdltraslate() {
		return xpdltraslate;
	}

	public void setXpdltraslate(Xpdl xpdltraslate) {
		this.xpdltraslate = xpdltraslate;
	}

	public TraslateBPMNResult getTraslateBpmnresult() {
		return traslateBpmnresult;
	}

	public TotalConformanceResult getTotalconformanceresult() {
		return totalconformanceresult;
	}

	public TotalPerformanceResult getTotalPerformanceresult() {
		return totalPerformanceresult;
	}
	
	
	
	

}
