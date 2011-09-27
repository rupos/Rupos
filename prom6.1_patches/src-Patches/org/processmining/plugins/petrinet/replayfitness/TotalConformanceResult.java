package org.processmining.plugins.petrinet.replayfitness;

import java.util.Iterator;
import java.util.List;


public class TotalConformanceResult {
	
	ConformanceResult total;
	List<ConformanceResult> list;
	String ret = System.getProperty("line.separator");
	
	
	public ConformanceResult getTotal() {
		return total;
	}
	public void setTotal(ConformanceResult total) {
		this.total = total;
	}
	public List<ConformanceResult> getList() {
		return list;
	}
	public void setList(List<ConformanceResult> list) {
		this.list = list;
	}
	
	public String toString(){
		String tot = ""; 
		Iterator<ConformanceResult> iter = list.iterator();
		Integer index = 1;
		while (iter.hasNext()) {
		    ConformanceResult fitnessResult = (ConformanceResult) iter.next();
		    tot += "------------ Traccia n."+(index++)+" ------------"+ret;
		    tot += fitnessResult;
		    tot += "-----------------------------------"+ret;
		}
                tot += "------------ TOTALE SU TUTTE LE TRACCE: ------------"+ret;
		tot += total;
                tot += "-----------------------------------"+ret;

		return tot;
	}
	
	

}
