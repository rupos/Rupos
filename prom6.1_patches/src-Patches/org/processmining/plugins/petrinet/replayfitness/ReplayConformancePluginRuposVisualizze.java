package org.processmining.plugins.petrinet.replayfitness;


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.StringVisualizer;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

@Visualizer
@Plugin(name = "Conformance Result Visualizer", parameterLabels = "Conformance Rupos Analisys", returnLabels = "Visualized Fitness", returnTypes = JComponent.class)
public class ReplayConformancePluginRuposVisualizze {

	@PluginVariant(requiredParameterLabels = { 0 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(PluginContext context, TotalConformanceResult tovisualize) {
		if(context instanceof UIPluginContext){
			try {
				ReplayConformanceRuposConnection connection = context.getConnectionManager().getFirstConnection(
						ReplayConformanceRuposConnection.class, context, tovisualize);

				// connection found. Create all necessary component to instantiate inactive visualization panel
				XLog log = connection.getObjectWithRole(ReplayFitnessConnection.XLOG);
				Petrinet netx = connection.getObjectWithRole(ReplayFitnessConnection.PNET);
				Petrinet net = PetrinetFactory.clonePetrinet(netx);
				drawfitnessnet(net,tovisualize);
				return getVisualizationPanel(context, net, log, tovisualize);

			} catch (ConnectionCannotBeObtained e) {
				// No connections available
				context.log("Connection does not exist", MessageLevel.DEBUG);
				return null;
			}
		}else {
			
			return StringVisualizer.visualize(context, tovisualize.toString());
		}

	}

	private void drawfitnessnet(Petrinet net, TotalConformanceResult tovisualize) {
		Map<String,Integer> missplacename2occ = new HashMap<String, Integer>();
		Map<String,Integer> remplacename2occ = new HashMap<String, Integer>();


		ConformanceResult totalResult = tovisualize.total;


		Map<Arc,Integer> archiattivati =totalResult.getMapArc();

		for(Arc a : archiattivati.keySet()){
			String afrom=a.getSource().getLabel();
			String ato=a.getTarget().getLabel();
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> newa : net.getEdges()){
				String from = newa.getSource().getLabel();
				String to = newa.getTarget().getLabel();
				if((afrom==from) && (ato==to)){
					Integer i = archiattivati.get(a);
					newa.getAttributeMap().put(AttributeMap.LABEL,i.toString() );
					newa.getAttributeMap().put(AttributeMap.TOOLTIP,i.toString() );
					newa.getAttributeMap().put(AttributeMap.SHOWLABEL,true );
				}

			}

		}



		Marking miss = totalResult.getMissingMarking();
		for(Place p : miss.baseSet()){
			int i = miss.occurrences(p);
			missplacename2occ.put(p.getLabel(), i);
		}
		Marking rem = totalResult.getRemainingMarking();
		for(Place p : rem.baseSet()){
			int i = rem.occurrences(p);
			remplacename2occ.put(p.getLabel(), i);
		}


		for (Place pl : net.getPlaces()) {
			int i = 0;
			int ii =0;
			if(missplacename2occ.containsKey(pl.getLabel())){
				i = missplacename2occ.get(pl.getLabel());
			}
			if(remplacename2occ.containsKey(pl.getLabel())){
				ii = remplacename2occ.get(pl.getLabel());
			}
			if(ii>0 && i>0){
				String r=String.valueOf(ii)+"/-"+String.valueOf(i);
				pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				pl.getAttributeMap().remove(AttributeMap.TOOLTIP);
				pl.getAttributeMap().put(AttributeMap.TOOLTIP, r);
				pl.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				//this.inserPlace(pl.getLabel(), x, y, "red", r);
			}else if (ii>0 && i<=0){
				pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				pl.getAttributeMap().remove(AttributeMap.TOOLTIP);
				pl.getAttributeMap().put(AttributeMap.TOOLTIP, String.valueOf(ii));
				pl.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				//this.inserPlace(pl.getLabel(), x, y, "red", String.valueOf(ii));
			}else if (i>0 && ii<=0){
				//this.inserPlace(pl.getLabel(), x, y, "red", "-"+String.valueOf(i));
				pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				pl.getAttributeMap().remove(AttributeMap.TOOLTIP);
				pl.getAttributeMap().put(AttributeMap.TOOLTIP, String.valueOf(-i));
				pl.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			}


		}
		for (Transition ts : net.getTransitions()) {

			for (Transition tsx : totalResult.getMapTransition().keySet()){

				if(tsx.getLabel().equals(ts.getLabel())){
					ts.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.ORANGE);
				}
			}
		}





	}

	private JComponent getVisualizationPanel(PluginContext context,
			Petrinet net, XLog log, TotalConformanceResult tovisualize) {
		Progress progress = context.getProgress();

		ReplayConformanceRuposPanel panel = new ReplayConformanceRuposPanel(context, net, log, progress, tovisualize);
		return panel;


	}


}
