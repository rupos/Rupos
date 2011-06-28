package org.processmining.plugins.petrinet.replayfitness;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public class TotalPerformanceResult{
	PerformanceResult total;
	List<Map<Place,PerformanceResult>> list;

	public TotalPerformanceResult() {
		this.list = new Vector<Map<Place,PerformanceResult>>();
	}
	
	public PerformanceResult getTotal() {
		return total;
	}

	public void setTotal(PerformanceResult total) {
		this.total = total;
	}

	public List<Map<Place, PerformanceResult>> getList() {
		return list;
	}

	public void setList(List<Map<Place, PerformanceResult>> list) {
		this.list = list;
	}

	public String toString(){
		String out = "";
		int i=0;
		for(Map<Place, PerformanceResult> pr : list){
			out+="*****************************************\n";
			out+="Traccia:"+i+++"\n";
			out+=pr.toString()+"\n";
			out+="*****************************************\n";
		}
		return out;
		
	}
	
}