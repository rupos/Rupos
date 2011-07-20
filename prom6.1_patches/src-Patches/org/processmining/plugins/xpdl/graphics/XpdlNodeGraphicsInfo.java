package org.processmining.plugins.xpdl.graphics;

import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="NodeGraphicsInfo"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:Coordinates" minOccurs="0"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="ToolId"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="IsVisible"
 *         type="xsd:boolean" use="optional" default="true"/> <xsd:attribute
 *         name="Page" type="xsd:NMTOKEN" use="optional"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in XPDL 2.1, now use PageId and Page
 *         element</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="PageId" type="xpdl:IdRef" use="optional"/>
 *         <xsd:attribute name="LaneId" type="xsd:NMTOKEN" use="optional"/>
 *         <xsd:attribute name="Height" type="xsd:double" use="optional"/>
 *         <xsd:attribute name="Width" type="xsd:double" use="optional"/>
 *         <xsd:attribute name="BorderColor" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="FillColor" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="Shape" type="xsd:string" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlNodeGraphicsInfo extends XpdlElement {

	/*
	 * Attributes
	 */
	private String toolId;
	private String isVisible;
	private String page;
	private String pageId;
	private String laneId;
	private String height;
	private String width;
	private String borderColor;
	private String fillColor;
	private String shape;

	/*
	 * Elements
	 */
	private XpdlCoordinates coordinates;

	public XpdlCoordinates getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(XpdlCoordinates coordinates) {
		this.coordinates = coordinates;
	}
	public XpdlNodeGraphicsInfo(String tag,String pid,String x, String y) {
		super(tag);

		toolId = "ProMToll";
		isVisible = "true";
		page = null;
		pageId = pid;
		laneId = "ID-4";
		height = "40";
		width = "100";
		borderColor = null;
		fillColor = null;
		shape = null;

		coordinates = new XpdlCoordinates("Coordinates");
		coordinates.setyCoordinate(y);
		
		coordinates.setxCoordinate(x);
		
	}
	public XpdlNodeGraphicsInfo(String tag) {
		super(tag);

		toolId = null;
		isVisible = "true";
		page = null;
		pageId = null;
		laneId = null;
		height = "40";
		width = "100";
		borderColor = null;
		fillColor = null;
		shape = null;

		coordinates = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Coordinates")) {
			coordinates = new XpdlCoordinates("Coordinates");
			coordinates.importElement(xpp, xpdl);
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
		if (coordinates != null) {
			s += coordinates.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ToolId");
		if (value != null) {
			toolId = value;
		}
		value = xpp.getAttributeValue(null, "IsVisible");
		if (value != null) {
			isVisible = value;
		}
		value = xpp.getAttributeValue(null, "Page");
		if (value != null) {
			page = value;
		}
		value = xpp.getAttributeValue(null, "PageId");
		if (value != null) {
			pageId = value;
		}
		value = xpp.getAttributeValue(null, "LaneId");
		if (value != null) {
			laneId = value;
		}
		value = xpp.getAttributeValue(null, "Height");
		if (value != null) {
			height = value;
		}
		value = xpp.getAttributeValue(null, "Width");
		if (value != null) {
			width = value;
		}
		value = xpp.getAttributeValue(null, "BorderColor");
		if (value != null) {
			borderColor = value;
		}
		value = xpp.getAttributeValue(null, "FillColor");
		if (value != null) {
			fillColor = value;
		}
		value = xpp.getAttributeValue(null, "Shape");
		if (value != null) {
			shape = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (toolId != null) {
			s += exportAttribute("ToolId", toolId);
		}
		if (isVisible != null) {
			s += exportAttribute("IsVisible", isVisible);
		}
		if (page != null) {
			s += exportAttribute("Page", page);
		}
		if (pageId != null) {
			s += exportAttribute("PageId", pageId);
		}
		if (laneId != null) {
			s += exportAttribute("LaneId", laneId);
		}
		if (height != null) {
			s += exportAttribute("Height", height);
		}
		if (width != null) {
			s += exportAttribute("Width", width);
		}
		if (borderColor != null) {
			s += exportAttribute("BorderColor", borderColor);
		}
		if (fillColor != null) {
			s += exportAttribute("FillColor", fillColor);
		}
		if (shape != null) {
			s += exportAttribute("Shape", shape);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkBoolean(xpdl, "IsVisible", isVisible, false);
		checkDouble(xpdl, "Height", height, false);
		checkDouble(xpdl, "Weight", width, false);
	}
}
