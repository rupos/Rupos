package org.processmining.plugins.bpmn;

import java.util.HashMap;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public class TraslateBPMNResult {
	
	private BPMNDiagram bpmn=null;
	private Petrinet petri=null;
	private Marking marking=null;
	private Map<String, Place> placeMap = null;
	
	 public TraslateBPMNResult(BPMNDiagram b, Petrinet p,Marking m, Map<String, Place> mp){
		 this.bpmn=b;
		 this.petri=p;
		 this.marking=m;
		 this.placeMap=mp;
				
	}
	 public TraslateBPMNResult(Map<String, Place> mp){
	
		 this.placeMap=mp;		
	}

	public BPMNDiagram getBpmn() {
		return bpmn;
	}

	public void setBpmn(BPMNDiagram bpmn) {
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
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	
	public String toString(){
		String res = petri.toString();
		res+= marking.toString();
		res+= bpmn.toString();
		return res;
		
	} */
	
	

}
