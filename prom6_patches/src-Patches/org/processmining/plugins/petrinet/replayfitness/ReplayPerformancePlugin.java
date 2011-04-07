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

public class ReplayPerformancePlugin {
	private XEventClasses eventClasses = null;


	public TotalFitnessResult getPerformanceDetails(PluginContext context, XLog log, Petrinet net, ReplayFitnessSetting setting) {

		Marking marking;

		try {
			InitialMarkingConnection connection = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			marking = connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained ex) {
			context.log("Petri net lacks initial marking");
			return null;
		}


		TotalFitnessResult fitness = new TotalFitnessResult();
		XEventClasses classes = getEventClasses(log);
		Map<Transition, XEventClass> map = getMapping(classes, net);
		context.getConnectionManager().addConnection(new LogPetrinetConnectionImpl(log, classes, net, map));

		PetrinetSemantics semantics = PetrinetSemanticsFactory.regularPetrinetSemantics(Petrinet.class);

		Replayer<ReplayFitnessCost> replayer = new Replayer<ReplayFitnessCost>(context, net, semantics, map,
				ReplayFitnessCost.addOperator);

		int replayedTraces = 0;
		for (XTrace trace : log) {
			List<XEventClass> list = getList(trace, classes);
			try {
				List<Transition> sequence = replayer.replayTrace(marking, list, setting);
				updatePerformance(net, marking, sequence, semantics);
				replayedTraces++;
			} catch (Exception ex) {
				context.log("Replay of trace " + trace + " failed: " + ex.getMessage());
			}
		}

		String text = "(based on a successful replay of " + replayedTraces + " out of " + log.size() + " traces)";

		return fitness;
	}


    private void addArcUsage(Arc arc, FitnessResult fitnessResult) {
	Integer numUsage = totalResult.getMapArc().get(arc);
	totalResult.getMapArc().put(arc, numUsage == null ? 1 : numUsage+1);
	numUsage = fitnessResult.getMapArc().get(arc);
	fitnessResult.getMapArc().put(arc, numUsage == null ? 1 : numUsage+1);
    }


	private void updatePerformance(Petrinet net, Marking initMarking, List<Transition> sequence, PetrinetSemantics semantics) {
		Marking marking = new Marking(initMarking);
		
		int producedTrace  = marking.size();
		int consumedTrace = 0;
		
		int missingTrace=0;
		FitnessResult tempFitnessResult = new FitnessResult();
		listResult.add(tempFitnessResult);

		for (Transition transition : sequence) {
		    boolean fittingTransition = true;
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
					.getInEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : preset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					addArcUsage(arc, tempFitnessResult);
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
					    totalResult.getMissingMarking().add(place);
					    tempFitnessResult.getMissingMarking().add(place);
					}
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> postset = net
					.getOutEdges(transition);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : postset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					addArcUsage(arc, tempFitnessResult);
					Place place = (Place) arc.getTarget();
					int produced = arc.getWeight();
					for (int i = 0; i < produced; i++) {
						marking.add(place);
					}
				}
			}
			
		}

		// Rupos patches
		totalResult.getRemainingMarking().addAll(marking);
		tempFitnessResult.getRemainingMarking().addAll(marking);
	    
		//calcola la fitness
		tempFitnessResult.set(producedTrace,consumedTrace,missingTrace,marking.isEmpty() ? 0 : marking.size() - 1);
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
	    setting.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
	    setting.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
	    setting.setAction(ReplayAction.REMOVE_HEAD, true);
	    setting.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, true);
	    setting.setAction(ReplayAction.INSERT_DISABLED_MATCH, true);
	    setting.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, true);
	}

    List<FitnessResult> listResult;
    FitnessResult totalResult;
    

    // Rupos public methos
    @Plugin(name = "PerformanceDetails", returnLabels = { "Performance Total" }, returnTypes = { TotalFitnessResult.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public TotalFitnessResult getPerformanceDetails(UIPluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);
	ReplayFitnessUI ui = new ReplayFitnessUI(setting);
	context.showWizard("Configure Fitness Settings", true, true, ui.initComponents());
	ui.setWeights();

	listResult = new Vector<FitnessResult>();
	totalResult = new FitnessResult();
    
	getPerformanceDetails(context, log, net, setting);
	
	TotalFitnessResult total = new TotalFitnessResult();
	total.setList(listResult);
	total.setTotal(totalResult);
	
	return total;
    }

    @Plugin(name = "PerformanceDetails", returnLabels = { "Performance Total" }, returnTypes = { TotalFitnessResult.class }, parameterLabels = {}, userAccessible = true)
    @UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
    public TotalFitnessResult getPerformanceDetails(PluginContext context, XLog log, Petrinet net) {
	ReplayFitnessSetting setting = new ReplayFitnessSetting();
	suggestActions(setting, log, net);

	return getPerformanceDetails(context, log, net, setting);
    }



    @Visualizer
	@Plugin(name = "Performance Result Visualizer", parameterLabels = "String", returnLabels = "Label of String", returnTypes = JComponent.class)
	public static JComponent visualize(PluginContext context, TotalFitnessResult tovisualize) {
		return StringVisualizer.visualize(context, tovisualize.toString());
	}
    
    
}
