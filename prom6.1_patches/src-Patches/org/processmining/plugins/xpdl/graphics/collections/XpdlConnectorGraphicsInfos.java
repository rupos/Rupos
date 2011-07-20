package org.processmining.plugins.xpdl.graphics.collections;

import java.awt.geom.Point2D;

import org.processmining.plugins.xpdl.collections.XpdlCollections;
import org.processmining.plugins.xpdl.graphics.XpdlConnectorGraphicsInfo;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ConnectorGraphicsInfos"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:ConnectorGraphicsInfo"
 *         minOccurs="0" maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlConnectorGraphicsInfos extends XpdlCollections<XpdlConnectorGraphicsInfo> {

	public XpdlConnectorGraphicsInfos(String tag) {
		super(tag);
	}
	public XpdlConnectorGraphicsInfos(String tag,String pid, Point2D xysource , Point2D xytarget) {
		super(tag);
		this.list.add(new  XpdlConnectorGraphicsInfo("ConnectorGraphicsInfo",pid,xysource,xytarget));
	}
	public XpdlConnectorGraphicsInfo create() {
		return new XpdlConnectorGraphicsInfo("ConnectorGraphicsInfo");
	}
}
