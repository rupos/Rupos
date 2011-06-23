package org.processmining.plugins.petrinet.replay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.framework.plugin.Progress;
import org.processmining.framework.util.search.NodeExpander;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.IllegalTransitionException;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessCost;

/**
 * Expands the current state of the cost-based Petri net trace replayer.
 * 
 * @author hverbeek
 * 
 * @param <C>
 *            The class implementing the cost structure. To be able to detect
 *            which cost is better, it needs to implement Comparable.
 */
public class ReplayStateExpander<C extends ReplayCost & Comparable<? super C>> implements NodeExpander<ReplayState<C>> {

	/**
	 * The settings to use. Included which actions are allowed.
	 */
	private final ReplaySettings<C> settings;

	/**
	 * The net to replay the traces in.
	 */
	private final Petrinet net;

	/**
	 * The semantics to use while replaying the traces in the net.
	 */
	private final PetrinetSemantics semantics;

	/**
	 * The map which tells which transitions match which objects in the traces.
	 */
	private final Map<Transition, ? extends Object> map;

	/**
	 * The add operator for adding to a cost. This operator ensures that the
	 * result is of identical class as the parameter.
	 */
	private final ReplayCostAddOperator<C> addOperator;

	/**
	 * The best (with the cheapest cost) found so far.
	 */
	ReplayState<C> bestState;

	/**
	 * Create the state expander.
	 * 
	 * @param settings
	 *            The settings to use.
	 * @param net
	 *            The net to replay the traces in.
	 * @param semantics
	 *            The semantics to use while replaying the traces in the net.
	 * @param map
	 *            The map which tells which transitions match which objects in
	 *            the traces.
	 * @param addOperator
	 *            The add operator for adding to a cost.
	 */
	public ReplayStateExpander(ReplaySettings<C> settings, Petrinet net, PetrinetSemantics semantics,
			Map<Transition, ? extends Object> map, ReplayCostAddOperator<C> addOperator) {
		this.settings = settings;
		this.net = net;
		this.semantics = semantics;
		this.map = map;
		this.addOperator = addOperator;

		/*
		 * Initially, the best state is very bad.
		 */
		bestState = new ReplayState<C>(null, null, null, settings.getMaximalCost(), null);
		/*if (bestState.cost instanceof ReplayFitnessCost) {
			ReplayFitnessCost cost1 = (ReplayFitnessCost) bestState.cost;
			cost1.setParent((ReplayState<ReplayFitnessCost>) bestState);
		}*/
	}

	/**
	 * Expands a state. If it detects a final state with cheaper costs than the
	 * best state, then it updates the best state.
	 * 
	 * @param state
	 *            The state to expand.
	 * @param progress
	 *            The progress indicator to pass on to the
	 *            MultiThreadedSearcher.
	 * @param unmodifiableResultCollection
	 *            Required by the interface, but not used here.
	 * @returns All possible expansions of the given state.
	 */
	public Collection<ReplayState<C>> expandNode(ReplayState<C> state, Progress progress,
			Collection<ReplayState<C>> unmodifiableResultCollection) {
		Collection<ReplayState<C>> states = new LinkedList<ReplayState<C>>();
		/*
		 * Check whether the caller considers this state to be a final state.
		 * Note that this may depend on the marking and on the trace.
		 */
		if (settings.isFinal(state.marking, state.trace)) {
			/*
			 * State corresponds to final state, check whether costs are better
			 * than best so far.
			 */
			if (state.cost.compareTo(bestState.cost) < 0) {
				/*
				 * Found a new best trace.
				 */
				synchronized (bestState) {
					bestState = state;
				}
			}
		} else {
			/*
			 * 1. Check the enabled transitions.
			 */
			Collection<Transition> enabledTransitions;
			synchronized (semantics) {
				semantics.setCurrentState(state.marking);
				enabledTransitions = semantics.getExecutableTransitions();
			}

			/*
			 * For every allowed action: expand this state with this action.
			 */
			if (settings.isAllowed(ReplayAction.INSERT_ENABLED_MATCH)) {
				states.addAll(executeEnabledMatches(state, enabledTransitions));
			}
			if (settings.isAllowed(ReplayAction.INSERT_ENABLED_INVISIBLE)) {
				states.addAll(executeInvisible(state, enabledTransitions));
			}
			if (settings.isAllowed(ReplayAction.REMOVE_HEAD)) {
				states.addAll(skipHead(state));
			}
			if (settings.isAllowed(ReplayAction.INSERT_ENABLED_MISMATCH)) {
				states.addAll(executeEnabledMismatches(state, enabledTransitions));
			}

			/*
			 * 2. Check the disabled transitions.
			 */
			Collection<Transition> disabledTransitions = new HashSet<Transition>(net.getTransitions());
			disabledTransitions.removeAll(enabledTransitions);

			/*
			 * For every allowed action: expand this state with this action.
			 */
			if (settings.isAllowed(ReplayAction.INSERT_DISABLED_MATCH)) {
				states.addAll(executeDisabledMatches(state, disabledTransitions));
			}
			if (settings.isAllowed(ReplayAction.INSERT_DISABLED_MISMATCH)) {
				states.addAll(executeDisabledMismatches(state, disabledTransitions));
			}
		}
		return states;
	}

	/**
	 * Required by the interface, but not used here.
	 */
	public void processLeaf(ReplayState<C> leafState, Progress progress, Collection<ReplayState<C>> resultCollection) {
		/*
		 * Nothing to do.
		 */
	}

	/**
	 * From the given state, executes any enabled transition that matches the
	 * head of the trace, and adds corresponding states to the search space.
	 * 
	 * @param state
	 *            The current state.
	 * @param transitions
	 *            The enabled transitions.
	 */
	private Collection<ReplayState<C>> executeEnabledMatches(ReplayState<C> state, Collection<Transition> transitions) {
		Collection<ReplayState<C>> states = new ArrayList<ReplayState<C>>();
		if (!state.trace.isEmpty()) {
			for (Transition transition : transitions) {
				if (transition.isInvisible() || !map.containsKey(transition)) {
				} else if (map.get(transition).equals(state.trace.get(0))) {
					try {
						/*
						 * Determine new marking.
						 */
						Marking newMarking;
						synchronized (semantics) {
							semantics.setCurrentState(state.marking);
							semantics.executeExecutableTransition(transition);
							newMarking = semantics.getCurrentState();
						}
						/*
						 * Determine new cost.
						 */
						C newCost = addOperator.add(state.cost, ReplayAction.INSERT_ENABLED_MATCH, transition,
								state.trace.get(0));
						/*
						 * Determine new trace.
						 */
						List<? extends Object> newTrace = getTail(state.trace);
						/*
						 * Add new state.
						 */
						addState(states, state, newMarking, transition, newCost, newTrace);
					} catch (IllegalTransitionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return states;
	}

	/**
	 * From the given state, executes any enabled invisible transitions, and
	 * adds corresponding states to the search space.
	 * 
	 * @param state
	 *            The current state.
	 * @param transitions
	 *            The enabled transitions.
	 */
	private Collection<ReplayState<C>> executeInvisible(ReplayState<C> state, Collection<Transition> transitions) {
		Collection<ReplayState<C>> states = new ArrayList<ReplayState<C>>();
		for (Transition transition : transitions) {
			if (transition.isInvisible() || !map.containsKey(transition)) {
				try {
					/*
					 * Determine new marking.
					 */
					Marking newMarking;
					synchronized (semantics) {
						semantics.setCurrentState(state.marking);
						semantics.executeExecutableTransition(transition);
						newMarking = semantics.getCurrentState();
					}
					/*
					 * Determine new cost.
					 */
					C newCost = addOperator.add(state.cost, ReplayAction.INSERT_ENABLED_INVISIBLE, transition, null);
					/*
					 * Add new state.
					 */
					addState(states, state, newMarking, transition, newCost, state.trace);
				} catch (IllegalTransitionException e) {
					e.printStackTrace();
				}
			}
		}
		return states;
	}

	/**
	 * From the given state, skips the head of the trace, and adds a
	 * corresponding state to the search space.
	 * 
	 * @param state
	 *            The current state.
	 */
	private Collection<ReplayState<C>> skipHead(ReplayState<C> state) {
		Collection<ReplayState<C>> states = new ArrayList<ReplayState<C>>();
		if (!state.trace.isEmpty()) {
			/*
			 * Determine new cost.
			 */
			C newCost = addOperator.add(state.cost, ReplayAction.REMOVE_HEAD, null, state.trace.get(0));
			/*
			 * Determine new trace.
			 */
			List<? extends Object> newTrace = getTail(state.trace);
			/*
			 * Add new state.
			 */
			addState(states, state, state.marking, null, newCost, newTrace);
		}
		return states;
	}

	/**
	 * From the given state, executes any enabled transition that does not match
	 * the head of the trace, and adds corresponding states to the search space.
	 * 
	 * @param state
	 *            The current state.
	 * @param transitions
	 *            The enabled transitions.
	 */
	private Collection<ReplayState<C>> executeEnabledMismatches(ReplayState<C> state, Collection<Transition> transitions) {
		Collection<ReplayState<C>> states = new ArrayList<ReplayState<C>>();
		if (!state.trace.isEmpty()) {
			for (Transition transition : transitions) {
				if (transition.isInvisible() || !map.containsKey(transition)) {
				} else if (!map.get(transition).equals(state.trace.get(0))) {
					try {
						/*
						 * Determine new marking.
						 */
						Marking newMarking;
						synchronized (semantics) {
							semantics.setCurrentState(state.marking);
							semantics.executeExecutableTransition(transition);
							newMarking = semantics.getCurrentState();
						}
						/*
						 * Determine new cost.
						 */
						C newCost = addOperator.add(state.cost, ReplayAction.INSERT_ENABLED_MISMATCH, transition, null);
						/*
						 * Add new state.
						 */
						addState(states, state, newMarking, transition, newCost, state.trace);
					} catch (IllegalTransitionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return states;
	}

	/**
	 * From the given state, executes any disabled transition that matches the
	 * head of the trace, and adds corresponding states to the search space.
	 * 
	 * @param state
	 *            The current state.
	 * @param transitions
	 *            The disabled transitions.
	 */
	private Collection<ReplayState<C>> executeDisabledMatches(ReplayState<C> state, Collection<Transition> transitions) {
		Collection<ReplayState<C>> states = new ArrayList<ReplayState<C>>();
		if (!state.trace.isEmpty()) {
			for (Transition transition : transitions) {
				if (transition.isInvisible() || !map.containsKey(transition)) {
				} else if (map.get(transition).equals(state.trace.get(0))) {
					try {
						/*
						 * Enable the disabled transition by adding sufficient
						 * tokens in its preset places.
						 */
						Marking preMarking = new Marking(state.marking);
						Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = transition
								.getGraph().getInEdges(transition);
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : edges) {
							if (e instanceof Arc) {
								Arc arc = (Arc) e;
								if (arc.getWeight() > state.marking.occurrences(arc.getSource())) {
									preMarking.add((Place) arc.getSource(), arc.getWeight()
											- state.marking.occurrences(arc.getSource()));
								}
							}
						}
						/*
						 * Determine the new marking.
						 */
						Marking newMarking;
						synchronized (semantics) {
							semantics.setCurrentState(preMarking);
							semantics.executeExecutableTransition(transition);
							newMarking = semantics.getCurrentState();
						}
						/*
						 * Determine the new cost.
						 */
						C newCost = addOperator.add(state.cost, ReplayAction.INSERT_DISABLED_MATCH, transition,
								state.trace.get(0));
						/*
						 * Determine the new trace.
						 */
						List<? extends Object> newTrace = getTail(state.trace);
						/*
						 * Add new state.
						 */
						addState(states, state, newMarking, transition, newCost, newTrace);
					} catch (IllegalTransitionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return states;
	}

	/**
	 * From the given state, executes any disabled transition that does not
	 * match the head of the trace, and adds corresponding states to the search
	 * space.
	 * 
	 * @param state
	 *            The current state.
	 * @param transitions
	 *            The disabled transitions.
	 */
	private Collection<ReplayState<C>> executeDisabledMismatches(ReplayState<C> state,
			Collection<Transition> transitions) {
		Collection<ReplayState<C>> states = new ArrayList<ReplayState<C>>();
		if (!state.trace.isEmpty()) {
			for (Transition transition : transitions) {
				if (transition.isInvisible() || !map.containsKey(transition)) {
				} else if (!map.get(transition).equals(state.trace.get(0))) {
					try {
						/*
						 * Enable the disabled transition by adding sufficient
						 * tokens in its preset places.
						 */
						Marking preMarking = new Marking(state.marking);
						Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = transition
								.getGraph().getInEdges(transition);
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : edges) {
							if (e instanceof Arc) {
								Arc arc = (Arc) e;
								if (arc.getWeight() > state.marking.occurrences(arc.getSource())) {
									preMarking.add((Place) arc.getSource(), arc.getWeight()
											- state.marking.occurrences(arc.getSource()));
								}
							}
						}
						/*
						 * Determine the new marking.
						 */
						Marking newMarking;
						synchronized (semantics) {
							semantics.setCurrentState(preMarking);
							semantics.executeExecutableTransition(transition);
							newMarking = semantics.getCurrentState();
						}
						/*
						 * Determine the new cost.
						 */
						C newCost = addOperator
								.add(state.cost, ReplayAction.INSERT_DISABLED_MISMATCH, transition, null);
						/*
						 * Add new state.
						 */
						addState(states, state, newMarking, transition, newCost, state.trace);
					} catch (IllegalTransitionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return states;
	}

	/**
	 * Adds a state to the search space, provided that its cost is acceptable.
	 * 
	 * @param states
	 *            The states to add to the search space.
	 * @param state
	 *            The previous state, that is, the state from which the new
	 *            state was reached.
	 * @param marking
	 *            The marking of the new state.
	 * @param transition
	 *            The transition that lead from the previous state to the new
	 *            state. May be null if the head of the trace got skipped.
	 * @param cost
	 *            The cost of the new state.
	 * @param trace
	 *            The trace still to replay from the new state.
	 */
	private void addState(Collection<ReplayState<C>> states, ReplayState<C> state, Marking marking,
			Transition transition, C cost, List<? extends Object> trace) {
		ReplayState<C> tmp = new ReplayState<C>(state, marking, transition, cost, trace);
		Object cObject = cost;
		Object tmpObject = tmp;
		if ( cObject instanceof ReplayFitnessCost){
			ReplayFitnessCost cost1 = (ReplayFitnessCost) cObject;
			cost1.setParent((ReplayState<ReplayFitnessCost>) tmpObject);
		}
		if ((cost.compareTo(bestState.cost) < 0) && cost.isAcceptable()) {
			states.add(tmp);
		}
	}

	/**
	 * Returns a copy of the trace with the head removed.
	 * 
	 * @param trace
	 *            The trace.
	 * @return A copy of the trace with the head removed.
	 */
	private List<? extends Object> getTail(List<? extends Object> trace) {
		List<Object> tail = new ArrayList<Object>(trace.size() - 1);
		for (int i = 1; i < trace.size(); i++) {
			tail.add(trace.get(i));
		}
		return tail;
	}
}
