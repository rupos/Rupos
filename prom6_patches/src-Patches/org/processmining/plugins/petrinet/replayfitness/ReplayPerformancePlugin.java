package org.processmining.plugins.petrinet.replayfitness;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Set;
import java.util.Date;


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
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
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


	public TotalPerformanceResult getPerformanceDetails(PluginContext context, XLog log, Petrinet net, ReplayFitnessSetting setting) {

		Marking marking;

		try {
			InitialMarkingConnection connection = context.getConnectionManager().getFirstConnection(
					InitialMarkingConnection.class, context, net);
			marking = connection.getObjectWithRole(InitialMarkingConnection.MARKING);
		} catch (ConnectionCannotBeObtained ex) {
			context.log("Petri net lacks initial marking");
			return null;
		}


		TotalPerformanceResult performance = new TotalPerformanceResult();
		
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
				updatePerformance(net, marking, sequence, semantics, trace);
				replayedTraces++;
			} catch (Exception ex) {
				context.log("Replay of trace " + trace + " failed: " + ex.getMessage());
			}
		}

		String text = "(based on a successful replay of " + replayedTraces + " out of " + log.size() + " traces)";

		return performance;
	}

	
	

	

	private void updatePerformance(Petrinet net, Marking initMarking, List<Transition> sequence, PetrinetSemantics semantics, XTrace trace) {
		// if (trace.size() != sequence.size())
		//     System.exit(1);

		XAttributeTimestampImpl date  = (XAttributeTimestampImpl)(trace.get(0).getAttributes().get("time:timestamp"));
		long d1 = date.getValue().getTime();

		Map<Place, PerformanceResult> performance = new HashMap<Place, PerformanceResult>();

		Marking marking = new Marking(initMarking);

		for (Place place : marking) {
			PerformanceResult result = null;
			if (performance.containsKey(place))
				result = performance.get(place);
			else
				result = new PerformanceResult();

			result.addToken();

			performance.put(place, result);
		}


		for (int iTrans=0; iTrans<sequence.size(); iTrans++) {
			Transition transition = sequence.get(iTrans);
			XAttributeTimestampImpl date1  = (XAttributeTimestampImpl)(trace.get(iTrans).getAttributes().get("time:timestamp"));
			long d2 = date1.getValue().getTime();
			float deltaTime = d2-d1;
			d1 = d2;

			boolean fittingTransition = true;
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> preset = net
			.getInEdges(transition);

			Set<Place> places = new HashSet<Place>();
			places.addAll(marking);
			for (Place place : places) {
				PerformanceResult result = null;
				if (performance.containsKey(place))
					result = performance.get(place);
				else
					result = new PerformanceResult();

				int placeMarking = marking.occurrences(place);
				if (placeMarking == 0)
					continue;

				// Transitions denending on the current place
				int maxMarking = 0;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : net.getOutEdges(place)) {
					if (! (edge instanceof Arc))
						continue;
					Arc arc = (Arc) edge;
					Transition trs = (Transition)arc.getTarget();
					// Transition preset
					int minMarking = placeMarking;
					for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge1 : net.getInEdges(trs)) {
						if (! (edge1 instanceof Arc))
							continue;
						Arc arc1 = (Arc) edge1;
						Place p1 = (Place)arc1.getSource();
						int tokens = marking.occurrences(p1);
						minMarking = Math.min(minMarking, tokens);
					}
					maxMarking = Math.max(maxMarking, minMarking);
				}
				// maxMarking < placeMarking
				// maxMarking is the consumable tokens
				// synchTime = (placeMarking - maxMarking) *  deltaTime;
				result.addTime(placeMarking * deltaTime, maxMarking * deltaTime);
				performance.put(place, result);
			}

			// Updates marking according with enabled transition
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

						PerformanceResult result = null;
						if (performance.containsKey(place))
							result = performance.get(place);
						else
							result = new PerformanceResult();
						result.addToken();
						performance.put(place, result);
					}
				}
			}
		}
	
		this.listResult.add(performance);
	//	System.out.println("*****************************************");
	//	System.out.println(i++);
	//	System.out.println("*****************************************");
	//	System.out.println("");
	//	System.out.println(performance);
		
	}

	int i=1;

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

	List<Map<Place,PerformanceResult>> listResult;
	PerformanceResult totalResult;


	// Rupos public methos
	@Plugin(name = "PerformanceDetails", returnLabels = { "Performance Total" }, returnTypes = { TotalPerformanceResult.class }, parameterLabels = {}, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	public TotalPerformanceResult getPerformanceDetails(UIPluginContext context, XLog log, Petrinet net) {
		ReplayFitnessSetting setting = new ReplayFitnessSetting();
		suggestActions(setting, log, net);
		ReplayFitnessUI ui = new ReplayFitnessUI(setting);
		context.showWizard("Configure Fitness Settings", true, true, ui.initComponents());
		ui.setWeights();

		listResult = new Vector<Map<Place,PerformanceResult>>();
		totalResult = new PerformanceResult();

		getPerformanceDetails(context, log, net, setting);

		TotalPerformanceResult total = new TotalPerformanceResult();
		total.setList(listResult);
		total.setTotal(totalResult);

		return total;
	}

	@Plugin(name = "PerformanceDetails", returnLabels = { "Performance Total" }, returnTypes = { TotalPerformanceResult.class }, parameterLabels = {}, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "T. Yuliani and H.M.W. Verbeek", email = "h.m.w.verbeek@tue.nl")
	public TotalPerformanceResult getPerformanceDetails(PluginContext context, XLog log, Petrinet net) {
		ReplayFitnessSetting setting = new ReplayFitnessSetting();
		suggestActions(setting, log, net);
		listResult = new Vector<Map<Place,PerformanceResult>>();
		totalResult = new PerformanceResult();

		getPerformanceDetails(context, log, net, setting);

		TotalPerformanceResult total = new TotalPerformanceResult();
		total.setList(listResult);
		total.setTotal(totalResult);

		return total;

		//return getPerformanceDetails(context, log, net, setting);
	}



	@Visualizer
	@Plugin(name = "Performance Result Visualizer", parameterLabels = "String", returnLabels = "Label of String", returnTypes = JComponent.class)
	public static JComponent visualize(PluginContext context, TotalPerformanceResult tovisualize) {
		return StringVisualizer.visualize(context, tovisualize.toString());
	}


}
