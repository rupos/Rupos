package org.processmining.plugins.petrinet.replayfitness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.connections.logmodel.LogPetrinetConnectionImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.semantics.petrinet.PetrinetSemantics;
import org.processmining.models.semantics.petrinet.impl.PetrinetSemanticsFactory;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replay.Replayer;

public class ReplayFitnessPlugin {

	private int producedTokens;
	private int missingTokens;
	private int consumedTokens;
	private int remainingTokens;

	private XEventClasses eventClasses = null;

	@Plugin(name = "Fitness", returnLabels = { "Fitness" }, returnTypes = { ReplayFitness.class }, parameterLabels = {}, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	public ReplayFitness getFitness(UIPluginContext context, XLog log, Petrinet net) {
		ReplayFitnessSetting setting = new ReplayFitnessSetting();
		suggestActions(setting, log, net);
		ReplayFitnessUI ui = new ReplayFitnessUI(setting);
		context.showWizard("Configure Fitness Settings", true, true, ui.initComponents());
		ui.setWeights();

		return getFitness(context, log, net, setting);
	}

	@Plugin(name = "Fitness", returnLabels = { "Fitness" }, returnTypes = { ReplayFitness.class }, parameterLabels = {}, userAccessible = true)
	public ReplayFitness getFitness(PluginContext context, XLog log, Petrinet net) {
		ReplayFitnessSetting setting = new ReplayFitnessSetting();
		suggestActions(setting, log, net);
		return getFitness(context, log, net, setting);
	}

	@Plugin(name = "Fitness", returnLabels = { "Fitness" }, returnTypes = { ReplayFitness.class }, parameterLabels = {}, userAccessible = true)
	public ReplayFitness getFitness(PluginContext context, XLog log, Petrinet net, ReplayFitnessSetting setting) {
		/*
		 * try {
		 * context.getConnectionManager().getFirstConnection(LogPetrinetConnection
		 * .class, context, log, net); } catch (ConnectionCannotBeObtained ex) {
		 * context.log("Log and Petri net are not connected"); return null; }
		 */
		Marking marking;

		try {
			InitialMarkingConnection connection = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			marking = connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained ex) {
			context.log("Petri net lacks initial marking");
			return null;
		}

		return getFitness(context, log, net, marking, setting);
	}

	public ReplayFitness getFitness(PluginContext context, XLog log, Petrinet net, Marking marking,
			ReplayFitnessSetting setting) {
		ReplayFitness fitness = new ReplayFitness(0.0, "");
		XEventClasses classes = getEventClasses(log);
		Map<Transition, XEventClass> map = getMapping(classes, net);
		context.getConnectionManager().addConnection(new LogPetrinetConnectionImpl(log, classes, net, map));

		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);

		Replayer<ReplayFitnessCost> replayer = new Replayer<ReplayFitnessCost>(context, net, semantics, map,
				ReplayFitnessCost.addOperator);

		producedTokens = 0;
		consumedTokens = 0;
		missingTokens = 0;
		remainingTokens = 0;

		int replayedTraces = 0;
		for (XTrace trace : log) {
			List<XEventClass> list = getList(trace, classes);
			try {
				List<Transition> sequence = replayer.replayTrace(marking, list, setting);
				replayedTraces++;
				updateFitness(net, marking, sequence, semantics);
			} catch (Exception ex) {
				context.log("Replay of trace " + trace + " failed: " + ex.getMessage());
			}
		}

		String text = "(based on a successful replay of " + replayedTraces + " out of " + log.size() + " traces)";
		fitness.set(producedTokens, consumedTokens, missingTokens, remainingTokens, text);
		ReplayFitnessConnection connection = new ReplayFitnessConnection(fitness, log, net);
		context.getConnectionManager().addConnection(connection);

		return fitness;
	}

	private void updateFitness(Petrinet net, Marking initMarking, List<Transition> sequence, PetrinetSemantics semantics) {
		Marking marking = new Marking(initMarking);
		producedTokens += marking.size();

		for (Transition transition : sequence) {
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : preset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getSource();
					int consumed = arc.getWeight();
					int missing = 0;
					if (arc.getWeight() > marking.occurrences(place)) {
						missing = arc.getWeight() - marking.occurrences(place);
					}
					for (int i = missing; i < consumed; i++) {
						marking.remove(place);
					}
					// Rupos patches
					for (int i = 0; i < missing; i++) {
					    if (this.missingMarking == null)
						this.missingMarking = new Marking();
					    this.missingMarking.add(place);
					}
					consumedTokens += consumed;
					missingTokens += missing;
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : postset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					Place place = (Place) arc.getTarget();
					int produced = arc.getWeight();
					for (int i = 0; i < produced; i++) {
						marking.add(place);
					}
					producedTokens += produced;
				}
			}
		}
		consumedTokens += marking.size();
		remainingTokens += marking.isEmpty() ? 0 : marking.size() - 1;

		// Rupos patches
		if (this.remainingMarking == null)
		    this.remainingMarking = new Marking();
		this.remainingMarking.addAll(marking);
	}

	private List<XEventClass> getList(XTrace trace, XEventClasses classes) {
		List<XEventClass> list = new ArrayList<XEventClass>();
		for (XEvent event : trace) {
			list.add(classes.getClassOf(event));
		}
		return list;
	}

	private XEventClasses getEventClasses(XLog log) {
		if (eventClasses == null) {
			XEventClassifier classifier = XLogInfoImpl.STANDARD_CLASSIFIER;
			XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
			eventClasses = summary.getEventClasses(classifier);
		}
		return eventClasses;
	}

	private Map<Transition, XEventClass> getMapping(XEventClasses classes, Petrinet net) {
		Map<Transition, XEventClass> map = new HashMap<Transition, XEventClass>();

		for (Transition transition : net.getTransitions()) {
			for (XEventClass eventClass : classes.getClasses()) {
				if (eventClass.getId().equals(transition.getAttributeMap().get(AttributeMap.LABEL))) {
					map.put(transition, eventClass);
				}
			}
		}
		return map;
	}

	private void suggestActions(ReplayFitnessSetting setting, XLog log, Petrinet net) {
		boolean hasInvisibleTransitions = false;
		Collection<String> transitionLabels = new HashSet<String>();
		for (Transition transition : net.getTransitions()) {
			transitionLabels.add((String) transition.getAttributeMap().get(AttributeMap.LABEL));
			if (transition.isInvisible()) {
				hasInvisibleTransitions = true;
			}
		}
		Collection<String> eventClassLabels = new HashSet<String>();
		for (XEventClass eventClass : getEventClasses(log).getClasses()) {
			eventClassLabels.add(eventClass.getId());
		}
		setting.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
		setting.setAction(ReplayAction.INSERT_DISABLED_MATCH, true);
		if (transitionLabels.containsAll(eventClassLabels)) {
			/*
			 * For every event class there is at least one transition. Thus,
			 * there is always a matching transition.
			 */
			setting.setAction(ReplayAction.REMOVE_HEAD, false);
			setting.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, false);
			setting.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, false);
		} else {
			setting.setAction(ReplayAction.REMOVE_HEAD, true);
			setting.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, true);
			setting.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, true);
		}
		if (hasInvisibleTransitions || !eventClassLabels.containsAll(transitionLabels)) {
			setting.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
		} else {
			/*
			 * There are no explicit invisible transitions and all transitions
			 * correspond to event classes.
			 */
			setting.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, false);
		}
	}

    // Rupos properties
    Marking remainingMarking = null;
    Marking missingMarking = null;

    // Rupos public methos
    @Plugin(name = "FitnessDetails", returnLabels = { "Fitness", "Remaining","Missing" }, returnTypes = { ReplayFitness.class, Marking.class, Marking.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public Object[] getFitnessDetails(UIPluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);
	ReplayFitnessUI ui = new ReplayFitnessUI(setting);
	context.showWizard("Configure Fitness Settings", true, true, ui.initComponents());
	ui.setWeights();

	this.remainingMarking = new Marking();
	this.missingMarking = new Marking();

	return new Object[]{getFitness(context, log, net, setting),
			    this.remainingMarking,
			    this.missingMarking
	};
    }

    @Plugin(name = "FitnessDetails", returnLabels = { "Fitness", "Remaining","Missing" }, returnTypes = { ReplayFitness.class, Marking.class, Marking.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public Object[] getFitnessDetails(PluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);

	this.remainingMarking = new Marking();
	this.missingMarking = new Marking();

	return new Object[]{getFitness(context, log, net, setting),
			    this.remainingMarking,
			    this.missingMarking
	};
    }
}
