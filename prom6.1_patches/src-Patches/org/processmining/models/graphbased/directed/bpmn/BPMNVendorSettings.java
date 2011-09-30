/**
 * 
 */
package org.processmining.models.graphbased.directed.bpmn;

/**
 * @author ibayraktar
 * 
 */
public class BPMNVendorSettings {

	public enum GatewayVisibilitySettings {
		Implicit, Explicit
	}

	public static GatewayVisibilitySettings[] settingsArray = { GatewayVisibilitySettings.Implicit,
			GatewayVisibilitySettings.Explicit };

	private GatewayVisibilitySettings ANDSplit;
	private GatewayVisibilitySettings ANDJoin;
	private boolean ANDMarker;
	private GatewayVisibilitySettings XORSplit;
	private GatewayVisibilitySettings XORJoin;
	private boolean XORMarker;
	private GatewayVisibilitySettings ORSplit;
	private GatewayVisibilitySettings ORJoin;
	private boolean ORMarker;

	public BPMNVendorSettings() {
		//Default settings are set as standard xpdl settings
		this.setStandardSettings();
	}
	
	public void setStandardSettings() {
		this.setANDJoin(GatewayVisibilitySettings.Explicit);
		this.setANDSplit(GatewayVisibilitySettings.Explicit);
		this.setANDMarker(true);
		this.setXORJoin(GatewayVisibilitySettings.Explicit);
		this.setXORSplit(GatewayVisibilitySettings.Explicit);
		this.setXORMarker(true);
		this.setORJoin(GatewayVisibilitySettings.Explicit);
		this.setORSplit(GatewayVisibilitySettings.Explicit);
		this.setORMarker(true);
	}

	public GatewayVisibilitySettings getANDSplit() {
		return ANDSplit;
	}

	public void setANDSplit(GatewayVisibilitySettings aNDSplit) {
		ANDSplit = aNDSplit;
	}

	public GatewayVisibilitySettings getXORSplit() {
		return XORSplit;
	}

	public void setXORSplit(GatewayVisibilitySettings xORSplit) {
		XORSplit = xORSplit;
	}

	public GatewayVisibilitySettings getORSplit() {
		return ORSplit;
	}

	public void setORSplit(GatewayVisibilitySettings oRSplit) {
		ORSplit = oRSplit;
	}

	public GatewayVisibilitySettings getANDJoin() {
		return ANDJoin;
	}

	public void setANDJoin(GatewayVisibilitySettings aNDJoin) {
		ANDJoin = aNDJoin;
	}

	public GatewayVisibilitySettings getXORJoin() {
		return XORJoin;
	}

	public void setXORJoin(GatewayVisibilitySettings xORJoin) {
		XORJoin = xORJoin;
	}

	public GatewayVisibilitySettings getORJoin() {
		return ORJoin;
	}

	public void setORJoin(GatewayVisibilitySettings oRJoin) {
		ORJoin = oRJoin;
	}

	public boolean getANDMarker() {
		return ANDMarker;
	}

	public void setANDMarker(boolean aNDMarker) {
		ANDMarker = aNDMarker;
	}

	public boolean getXORMarker() {
		return XORMarker;
	}

	public void setXORMarker(boolean xORMarker) {
		XORMarker = xORMarker;
	}

	public boolean getORMarker() {
		return ORMarker;
	}

	public void setORMarker(boolean oRMarker) {
		ORMarker = oRMarker;
	}

}
