package org.processmining.plugins.xpdl.idname;

import java.util.Arrays;
import java.util.Map;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.collections.XpdlActivitySets;
import org.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import org.xmlpull.v1.XmlPullParser;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;
/**
 * @author hverbeek
 * 
 *         <xsd:element name="Artifact"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Not further defined
 *         here.</xsd:documentation> </xsd:annotation> <xsd:complexType>
 *         <xsd:sequence minOccurs="0"> <xsd:element ref="xpdl:Object"
 *         minOccurs="0"/> <xsd:element ref="xpdl:Group" minOccurs="0"/>
 *         <xsd:element ref="xpdl:DataObject" minOccurs="0"/> <xsd:element
 *         ref="xpdl:NodeGraphicsInfos" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="ArtifactType"
 *         use="required"> <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="DataObject"/> <xsd:enumeration
 *         value="Group"/> <xsd:enumeration value="Annotation"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="TextAnnotation" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="Group" type="xsd:string" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */

public class XpdlArtifact extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String artifactType;
	private String textAnnotation;
	public String getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(String artifactType) {
		this.artifactType = artifactType;
	}

	public String getTextAnnotation() {
		return textAnnotation;
	}

	public void setTextAnnotation(String textAnnotation) {
		this.textAnnotation = textAnnotation;
	}

	private String groupAttribute;

	/*
	 * Elements
	 */
	private XpdlObject object;
	private XpdlGroup groupElement;
	private XpdlDataObject dataObject;
	private XpdlNodeGraphicsInfos nodeGraphicsInfos;

	public XpdlArtifact(String tag) {
		super(tag);

		artifactType = null;
		textAnnotation = null;
		groupAttribute = null;

		object = null;
		groupElement = null;
		dataObject = null;
		nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos") ;
	}
	public XpdlArtifact(String tag,String pid,String x, String y,String type,String text) {
		super(tag);

		artifactType = type;
		textAnnotation = text;
		groupAttribute = null;

		object = null;
		groupElement = null;
		dataObject = null;
		nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos", pid, x,  y) ;
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
		if (xpp.getName().equals("Group")) {
			groupElement = new XpdlGroup("Group");
			groupElement.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("DataObject")) {
			dataObject = new XpdlDataObject("DataObject");
			dataObject.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("NodeGraphicsInfos")) {
			nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
			nodeGraphicsInfos.importElement(xpp, xpdl);
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
		if (groupElement != null) {
			s += groupElement.exportElement();
		}
		if (dataObject != null) {
			s += dataObject.exportElement();
		}
		if (nodeGraphicsInfos != null) {
			s += nodeGraphicsInfos.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ArtifactType");
		if (value != null) {
			artifactType = value;
		}
		value = xpp.getAttributeValue(null, "TextAnnotation");
		if (value != null) {
			textAnnotation = value;
		}
		value = xpp.getAttributeValue(null, "Group");
		if (value != null) {
			groupAttribute = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (artifactType != null) {
			s += exportAttribute("ArtifactType", artifactType);
		}
		if (textAnnotation != null) {
			s += exportAttribute("TextAnnotation", textAnnotation);
		}
		if (groupAttribute != null) {
			s += exportAttribute("Group", groupAttribute);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "ArtifactType", artifactType, Arrays.asList("DataObject", "Group", "Annotation"), true);
	}
	
	public void convertToBpmn(BPMNDiagram bpmn, SubProcess parent, 
			Map<String, BPMNNode> id2node) {
		if(artifactType != null){
			if(artifactType.equals("Annotation"))
			id2node.put(id, bpmn.addArtifacts(textAnnotation, ArtifactType.TEXTANNOATION, parent));
		}
		
	}

}
