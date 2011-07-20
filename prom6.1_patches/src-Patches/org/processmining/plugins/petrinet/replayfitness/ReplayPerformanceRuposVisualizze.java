package org.processmining.plugins.petrinet.replayfitness;


import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
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
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;

@Visualizer
@Plugin(name = "Performance Result Visualizer", parameterLabels = "Performance Rupos Analisys", returnLabels = "Visualized Performance", returnTypes = JComponent.class)
public class ReplayPerformanceRuposVisualizze {

	@PluginVariant(requiredParameterLabels = { 0 })
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "gos", email = "di.unipi.it")
	public JComponent visualize(PluginContext context, TotalPerformanceResult tovisualize) {
		if(context instanceof UIPluginContext){
			try {
				ReplayPerformanceRuposConnection connection = context.getConnectionManager().getFirstConnection(
						ReplayPerformanceRuposConnection.class, context, tovisualize);

				// connection found. Create all necessary component to instantiate inactive visualization panel
				XLog log = connection.getObjectWithRole(ReplayFitnessConnection.XLOG);
				Petrinet netx = connection.getObjectWithRole(ReplayFitnessConnection.PNET);
				Petrinet net = PetrinetFactory.clonePetrinet(netx);

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



	private JComponent getVisualizationPanel(PluginContext context,
			Petrinet net, XLog log, TotalPerformanceResult tovisualize) {
		Progress progress = context.getProgress();
		String  visualize= this.toHTMLfromMPP(tovisualize.getList().get(0));
		drawperformancenet(net, tovisualize.getList().get(0),tovisualize.total);
		ReplayPerformanceRuposPanel panel = new ReplayPerformanceRuposPanel(context, net, log, progress, visualize);
		return panel;


	}
	private void drawperformancenet(Petrinet net, Map<Place, PerformanceResult> Result, PerformanceResult totalResult) {

		Map<String,PerformanceResult> name2performance = new HashMap<String, PerformanceResult>();

		for(Place p : Result.keySet() ){
			PerformanceResult res = Result.get(p);
			String name = p.getLabel();
			name2performance.put(name, res);
		}

		for(Place p : net.getPlaces() ){
			String name = p.getLabel();
			if(name2performance.containsKey(name)){
				PerformanceResult res = name2performance.get(name);
				String r="<html>SyncTime:"+res.synchTime+"<br/>WaitTime:"+res.waitTime+"<br/>SoujourTime:"+res.time+"<br/>CountToken"+res.tokenCount+"</html>";
				if(res.synchTime>0){
					p.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.GREEN);
				}else if(res.waitTime>0){
					p.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.cyan);
				}else p.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.WHITE);
				p.getAttributeMap().remove(AttributeMap.TOOLTIP);
				p.getAttributeMap().put(AttributeMap.TOOLTIP, r);
				p.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				/*for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> a : p.getGraph().getOutEdges(p)){
					a.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				}*/
				
			}else{
				p.getAttributeMap().remove(AttributeMap.TOOLTIP);
			}
			
		}

	}

	private String toHTMLfromMPP(Map<Place, PerformanceResult> Result ){
		String start = "<table border=\"2px\" style=\"width: 100%\">";
		String end ="</table>";
		String placen ="<td>Name Place</td>\n";
		String waitt="<td>WaitTime</td>\n";
		String synct="<td>SyncTime</td>\n";
		String sogtime="<td>SoggTime</td>\n";

		String out = "";

		Iterator it = Result.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			Place place =(Place) pairs.getKey();
			PerformanceResult perRes = (PerformanceResult) pairs.getValue();
			placen+="<td>"+place.getLabel()+"</td>";
			waitt += "<td>"+perRes.waitTime+"</td>";
			synct += "<td>"+perRes.synchTime+"</td>";
			sogtime += "<td>"+perRes.time+"</td>";

		}

		out=start+"<tr>"+placen+"</tr>"+"<tr>"+waitt+"</tr>"+"<tr>"+synct+"</tr>"+"<tr>"+sogtime+"</tr>"+end;
		return "<html>"+out+"</html>";
	}


}
