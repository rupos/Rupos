package org.processmining.plugins.petrinet.replayfitness;

import java.util.Iterator;
import java.util.List;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;

public class TotalFitnessResult {
	
	FitnessResult total;
	List<FitnessResult> list;
	String ret = System.getProperty("line.separator");
	
	
	public FitnessResult getTotal() {
		return total;
	}
	public void setTotal(FitnessResult total) {
		this.total = total;
	}
	public List<FitnessResult> getList() {
		return list;
	}
	public void setList(List<FitnessResult> list) {
		this.list = list;
	}
	
	public String toString(){
		String tot = ""; 
		Iterator<FitnessResult> iter = list.iterator();
		Integer index = 1;
		while (iter.hasNext()) {
		    FitnessResult fitnessResult = (FitnessResult) iter.next();
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
