package org.processmining.plugins.petrinet.replay.performance;

import java.util.List;
import java.util.Vector;


public class TotalPerformanceResult{
	PerformanceData total;
	
	List<PerformanceResult> listperformance;

	public TotalPerformanceResult() {
		
		this.listperformance = new Vector<PerformanceResult>();
	}
	
	public List<PerformanceResult> getListperformance() {
		return listperformance;
	}

	public void setListperformance(List<PerformanceResult> listperformance) {
		this.listperformance = listperformance;
	}

	public PerformanceData getTotal() {
		return total;
	}

	public void setTotal(PerformanceData total) {
		this.total = total;
	}

	

	public String toString(){
		String out = "";
		int i=0;
		
		for(PerformanceResult pr : listperformance){
			out+="*****************************************\n";
			out+="Traccia:"+i+++"\n";
			out+=pr.toString()+"\n";
			out+="*****************************************\n";
		}
		return out;
		
	}
	
}