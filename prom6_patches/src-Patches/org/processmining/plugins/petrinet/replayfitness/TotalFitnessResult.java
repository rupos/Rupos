package org.processmining.plugins.petrinet.replayfitness;

import java.util.Iterator;
import java.util.List;

public class TotalFitnessResult {
	
	FitnessResult total;
	List<FitnessResult> list;
	
	
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
		String ret = System.getProperty("line.separator");
		String tot = "Totale:"; 
		tot += ret+serializeFitnessResult(total);
		Iterator<FitnessResult> iter = list.iterator();
		Integer index = 1;
		while (iter.hasNext()) {
			tot += "Traccia n."+index++;
			FitnessResult fitnessResult = (FitnessResult) iter.next();
			tot += ret;
			tot += serializeFitnessResult(fitnessResult);
		}
		return tot;
	}
	
	
	private String serializeFitnessResult(FitnessResult result) {
		String ret = System.getProperty("line.separator");
		String tot = "Fitness totale:" +result.getFitness();
		tot+=ret+"Missing Marking:"+result.getMissingMarking();
		tot+=ret+"Remaning Marking: "+ result.getRemainingMarking();
		tot +=ret;
		return tot;
	}

}
