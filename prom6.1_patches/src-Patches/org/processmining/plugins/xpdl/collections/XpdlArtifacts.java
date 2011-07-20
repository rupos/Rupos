package org.processmining.plugins.xpdl.collections;

import org.processmining.plugins.xpdl.idname.XpdlArtifact;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.collections.XpdlActivitySets;
/**
 * @author hverbeek
 * 
 *         <xsd:element name="Artifacts"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence maxOccurs="unbounded"> <xsd:element
 *         ref="xpdl:Artifact"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlArtifacts extends XpdlCollections<XpdlArtifact> {

	public XpdlArtifacts(String tag) {
		super(tag);
	}

	public XpdlArtifact create() {
		return new XpdlArtifact("Artifact");
	}

	public void convertToBpmn(BPMNDiagram bpmn, SubProcess parent,
			Map<String, BPMNNode> id2node) {
		
			for(XpdlArtifact xa : this.list){
				xa.convertToBpmn(bpmn, parent,  id2node);
				
			}
		

	}

}
