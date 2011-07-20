package org.processmining.plugins.xpdl.idname;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Map;

import org.processmining.models.graphbased.AbstractGraph;
import org.processmining.models.graphbased.AbstractGraphElement;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.FlowAssociation;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.graphics.collections.XpdlConnectorGraphicsInfos;
import org.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Association"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence minOccurs="0"> <xsd:element
 *         ref="xpdl:Object"/> <xsd:element ref="xpdl:ConnectorGraphicsInfos"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Source" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Target" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="AssociationDirection" use="optional" default="None">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="None"/> <xsd:enumeration value="To"/>
 *         <xsd:enumeration value="From"/> <xsd:enumeration value="Both"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */

public class XpdlAssociation extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String source;
	private String target;
	private String associationDirection;

	/*
	 * Elements
	 */
	private XpdlObject object;
	private XpdlConnectorGraphicsInfos connectorGraphicsInfos;

	public XpdlAssociation(String tag) {
		super(tag);

		source = null;
		target = null;
		associationDirection = null;

		object = null;
		connectorGraphicsInfos = null;
	}
	public XpdlAssociation(String tag,String s,String t,String pid, Point2D xysource , Point2D xytarget) {
		super(tag);

		source = s;
		target = t;
		associationDirection = "None";

		object = null;
		connectorGraphicsInfos  = new XpdlConnectorGraphicsInfos("ConnectorGraphicsInfos",pid,xysource,xytarget);
	}
	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ConnectorGraphicsInfos")) {
			connectorGraphicsInfos = new XpdlConnectorGraphicsInfos("ConnectorGraphicsInfos");
			connectorGraphicsInfos.importElement(xpp, xpdl);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if (object != null) {
			s += object.exportElement();
		}
		if (connectorGraphicsInfos != null) {
			s += connectorGraphicsInfos.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Source");
		if (value != null) {
			source = value;
		}
		value = xpp.getAttributeValue(null, "Target");
		if (value != null) {
			target = value;
		}
		value = xpp.getAttributeValue(null, "AssociationDirection");
		if (value != null) {
			associationDirection = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (source != null) {
			s += exportAttribute("Source", source);
		}
		if (target != null) {
			s += exportAttribute("Target", target);
		}
		if (associationDirection != null) {
			s += exportAttribute("AssociationDirection", associationDirection);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "Source", source);
		checkRequired(xpdl, "Target", target);
		checkRestriction(xpdl, "AssociationDirection", associationDirection, Arrays
				.asList("None", "To", "From", "Both"), false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, SubProcess parent,
			Map<String, BPMNNode> id2node) {
		if ((source != null) && (target != null)) {
			if(id2node.get(source) instanceof BPMNNode){
				BPMNNode fromNode = id2node.get(source);
				BPMNNode toNode = id2node.get(target);
				if ((fromNode != null) && (toNode != null)) {
					bpmn.addFlowAssociation(fromNode, toNode, parent);
				}
			}else{
				//Flow f = bpmn.getFlows().iterator().next();
				
				//AbstractGraphElement asb = new AbstractGraph();
				
				//asb.getAttributeMap().put(key, value)
				//asb = f;
				//BPMNNode toNode = id2node.get(target); 
				//FlowAssociation fl  = bpmn.addFlowAssociation(toNode, toNode, parent);
				
			}
			
		}

	}
}
