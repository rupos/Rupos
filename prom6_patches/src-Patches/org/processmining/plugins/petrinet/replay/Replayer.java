package org.processmining.plugins.petrinet.replay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.search.ExpandCollection;
import org.processmining.framework.util.search.MultiThreadedSearcher;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessCost;

/**
 * Cost-based Petri net trace replayer.
 * 
 * This replayer returns the cheapest transition sequence that replays a given
 * trace in a given Petri net with a given initial marking. The caller needs to
 * specify a cost structure by implementing the ReplayCost and the
 * ReaplyCostAddOperator interfaces. This cost structure determines which
 * sequence will be the cheapest.
 * 
 * The replayer assumes that there are six possible actions that can be used to
 * replay the trace (see ReplayAction):
 * 
 * 1. Execute an enabled transition that matches the head of trace. The
 * transition will be inserted in the transition sequence, the head of the trace
 * will be removed.
 * 
 * 2. Execute an invisible transition. The transition will be inserted in the
 * transition sequence.
 * 
 * 3. Skip the head of the trace. The head of the trace will be removed.
 * 
 * 4. Execute an enabled transition that does not match the head of the trace.
 * The transition will be inserted in the transition sequence, but the head of
 * the trace will not be removed.
 * 
 * 5. Execute a disabled transition that matches the head of the trace. The
 * transition will be inserted in the transition sequence and the head of the
 * trace will be removed.
 * 
 * 6. Execute a disabled transition that does not match the head of the trace.
 * The transition will be inserted in the transition sequence, but the head of
 * the trace will not be removed.
 * 
 * In the cost structure, the caller needs to specify what the costs are for
 * every action. To make this more flexible, the cost does not only depend on
 * the action, but may also depend on the transition involved (if any) and the
 * matched head of the trace (if any). Thus, it would be possible for the caller
 * to punish the skipping of a certain head of the trace severely.
 * 
 * Apart from the cost structure, the caller also needs to specify an initial
 * cost, a maximal cost, whether some replay is final, and which of the 6
 * actions are allowed at all. To so so, s/he needs to implement the
 * ReplaySettings interface.
 * 
 * The replayer does a cost-based breadth-first search. At any point in time, it
 * will have a set of states in the search space that it can expand. Of these
 * states, it takes the one with the lowest cost, and expands it. If a state
 * corresponds to a final replay, and if it is cheaper then the cheapest replay
 * found so far, then we have a new cheapest replay. The replayer returns null
 * if no replay was found.
 * 
 * Some observations that need to be taken into account by the caller:
 * 
 * A. To prevent endless cycles, every action should have a positive cost.
 * 
 * B. If skipping the head of the trace is allowed, then the replayer will
 * always find some replay (skipping all objects in the trace is a replay).
 * 
 * @author hverbeek
 * @see ReplayCost
 * @see ReplayCostAddOperator
 * @see ReplaySettings
 * @param <C>
 *            The class implementing the cost structure. To be able to detect
 *            which cost is better, it needs to implement Comparable.
 */
public class Replayer<C extends ReplayCost & Comparable<? super C>> {

	/**
	 * The Petri net to replay the traces in.
	 */
	private final Petrinet net;

	/**
	 * The semantics to use while replaying traces in the net.
	 */
	private final PetrinetSemantics semantics;

	/**
	 * The map which tells which transitions match which objects in the trace.
	 * Note that every transition can match with only one object, but that many
	 * transitions can match with the same object.
	 */
	private final Map<Transition, ? extends Object> map;

	/**
	 * The operator to use to add something to a cost. Note that the cost
	 * structure is generic, but that for every new state we need to determine a
	 * new cost. Unfortunately, this new cost may not be of the generic type
	 * (ReplayCost), but needs to be of the actual implementing class (that is,
	 * C). Also unfortunately, two costs of different implementing classes (say,
	 * C1 and C2) need not be comparable, so we cannot make the ReplayCost
	 * interface extend Comparable, as this assumes that we can compare a C1
	 * object with a C2 object. Therefore, we need this operator.
	 */
	private final ReplayCostAddOperator<C> addOperator;

	/**
	 * The context in which the replayer runs. Needed for the
	 * MultiThreadedSearcher.
	 */
	private final PluginContext context;

	/**
	 * Constructs a replayer.
	 * 
	 * @param context
	 *            The context in which the replayer runs.
	 * @param net
	 *            The net to replay the traces in.
	 * @param semantics
	 *            The semantics to use while replaying traces.
	 * @param map
	 *            The map from transitions to objects in the trace.
	 * @param addOperator
	 *            The operator to use for adding to a cost.
	 */
	public Replayer(PluginContext context, Petrinet net, PetrinetSemantics semantics,
			Map<Transition, ? extends Object> map, ReplayCostAddOperator<C> addOperator) {
		this.context = context;
		this.net = net;
		this.semantics = semantics;
		this.map = map;
		this.addOperator = addOperator;
	}

	/**
	 * Replays a given trace with the given settings, from the given initial
	 * marking. Uses the MultiThreadedSearcher.
	 * 
	 * @param initialMarking
	 *            The initial marking of the Petri net for this trace.
	 * @param trace
	 *            The trace to replay.
	 * @param settings
	 *            The settings to use.
	 * @return The cheapest transition sequence that replays the given trace,
	 *         null if no such transition sequence exists.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public List<Transition> replayTrace(Marking initialMarking, List<? extends Object> trace, ReplaySettings<C> settings)
			throws InterruptedException, ExecutionException {
		/*
		 * Initialize the semantics.
		 */
		semantics.initialize(net.getTransitions(), initialMarking);

		/*
		 * Add the initial state with initial marking and initial cost.
		 */
		ReplayState<C> initialState = new ReplayState<C>(null, initialMarking, null, settings.getInitialCost(), trace);
		Collection<ReplayState<C>> initialStates = new TreeSet<ReplayState<C>>();
		Collection<ReplayState<C>> finalStates = new TreeSet<ReplayState<C>>();
		initialStates.add(initialState);
		Object oinitial = initialState;
		Object cinitial = initialState.cost;
		if (cinitial instanceof ReplayFitnessCost) {
			ReplayFitnessCost cost1 = (ReplayFitnessCost)cinitial ;
			cost1.setParent((ReplayState<ReplayFitnessCost>) oinitial);
		}
		

		/*
		 * Initialize the MultiThreadedSearcher.
		 * 
		 * The use of the TreeSet in the ExpandCollection guarantees that the
		 * states will be sorted on costs.
		 */
		ReplayStateExpander<C> expander = new ReplayStateExpander<C>(settings, net, semantics, map, addOperator);
		MultiThreadedSearcher<ReplayState<C>> searcher = new MultiThreadedSearcher<ReplayState<C>>(1,expander,
				new ExpandCollection<ReplayState<C>>() {
					private final TreeSet<ReplayState<C>> states = new TreeSet<ReplayState<C>>();

					public void add(Collection<? extends ReplayState<C>> newElements) {
						states.addAll(newElements);
					}

					public boolean isEmpty() {
						return states.isEmpty();
					}

					public ReplayState<C> pop() {
						ReplayState<C> head = states.first();
						states.remove(head);
						return head;
					}
				});
		searcher.addInitialNodes(initialStates);

		/*
		 * Start the search.
		 */
		searcher.startSearch(context.getExecutor(), context.getProgress(), finalStates);

		/*
		 * Search is done. Check the results.
		 */
		ArrayList<Transition> sequence = null;
		if (expander.bestState.cost.compareTo(settings.getMaximalCost()) < 0) {
			/*
			 * Found a solution, construct the transition sequence and return
			 * it.
			 */
			ReplayState<C> state = expander.bestState;
			sequence = new ArrayList<Transition>();
			while (state != null) {
				if (state.transition != null) {
					sequence.add(0, state.transition);
				}
				state = state.parentState;
			}
		} else {
			/*
			 * No solution, return null.
			 */
		}
		return sequence;
	}
}
