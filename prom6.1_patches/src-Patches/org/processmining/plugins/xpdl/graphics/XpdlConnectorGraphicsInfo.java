package org.processmining.plugins.xpdl.graphics;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ConnectorGraphicsInfo"> <xsd:annotation>
 *         <xsd:documentation>BPMN and XPDL</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence minOccurs="0">
 *         <xsd:element ref="xpdl:Coordinates" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="ToolId" type="xsd:NMTOKEN"
 *         use="optional"/> <xsd:attribute name="IsVisible" type="xsd:boolean"
 *         use="optional" default="true"/> <xsd:attribute name="Page"
 *         type="xsd:NMTOKEN" use="optional"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in XPDL 2.1, now use PageId and Page
 *         element</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="PageId" type="xpdl:IdRef" use="optional"/>
 *         <xsd:attribute name="Style" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="BorderColor" type="xsd:string" use="optional"/>
 *         <xsd:attribute name="FillColor" type="xsd:string" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlConnectorGraphicsInfo extends XpdlElement {

	/*
	 * Attributes
	 */
	private String toolId;
	private String isVisible;
	private String pageDeprecated;
	private String pageId;
	private String style;
	private String borderColor;
	private String fillColor;

	/*
	 * Elements
	 */
	private final List<XpdlCoordinates> coordinatesList;

	public List<XpdlCoordinates> getCoordinatesList() {
		return coordinatesList;
	}
	public XpdlConnectorGraphicsInfo(String tag) {
		super(tag);

		toolId = null;
		isVisible = null;
		pageDeprecated = null;
		pageId = null;
		style = null;
		borderColor = null;
		fillColor = null;

		coordinatesList = new ArrayList<XpdlCoordinates>();
	}
	public XpdlConnectorGraphicsInfo(String tag,String pid, Point2D xysource , Point2D xytarget) {
		super(tag);

		toolId = "ProM";
		isVisible = "true";
		pageDeprecated = null;
		pageId = pid;
		style = null;
		borderColor = null;
		fillColor = null;

		coordinatesList = new ArrayList<XpdlCoordinates>();
		coordinatesList.add(new XpdlCoordinates("Coordinates", String.valueOf(xysource.getX()),String.valueOf(xysource.getY())));
		coordinatesList.add(new XpdlCoordinates("Coordinates", String.valueOf(xytarget.getX()),String.valueOf(xytarget.getY())));
	}
	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Coordinates")) {
			XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");
			coordinates.importElement(xpp, xpdl);
			coordinatesList.add(coordinates);
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
		for (XpdlCoordinates coordinates : coordinatesList) {
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
			pageDeprecated = value;
		}
		value = xpp.getAttributeValue(null, "PageId");
		if (value != null) {
			pageId = value;
		}
		value = xpp.getAttributeValue(null, "Style");
		if (value != null) {
			style = value;
		}
		value = xpp.getAttributeValue(null, "BorderColor");
		if (value != null) {
			borderColor = value;
		}
		value = xpp.getAttributeValue(null, "FillColor");
		if (value != null) {
			fillColor = value;
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
		if (pageDeprecated != null) {
			s += exportAttribute("Page", pageDeprecated);
		}
		if (pageId != null) {
			s += exportAttribute("PageId", pageId);
		}
		if (style != null) {
			s += exportAttribute("Style", style);
		}
		if (borderColor != null) {
			s += exportAttribute("BorderColor", borderColor);
		}
		if (fillColor != null) {
			s += exportAttribute("FillColor", fillColor);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkBoolean(xpdl, "IsVisible", isVisible, false);
		if (pageDeprecated != null) {
			xpdl.logInfo(tag, lineNumber, "Page is deprecated, use PageId and Page element");
		}
	}
}
