package org.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.contexts.util.StringVisualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.plugins.petrinet.replayfitness.conformance.TotalConformanceResult;
import org.processmining.plugins.xpdl.Xpdl;

public class TraslateBPMNResult {

	private BPMNDiagramExt bpmn=null;
	
	private Petrinet petri=null;
	private Marking marking=null;
	private Collection<String> error = null;


	private Map<String, Place> placeMap = null;
	private Map<String, BPMNNode> id2node = null;
	private Map<String,String> node2id = null;

	public Map<String, String> getNode2id() {
		return node2id;
	}


	public Map<String, BPMNNode> getid2nodeMap() {
		return id2node;
	}

	public Collection<String> getError() {
		return error;
	}


	public void setError(Collection<String> error) {
		this.error = error;
	}


	public void setid2nodeMap(Map<String, BPMNNode> flowidMap) {
		this.id2node = flowidMap;
		createNode2id();
	}


	private void createNode2id() {
		if(!id2node.isEmpty()){
			node2id = new HashMap<String, String>();
			for(String id : id2node.keySet()){
				node2id.put(id2node.get(id).getLabel(),id);

			}
		}

	}


	private Xpdl xpdl=null;

	public TraslateBPMNResult(BPMNDiagramExt bpmn2, Petrinet p,Marking m, Map<String, Place> mp, Xpdl x,Map<String, BPMNNode> id2n,Collection<String> er){
		this.bpmn=bpmn2;
		this.petri=p;
		this.marking=m;
		this.placeMap=mp;
		this.setXpdl(x);
		this.id2node=id2n;
		createNode2id();
		this.error=er;
	}


	public BPMNDiagramExt getBpmn() {
		return bpmn;
	}

	public void setBpmn(BPMNDiagramExt bpmn) {
		this.bpmn = bpmn;
	}

	public Petrinet getPetri() {
		return petri;
	}

	public void setPetri(Petrinet petri) {
		this.petri = petri;
	}

	public Marking getMarking() {
		return marking;
	}

	public void setMarking(Marking marking) {
		this.marking = marking;
	}

	public Map<String, Place> getPlaceMap() {
		return placeMap;
	}

	public void setPlaceMap(Map<String, Place> placeMap) {
		this.placeMap = placeMap;
	}


	public void setXpdl(Xpdl xpdl) {
		this.xpdl = xpdl;
	}


	public Xpdl getXpdl() {
		return xpdl;
	}




	public String toString() {
		if(error!=null){
			if(!error.isEmpty()){
				return error.toString();
			}
		}
		return "TraslateBPMNResult"+"NESSUN ERRORE DA SEGNalare";
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()

	public String toString(){
		String res = petri.toString();
		res+= marking.toString();
		res+= bpmn.toString();
		return res;

	} */
	@Visualizer
	@Plugin(name = "Traslate Result Visualizer", parameterLabels = {"BPMN Diagram-tring"}, returnLabels = "BPMN Diagram abel of tring", returnTypes = JComponent.class)
	/*@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent visualize(PluginContext context, TraslateBPMNResult tovisualize) {
		return StringVisualizer.visualize(context, tovisualize.toString());
		//return ProMJGraphVisualizer.instance().visualizeGraph(context, tovisualize.getBpmn());
	}*/

	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent visualize(PluginContext context, TraslateBPMNResult tovisualize) {
		Progress progress = context.getProgress();
		BPMNTraslatePanel panel = new BPMNTraslatePanel(context, progress,tovisualize);
		return panel;
		//return ProMJGraphVisualizer.instance().visualizeGraph(context,tovisualize.getBpmn() );
	}


}
