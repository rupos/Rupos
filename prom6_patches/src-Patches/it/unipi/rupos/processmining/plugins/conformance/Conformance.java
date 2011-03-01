package it.unipi.rupos.processmining.plugins;

import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.PluginContext;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;

import org.deckfour.xes.model.XLog;
import org.processmining.connections.logmodel.LogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.connections.logmodel.LogPetrinetConnection;

import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessPlugin;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replay.ReplayAction;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

import org.processmining.plugins.petrinet.replayfitness.ReplayFitness;

@Plugin(name = "PetriNetConformance", 
	parameterLabels = { "Log",
			    "PetriNet",
			    "Initial Marking"
	}, 
	returnLabels = { "Conformance Results One",  "Conformance Results Two"}, 
	returnTypes = { String.class, String.class})
public class Conformance extends ReplayFitnessPlugin {

    @UITopiaVariant(uiLabel = "PetriNet Conformance", 
		    affiliation = "University of Pisa", 
		    author = "Roberto Guanciale", 
		    email ="guancio" + (char) 0x40 + "gmail.com",
		    website = "guancio.wordpress.com"
		    )
    @PluginVariant(variantLabel = "Log, PetriNet and Marking",
		   requiredParameterLabels = {0, 1, 2}
		   )
    public Object[] doConformance(PluginContext context, XLog log, Petrinet net, Marking marking) {

	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);

	ReplayFitness res = this.getFitness(context, log,  net, marking, setting);

	context.getFutureResult(0).setLabel("(Guancio Result)");
	context.getFutureResult(1).setLabel("(Guancio Result 2)");
	return new Object[] {res.getValue(), "Guancio Secondo"};
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

	private XEventClasses eventClasses = null;
	private XEventClasses getEventClasses(XLog log) {
		if (eventClasses == null) {
			XEventClassifier classifier = XLogInfoImpl.STANDARD_CLASSIFIER;
			XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
			eventClasses = summary.getEventClasses(classifier);
		}
		return eventClasses;
	}
}
