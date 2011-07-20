package org.processmining.plugins.xpdl.collections;

import java.awt.geom.Point2D;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.plugins.xpdl.idname.XpdlAssociation;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Associations"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence maxOccurs="unbounded"> <xsd:element
 *         ref="xpdl:Association"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlAssociations extends XpdlCollections<XpdlAssociation> {

	public XpdlAssociations(String tag) {
		super(tag);
	}
	public XpdlAssociations(String tag,String s, String t, String pid, Point2D xysource , Point2D xytarget) {
		super(tag);
		this.list.add(new XpdlAssociation("Association",s,t,pid,xysource,xytarget));
	}
	public XpdlAssociation create() {
		return new XpdlAssociation("Association");
	}

	public void convertToBpmn(BPMNDiagram bpmn, SubProcess parent,
			Map<String, BPMNNode> id2node) {
		// TODO Auto-generated method stub
		for(XpdlAssociation xs : this.list){
			
			xs.convertToBpmn(bpmn,parent,id2node);
		}
		
	}

}
