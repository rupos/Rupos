package org.processmining.plugins.xpdl;

import org.processmining.plugins.xpdl.text.XpdlText;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Created"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlCreated extends XpdlText  {

	public XpdlCreated(String tag) {
		super(tag);
	}
}