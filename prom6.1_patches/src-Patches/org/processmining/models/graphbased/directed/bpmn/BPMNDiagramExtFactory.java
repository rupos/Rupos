package org.processmining.models.graphbased.directed.bpmn;

public class BPMNDiagramExtFactory {

	private BPMNDiagramExtFactory() {
	}

	public static BPMNDiagramExt newBPMNDiagram(String label) {
		return new BPMNDiagramExtImpl(label);
	}

	public static BPMNDiagramExt cloneBPMNDiagram(BPMNDiagram diagram) {
		
		
		BPMNDiagramExtImpl newDiagram = new BPMNDiagramExtImpl(diagram.getLabel());
		newDiagram.cloneFrom(diagram);
		return newDiagram;
	}
	
public static BPMNDiagramExt cloneBPMNDiagram(BPMNDiagramExt diagram) {
		BPMNDiagramExtImpl newDiagram = new BPMNDiagramExtImpl(diagram.getLabel());
		newDiagram.cloneFrom(diagram);
		return newDiagram;
	}
	
}
