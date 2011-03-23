package org.processmining.plugins.petrinet.replayfitness;

import java.util.HashMap;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

public class FitnessResult {
	private double fitness;
	Marking remainingMarking = null;
    Marking missingMarking = null;
    Map<Transition, Integer> mapTransiction; 
    Map<Arc, Integer> mapArc;
    
    public FitnessResult(){
    	remainingMarking = new Marking();
    	missingMarking = new Marking();
    	mapTransiction = new HashMap<Transition, Integer>();
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
	public Map<Transition, Integer> getMapTransiction() {
		return mapTransiction;
	}
	public void setMapTransiction(Map<Transition, Integer> mapTransiction) {
		this.mapTransiction = mapTransiction;
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
    

}
