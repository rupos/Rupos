package org.processmining.plugins.petrinet.replayfitness;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replay.ReplayCost;
import org.processmining.plugins.petrinet.replay.ReplayCostAddOperator;
import org.processmining.plugins.petrinet.replay.ReplayState;

public class ReplayFitnessCost implements ReplayCost, Comparable<ReplayFitnessCost> {

	private Integer cost;
	private final ReplayFitnessSetting setting;
	private ReplayState<ReplayFitnessCost>  parentstate;

	public ReplayFitnessCost(Integer cost, ReplayFitnessSetting setting) {
		this.cost = cost;
		this.setting = setting;
		
	}

	public static ReplayCostAddOperator<ReplayFitnessCost> addOperator = new ReplayCostAddOperator<ReplayFitnessCost>() {
		public ReplayFitnessCost add(ReplayFitnessCost cost, ReplayAction action, Transition transition, Object object) {
			ReplayFitnessCost newCost = new ReplayFitnessCost(cost.cost, cost.setting);
			newCost.cost += cost.setting.getWeight(action);
			return newCost;
		}
	};

	public void setParent(ReplayState<ReplayFitnessCost> tmp){
		this.parentstate=tmp;
		
	}
	
	public int compareTo(ReplayFitnessCost cost) {
		
		int diff = this.cost.compareTo(cost.cost);
		if (diff != 0)
			return diff;
		if (this.parentstate.getParentState() == null && cost.parentstate.getParentState() == null)
			return 0;
		return this.parentstate.getParentState().getCost().compareTo(cost.parentstate.getParentState().getCost());
		
		/*
		float diff = this.cost.compareTo(cost.cost);
		if (Math.abs(diff/this.cost) > 0.1)
			return (int) diff;
		if (this.parentstate.parentState == null && cost.parentstate.parentState == null)
			return 0;
		if (this.parentstate.parentState == null)
			return -1;
		if (cost.parentstate.parentState == null)
			return 1;
		return this.parentstate.parentState.cost.compareTo(cost.parentstate.parentState.cost);
		*/
	}

	public boolean isAcceptable() {
		return true;
	}

	public int hashCode() {
		return cost.hashCode();
	}

	public String toString() {
		return cost.toString();
	}

	public boolean equals(Object o) {
		if (o instanceof ReplayFitnessCost) {
			return cost.equals(((ReplayFitnessCost) o).cost);
		}
		return false;
	}
}
