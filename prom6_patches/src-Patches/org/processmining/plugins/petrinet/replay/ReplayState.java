package org.processmining.plugins.petrinet.replay;

import java.util.List;
import java.util.UUID;

import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * The state in the search space of th ecost-based Petri net trace replayer. To
 * be able to sort states on their costs, this class implements
 * Comparable<ReplayState<C>>.
 * 
 * @author hverbeek
 * 
 * @param <C>
 *            The class implementing the cost structure. To be able to detect
 *            which cost is better, it needs to implement Comparable.
 */
public class ReplayState<C extends ReplayCost & Comparable<? super C>> implements Comparable<ReplayState<C>> {

	/**
	 * The previous state in this transition sequence.
	 */
	
	 ReplayState<C> parentState;

	public ReplayState<C> getParentState() {
		return parentState;
	}

	public void setParentState(ReplayState<C> parentState) {
		this.parentState = parentState;
	}

	public C getCost() {
		return cost;
	}

	public void setCost(C cost) {
		this.cost = cost;
	}

	/**
	 * The current marking in this state, that is, the marking after executing
	 * the transition sequence.
	 */
	Marking marking;

	/**
	 * The transition that was executed in-between the previous state and this
	 * state. May be null if this state resulted from the skipping of the head
	 * of the trace.
	 */
	Transition transition;

	/**
	 * The cost to get to this state.
	 */
	 C cost;

	/**
	 * The trace still to replay from this state.
	 */
	List<? extends Object> trace;

	/**
	 * A unique number.
	 */
	private final UUID id = UUID.randomUUID();

	/**
	 * Constructs a new state, given its predecessor state, the new marking, the
	 * transition leading from the predecessor state to the new state, the cost
	 * to go from the predecessor state to the new state, and the trace still
	 * left to replay from the new state.
	 * 
	 * @param state
	 *            The predecessor state of this state.
	 * @param marking
	 *            The marking of this state.
	 * @param transition
	 *            The transition leading from the predecessor state to this
	 *            state.
	 * @param cost
	 *            The cost to go from the predecessor state to this state.
	 * @param trace
	 *            the trace still left to replay from this state.
	 */
	public ReplayState(ReplayState<C> state, Marking marking, Transition transition, C cost,
			List<? extends Object> trace) {
		this.parentState = state;
		this.marking = marking;
		this.transition = transition;
		this.cost = cost;
		this.trace = trace;
	}

	/**
	 * Returns whether o equals this state.
	 */
	public boolean equals(Object o) {
		if (o instanceof ReplayState<?>) {
			return id.equals(((ReplayState<?>) o).id);
		}
		return false;
	}

	/**
	 * Returns the hash code of this state.
	 */
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * Compares this state to another state.
	 */
	public int compareTo(ReplayState<C> state) {
		int c = cost.compareTo(state.cost);
		if (c != 0) {
			return c;
		}
		/*
		 * Costs are equals. Enforce total order using unique id.
		 */
		return id.compareTo(state.id);
	}

	/**
	 * String representation of this state.
	 */
	public String toString() {
		return cost.toString();
	}
}
