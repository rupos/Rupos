

package org.processmining.models.graphbased.directed.bpmn.elements;

import org.jgraph.graph.GraphConstants;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

public class FlowAssociation extends BPMNEdge<BPMNNode, BPMNNode> {

	public FlowAssociation(BPMNNode source, BPMNNode target, SubProcess parent) {
		super(source, target, parent);
		float[] v = new float[2];
        v[0] = 1;
        v[1] = 3;
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_NONE);
		getAttributeMap().put(AttributeMap.DASHPATTERN, v );
	}
	
	

	public boolean equals(Object o) {
		return (o == this);
	}

}
