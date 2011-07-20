package org.processmining.plugins.xpdl.collections;

import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.plugins.xpdl.idname.XpdlWorkflowProcess;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="WorkflowProcesses"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:WorkflowProcess" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlWorkflowProcesses extends XpdlCollections<XpdlWorkflowProcess> {

	public XpdlWorkflowProcesses(String tag) {
		super(tag);
	}

	public XpdlWorkflowProcess create() {
		return new XpdlWorkflowProcess("WorkflowProcess");
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		if (!list.isEmpty()) {
			list.get(0).convertToBpmn(bpmn, id2node);
		}
	}
}
