package org.processmining.plugins.bpmn;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExtImpl;
import org.processmining.plugins.petrinet.replayfitness.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;
import org.processmining.plugins.xpdl.Xpdl;

public class BPMNexportResult {
	
	TraslateBPMNResult traslateBpmnresult=null;
	TotalConformanceResult totalconformanceresult=null;
	TotalPerformanceResult totalPerformanceresult=null;
	BPMNDiagramExt BPMNtraslate= null;
	
	
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

	
	public BPMNDiagramExt getBPMNtraslate() {
		return BPMNtraslate;
	}

	public void setBPMNtraslate(BPMNDiagramExt bPMNtraslate) {
		BPMNtraslate = bPMNtraslate;
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
