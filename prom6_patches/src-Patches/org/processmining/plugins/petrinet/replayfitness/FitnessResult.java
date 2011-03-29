package org.processmining.plugins.petrinet.replayfitness;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

public class FitnessResult {
	private double fitness;
	private Marking remainingMarking = null;
	private Marking missingMarking = null;
	private Map<Transition, Integer> mapTransition; 
	private Map<Arc, Integer> mapArc;
	private String ret = System.getProperty("line.separator");
    
    public FitnessResult(){
    	remainingMarking = new Marking();
    	missingMarking = new Marking();
    	mapTransition = new HashMap<Transition, Integer>();
    	mapArc = new HashMap<Arc, Integer>(); 
    	
    }
    
    
    
	public double getFitness() {
		return fitness;
	}
	public void setFitness(long fitness) {
		this.fitness = fitness;
	}
	public Marking getRemainingMarking() {
		return remainingMarking;
	}
	public void setRemainingMarking(Marking remainingMarking) {
		this.remainingMarking = remainingMarking;
	}
	public Marking getMissingMarking() {
		return missingMarking;
	}
	public void setMissingMarking(Marking missingMarking) {
		this.missingMarking = missingMarking;
	}
	public Map<Transition, Integer> getMapTransition() {
		return mapTransition;
	}
	public void setMapTransition(Map<Transition, Integer> mapTransition) {
		this.mapTransition = mapTransition;
	}
	public Map<Arc, Integer> getMapArc() {
		return mapArc;
	}
	public void setMapArc(Map<Arc, Integer> mapArc) {
		this.mapArc = mapArc;
	} 
	public void set(int producedTokens, int consumedTokens, int missingTokens, int remainingTokens) {
		fitness = 1.0 - missingTokens / (2.0 * consumedTokens) - remainingTokens / (2.0 * producedTokens);
		
	}
    
	public String toString() {
		String tot = "Fitness totale:" +getFitness()+ret;
		tot+="Missing Marking:"+getMissingMarking()+ret;
		tot+="Remaning Marking: "+ getRemainingMarking()+ret;
		tot+="Transizioni che non fittano:"+ret;
		Iterator iter = getMapTransition().keySet().iterator();
		while (iter.hasNext()) {
		    Transition t = (Transition)iter.next();
		    Integer i = getMapTransition().get(t);
		    tot += "     "+t+" : "+i+" tracce"+ret;
		}
		tot+="Attivazioni degli archi:"+ret;
		Iterator iterArc = getMapArc().keySet().iterator();
		while (iterArc.hasNext()) {
		    Arc a = (Arc)iterArc.next();
		    String asString = "FROM "+a.getSource()+" TO "+a.getTarget();
		    Integer i = getMapArc().get(a);
		    tot += "     "+asString+" : "+i+" attivazioni"+ret;
		}
		return tot;
	}

}
