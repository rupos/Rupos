package org.processmining.plugins.petrinet.replay.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replay.conformance.ConformanceResult;
import org.processmining.plugins.petrinet.replay.performance.PerformanceData;

public class PetriNetDrawUtil {
	
	
	
	public static  void drawconformance(Petrinet net,ConformanceResult totalResult) {
		Map<String,Integer> missplacename2occ = new HashMap<String, Integer>();
		Map<String,Integer> remplacename2occ = new HashMap<String, Integer>();





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
	
	public static void drawperformancenet(Petrinet net, Map<Place, PerformanceData> Result, Map<Arc, Integer> maparc) {

		Map<String,PerformanceData> name2performance = new HashMap<String, PerformanceData>();

		for(Place p : Result.keySet() ){
			PerformanceData res = Result.get(p);
			String name = p.getLabel();
			name2performance.put(name, res);
		}
		
		

		for(Arc a : maparc.keySet()){
			String afrom=a.getSource().getLabel();
			String ato=a.getTarget().getLabel();
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> newa : net.getEdges()){
				String from = newa.getSource().getLabel();
				String to = newa.getTarget().getLabel();
				if((afrom==from) && (ato==to)){
					Integer i = maparc.get(a);
					newa.getAttributeMap().put(AttributeMap.LABEL,i.toString() );
					newa.getAttributeMap().put(AttributeMap.SHOWLABEL,true );
				}

			}

		}

		for(Place p : net.getPlaces() ){
			String name = p.getLabel();
			if(name2performance.containsKey(name)){
				PerformanceData res = name2performance.get(name);
				String r="<html>SyncTime:"+res.getSynchTime()+"<br/>WaitTime:"+res.getWaitTime()+"<br/>SoujourTime:"+res.getTime()+"<br/>CountToken "+res.getTokenCount()+"</html>";
				if(res.getSynchTime()>0){
					p.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.GREEN);
				}else if(res.getWaitTime()>0){
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

	public static String toHTMLfromMPP(Map<Place, PerformanceData> Result ){
		String start = "<table border=\"2px\" style=\"width: 100%\">";
		String end ="</table>";
		String placen ="<td>Name Place</td>\n";
		String waitt="<td>WaitTime</td>\n";
		String synct="<td>SyncTime</td>\n";
		String sogtime="<td>SoggTime</td>\n";

		String out = "";
		
		for(Place place : Result.keySet()){
			PerformanceData perRes =	Result.get(place);
			placen+="<td>"+place.getLabel()+"</td>";
			waitt += "<td>"+perRes.getWaitTime()+"</td>";
			synct += "<td>"+perRes.getSynchTime()+"</td>";
			sogtime += "<td>"+perRes.getTime()+"</td>";
		}

		out=start+"<tr>"+placen+"</tr>"+"<tr>"+waitt+"</tr>"+"<tr>"+synct+"</tr>"+"<tr>"+sogtime+"</tr>"+end;
		return "<html>"+out+"</html>";
	}

}
