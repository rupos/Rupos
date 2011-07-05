package org.processmining.plugins.xpdl;

import java.util.Arrays;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Route"> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="GatewayType" use="optional" default="Exclusive">
 *         <xsd:annotation> <xsd:documentation> Used when needed for BPMN
 *         Gateways. Gate and sequence information is associated with the
 *         Transition Element.</xsd:documentation> </xsd:annotation>
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="XOR"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> </xsd:enumeration> <xsd:enumeration
 *         value="Exclusive"/> <xsd:enumeration value="OR"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> </xsd:enumeration> <xsd:enumeration
 *         value="Inclusive"/> <xsd:enumeration value="Complex"/>
 *         <xsd:enumeration value="AND"/> <xsd:enumeration value="Parallel"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="XORType" use="optional" default="Data"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="Data"/> <xsd:enumeration
 *         value="Event"/> </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:attribute name="ExclusiveType" use="optional" default="Data">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="Data"/> <xsd:enumeration value="Event"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="Instantiate" type="xsd:boolean" use="optional"
 *         default="false"/> <xsd:attribute name="MarkerVisible"
 *         type="xsd:boolean" use="optional" default="false"> <xsd:annotation>
 *         <xsd:documentation>Applicable only to XOR
 *         Gateways</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="IncomingCondition" type="xsd:string"
 *         use="optional"/> <xsd:attribute name="OutgoingCondition"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlRoute extends XpdlElement {

	/*
	 * Attributes
	 */
	private String gatewayType;
	private String xorType;
	private String exclusiveType;
	private String instantiate;
	private String markerVisible;
	private String incomingCondition;
	private String outgoingCondition;

	public XpdlRoute(String tag) {
		super(tag);

		gatewayType = null;
		xorType = null;
		exclusiveType = null;
		instantiate = null;
		markerVisible = null;
		incomingCondition = null;
		outgoingCondition = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "GatewayType");
		if (value != null) {
			gatewayType = value;
		}
		value = xpp.getAttributeValue(null, "XORType");
		if (value != null) {
			xorType = value;
		}
		value = xpp.getAttributeValue(null, "ExclusiveType");
		if (value != null) {
			exclusiveType = value;
		}
		value = xpp.getAttributeValue(null, "Instantiate");
		if (value != null) {
			instantiate = value;
		}
		value = xpp.getAttributeValue(null, "MarkerVisible");
		if (value != null) {
			markerVisible = value;
		}
		value = xpp.getAttributeValue(null, "IncomingCondition");
		if (value != null) {
			incomingCondition = value;
		}
		value = xpp.getAttributeValue(null, "OutgoingCondition");
		if (value != null) {
			outgoingCondition = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (gatewayType != null) {
			s += exportAttribute("GatewayType", gatewayType);
		}
		if (xorType != null) {
			s += exportAttribute("XORType", xorType);
		}
		if (exclusiveType != null) {
			s += exportAttribute("ExclusiveType", exclusiveType);
		}
		if (instantiate != null) {
			s += exportAttribute("Instantiate", instantiate);
		}
		if (markerVisible != null) {
			s += exportAttribute("MarkerVisible", markerVisible);
		}
		if (incomingCondition != null) {
			s += exportAttribute("IncomingCondition", incomingCondition);
		}
		if (outgoingCondition != null) {
			s += exportAttribute("OutgoingCondition", outgoingCondition);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "GatewayType", gatewayType, Arrays.asList("XOR", "Exclusive", "OR", "Inclusive",
				"Complex", "AND", "Parallel"), false);
		checkRestriction(xpdl, "XORType", xorType, Arrays.asList("Data", "Event"), false);
		checkRestriction(xpdl, "ExclusiveType", exclusiveType, Arrays.asList("Data", "Event"), false);
		checkBoolean(xpdl, "Instantiate", instantiate, false);
		checkBoolean(xpdl, "MarkerVisible", markerVisible, false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, String id, String name, SubProcess parent, Map<String, BPMNNode> id2node) {
		GatewayType type = GatewayType.DATABASED;
		if (gatewayType != null) {
			if (gatewayType.equalsIgnoreCase("XOR")|| gatewayType.equalsIgnoreCase("Exclusive")) {
				if ((instantiate != null) && (instantiate.equalsIgnoreCase("true"))) {
					type = GatewayType.EVENTBASED;
				}
			} else if (gatewayType.equalsIgnoreCase("AND")|| gatewayType.equalsIgnoreCase("Parallel")) {
				type = GatewayType.PARALLEL;
			} else if (gatewayType.equalsIgnoreCase("OR")|| gatewayType.equalsIgnoreCase("Inclusive")) {
				type = GatewayType.INCLUSIVE;
			} else if (gatewayType.equalsIgnoreCase("Complex")) {
				type = GatewayType.COMPLEX;
			}
		}
		id2node.put(id, bpmn.addGateway(name, type, parent));
	}
}
