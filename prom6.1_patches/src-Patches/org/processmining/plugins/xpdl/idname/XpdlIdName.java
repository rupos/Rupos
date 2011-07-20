package org.processmining.plugins.xpdl.idname;

import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

public class XpdlIdName extends XpdlElement {
	/*
	 * Attributes
	 */
	protected String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected String name;
	
	
	public Object clone() {
		XpdlIdName ele = new XpdlIdName(tag);
		ele.id = id;
		
		return ele;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public XpdlIdName(String tag) {
		super(tag);

		id = null;
		name = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Id");
		if (value != null) {
			id = value;
		}
		value = xpp.getAttributeValue(null, "Name");
		if (value != null) {
			name = value;
		}else{
			name = id;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (id != null) {
			s += exportAttribute("Id", id);
		}
		if (name != null) {
			s += exportAttribute("Name", name);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		checkRequired(xpdl, "Id", id);
	}
}
