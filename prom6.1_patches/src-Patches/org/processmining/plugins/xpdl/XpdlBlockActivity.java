package org.processmining.plugins.xpdl;

import java.util.Arrays;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.plugins.xpdl.collections.XpdlActivitySets;
import org.processmining.plugins.xpdl.idname.XpdlActivitySet;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="BlockActivity"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="ActivitySetId" type="xpdl:IdRef" use="required">
 *         <xsd:annotation> <xsd:documentation>BPMN: Corresponds to embedded
 *         subprocess. Pointer to ActivitySet/@Id in XPDL.</xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute
 *         name="StartActivityId" type="xpdl:IdRef" use="optional"/>
 *         <xsd:attribute name="View" use="optional" default="COLLAPSED">
 *         <xsd:annotation> <xsd:documentation>BPMN: Determines whether the
 *         subprocess is rendered as Collapsed or Expanded in diagram. Default
 *         is Collapsed.</xsd:documentation> </xsd:annotation> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration
 *         value="COLLAPSED"/> <xsd:enumeration value="EXPANDED"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlBlockActivity extends XpdlElement {

	/*
	 * Attributes
	 */
	private String activitySetId;
	private String startActivityId;
	private String view;

	public XpdlBlockActivity(String tag) {
		super(tag);

		activitySetId = null;
		startActivityId = null;
		view = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ActivitySetId");
		if (value != null) {
			activitySetId = value;
		}
		value = xpp.getAttributeValue(null, "StartActivityId");
		if (value != null) {
			startActivityId = value;
		}
		value = xpp.getAttributeValue(null, "View");
		if (value != null) {
			view = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (activitySetId != null) {
			s += exportAttribute("ActivitySetId", activitySetId);
		}
		if (startActivityId != null) {
			s += exportAttribute("StartActivityId", startActivityId);
		}
		if (view != null) {
			s += exportAttribute("View", view);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "ActivitySetId", activitySetId);
		checkRestriction(xpdl, "View", view, Arrays.asList("COLLAPSED", "EXPANDED"), false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, String id, String name, SubProcess parent,
			XpdlActivitySets activitySets, Map<String, BPMNNode> id2node, XpdlLoop loop ) {
		SubProcess subProcess = bpmn.addSubProcess(name, loop != null && loop.hasType("Standard"), false, false, loop != null && loop
				.hasType("MultiInstance"), false, parent);
		id2node.put(id, subProcess);
		convertToBpmn(bpmn, subProcess, activitySets, id2node);
	}

	public void convertToBpmn(BPMNDiagram bpmn, SubProcess subProcess, XpdlActivitySets activitySets,
			Map<String, BPMNNode> id2node ) {
		if ((activitySetId != null) && (activitySets != null)) {
			XpdlActivitySet activitySet = activitySets.get(activitySetId);
			if (activitySet != null) {
				activitySet.convertToBpmn(bpmn, subProcess, activitySets, id2node);
			}
		}
	}
}
