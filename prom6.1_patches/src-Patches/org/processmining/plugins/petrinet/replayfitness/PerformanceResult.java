package org.processmining.plugins.petrinet.replayfitness;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;

public class PerformanceResult {
	Map<Place,PerformanceData> mapperformance;
	Map<Arc, Integer> maparc;
	
	public PerformanceResult() {
		this.mapperformance = new HashMap<Place,PerformanceData>();
		this.maparc = new HashMap<Arc, Integer>();
	}

	public Map<Place, PerformanceData> getList() {
		return mapperformance;
	}

	public void setList(Map<Place, PerformanceData> list) {
		this.mapperformance = list;
	}

	public Map<Arc, Integer> getMaparc() {
		return maparc;
	}

	public void setMaparc(Map<Arc, Integer> maparc) {
		this.maparc = maparc;
	}

	
	public String toString() {
		return "PerformanceResult=" + mapperformance + "]";
	}
	
	
	
	
	
	

}
