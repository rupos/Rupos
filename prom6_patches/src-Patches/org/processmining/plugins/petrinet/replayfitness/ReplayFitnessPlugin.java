package org.processmining.plugins.petrinet.replayfitness;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;



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
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.StringVisualizer;
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

import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class ReplayFitnessPlugin {
    ReplayFitness convertDetails(TotalFitnessResult result) {
	FitnessResult summary = result.getTotal();
	ReplayFitness res = new ReplayFitness();
	res.set(summary.getProducedTokens(),
		summary.getConsumedTokens(),
		summary.getMissingMarking().size(),
		summary.getRemainingMarking().size(),
		"");
	return res;
    }

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
		TotalFitnessResult res = getFitnessDetails(context, log, net, setting);
		return this.convertDetails(res);
	}

	
	
	public TotalFitnessResult getFitnessDetails(PluginContext context, XLog log, Petrinet net, Marking marking, ReplayFitnessSetting setting) {
		TotalFitnessResult totalResult = new TotalFitnessResult();
		totalResult.setTotal(new FitnessResult());
		totalResult.setList(new Vector<FitnessResult>());

		XEventClasses classes = getEventClasses(log);
		Map<Transition, XEventClass> map = getMapping(classes, net);
		context.getConnectionManager().addConnection(new LogPetrinetConnectionImpl(log, classes, net, map));

		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);

		Replayer<ReplayFitnessCost> replayer = new Replayer<ReplayFitnessCost>(context, net, semantics, map,
				ReplayFitnessCost.addOperator);

		int replayedTraces = 0;
		int i =0;
		for (XTrace trace : log) {
			List<XEventClass> list = getList(trace, classes);
			try {
			    System.out.println("Replay :" + ++i);
				List<Transition> sequence = replayer.replayTrace(marking, list, setting);
				updateFitness(net, marking, sequence, semantics, totalResult);
				replayedTraces++;
			    System.out.println("Replayed");

			} catch (Exception ex) {
			    System.out.println("Failed");
				context.log("Replay of trace " + trace + " failed: " + ex.getMessage());
			}
		}

		String text = "(based on a successful replay of " + replayedTraces + " out of " + log.size() + " traces)";
		// ReplayFitnessConnection connection = new ReplayFitnessConnection(fitness, log, net);
		// context.getConnectionManager().addConnection(connection);
		totalResult.getTotal().updateFitness();

		return totalResult;
	}


    private void addArcUsage(Arc arc, FitnessResult fitnessResult, FitnessResult totalResult) {
	Integer numUsage = totalResult.getMapArc().get(arc);
	totalResult.getMapArc().put(arc, numUsage == null ? 1 : numUsage+1);
	numUsage = fitnessResult.getMapArc().get(arc);
	fitnessResult.getMapArc().put(arc, numUsage == null ? 1 : numUsage+1);
    }


    private void updateFitness(Petrinet net, Marking initMarking, List<Transition> sequence, PetrinetSemantics semantics, TotalFitnessResult totalResult) {
		Marking marking = new Marking(initMarking);
		int producedTokens = marking.size();
		int consumedTokens = 0;
		int producedTrace  = marking.size();
		int consumedTrace = 0;
		
		int missingTrace=0;
		FitnessResult tempFitnessResult = new FitnessResult();
		totalResult.getList().add(tempFitnessResult);

		for (Transition transition : sequence) {
		    boolean fittingTransition = true;
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : preset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					addArcUsage(arc, tempFitnessResult, totalResult.getTotal());
					Place place = (Place) arc.getSource();
					int consumed = arc.getWeight();
					int missing = 0;
					//
					if (arc.getWeight() > marking.occurrences(place)) {
						missing = arc.getWeight() - marking.occurrences(place);
					}
					for (int i = missing; i < consumed; i++) {
						marking.remove(place);
					}
					// Rupos patches
					for (int i = 0; i < missing; i++) {
					    totalResult.getTotal().getMissingMarking().add(place);
					    tempFitnessResult.getMissingMarking().add(place);
					}
					consumedTokens += consumed;
					consumedTrace += consumed;
					missingTrace +=missing;
					//se sono mancati token, questa transizione non ha fittato per questa traccia
					if (missing>0) fittingTransition = false;
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : postset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					addArcUsage(arc, tempFitnessResult, totalResult.getTotal());
					Place place = (Place) arc.getTarget();
					int produced = arc.getWeight();
					for (int i = 0; i < produced; i++) {
						marking.add(place);
					}
					producedTokens += produced;
					producedTrace +=produced;
				}
			}
			if (!fittingTransition) {
			    Integer numTraceNotFittingForTransaction = totalResult.getTotal().getMapTransition().get(transition);
			    totalResult.getTotal().getMapTransition().put(transition, numTraceNotFittingForTransaction == null ? 1 : numTraceNotFittingForTransaction+1);
			    //sulla singola traccia, il numero di tracce che non fittano la transizione è 0 o 1
			    tempFitnessResult.getMapTransition().put(transition, 1);
			}
			
		}
		consumedTokens += marking.size();
		consumedTrace += marking.size();
		int remainingTokens = marking.isEmpty() ? 0 : marking.size() - 1;

		// Rupos patches
		totalResult.getTotal().getRemainingMarking().addAll(marking);
		tempFitnessResult.getRemainingMarking().addAll(marking);
	    
		//calcola la fitness del singolo trace
		tempFitnessResult.setProducedTokens(producedTokens);
		tempFitnessResult.setConsumedTokens(consumedTokens);

		// Per il totale
		totalResult.getTotal().setProducedTokens(totalResult.getTotal().getProducedTokens() + producedTokens);
		totalResult.getTotal().setConsumedTokens(totalResult.getTotal().getConsumedTokens() + consumedTokens);
		
		// Calcola la fitness per il singolo trace
		tempFitnessResult.updateFitness();
	}

	private List<XEventClass> getList(XTrace trace, XEventClasses classes) {
		List<XEventClass> list = new ArrayList<XEventClass>();
		for (XEvent event : trace) {
			list.add(classes.getClassOf(event));
		}
		return list;
	}

	private XEventClasses getEventClasses(XLog log) {
	    XEventClassifier classifier = XLogInfoImpl.STANDARD_CLASSIFIER;
	    XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
	    XEventClasses eventClasses = summary.getEventClasses(classifier);
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

    // Rupos public methos
    @Plugin(name = "FitnessDetails", returnLabels = { "Fitness Total" }, returnTypes = { TotalFitnessResult.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public TotalFitnessResult getFitnessDetails(UIPluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);
	ReplayFitnessUI ui = new ReplayFitnessUI(setting);
	context.showWizard("Configure Fitness Settings", true, true, ui.initComponents());
	ui.setWeights();

	TotalFitnessResult totalResult = getFitnessDetails(context, log, net, setting);
	
	
	return totalResult;
    }

    @Plugin(name = "FitnessDetails", returnLabels = { "Fitness Total" }, returnTypes = { TotalFitnessResult.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public TotalFitnessResult getFitnessDetails(PluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);

	TotalFitnessResult total = getFitnessDetails(context, log, net, setting);
	

	return total;
    }


    @Plugin(name = "FitnessDetailsSettings", returnLabels = { "Fitness Total" }, returnTypes = { TotalFitnessResult.class }, parameterLabels = {}, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	public TotalFitnessResult getFitnessDetails(PluginContext context, XLog log, Petrinet net, ReplayFitnessSetting setting) {

		Marking marking;

		try {
			InitialMarkingConnection connection = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			marking = connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained ex) {
			context.log("Petri net lacks initial marking");
			return null;
		}

		TotalFitnessResult total = getFitnessDetails(context, log, net, marking, setting);
	//visualizza i dati 
	PNVisualizzeJS js = new PNVisualizzeJS();
	js.generateJS(net, total);
	
	return total;
    }

    @Plugin(name = "FitnessSuggestSettings", returnLabels = { "Settings" }, returnTypes = { ReplayFitnessSetting.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public ReplayFitnessSetting suggestSettings(PluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting settings = new ReplayFitnessSetting();
	suggestActions(settings, log, net);
	return settings;
    }

    @Visualizer
	@Plugin(name = "Fitness Result Visualizer", parameterLabels = "String", returnLabels = "Label of String", returnTypes = JComponent.class)
	public static JComponent visualize(PluginContext context, TotalFitnessResult tovisualize) {
		return StringVisualizer.visualize(context, tovisualize.toString());
	}
    
    
}
