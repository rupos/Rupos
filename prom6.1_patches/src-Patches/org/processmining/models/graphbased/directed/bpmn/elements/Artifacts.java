package org.processmining.models.graphbased.directed.bpmn.elements;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.shapes.AbstractShape;
import org.processmining.models.shapes.Decorated;



public class Artifacts extends BPMNNode implements Decorated {
	
	protected final static int stdWidth = 150;
	protected final static int stdHeight = 50;	
	
	public enum ArtifactType {
		TEXTANNOATION, GROUP , DATAOBJECT
	}
	
	private ArtifactType artifactType = ArtifactType.TEXTANNOATION;

	public ArtifactType getArtifactType() {
		return artifactType;
	}

	public void setArtifactType(ArtifactType artifactType) {
		this.artifactType = artifactType;
	}

	public Artifacts(
			AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,String label,  ArtifactType at ) {
		super(bpmndiagram);
		fillAttributes(label,at);
		
	}
	
	public Artifacts(
			AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,String label,  ArtifactType at, SubProcess parent ) {
		super(bpmndiagram,parent);
		fillAttributes(label,at);
		
	}
	
	public Artifacts(
			AbstractDirectedGraph<BPMNNode, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> bpmndiagram,String label,  ArtifactType at, Swimlane parentSwimlane ) {
		super(bpmndiagram,parentSwimlane);
		fillAttributes(label,at);
		
	}
	
	

	private void fillAttributes(String label, ArtifactType at) {
		
		this.artifactType = at;
		getAttributeMap().put(AttributeMap.LABEL, label);
		//getAttributeMap().put(AttributeMap.SHOWLABEL, false);
		getAttributeMap().put(AttributeMap.SHAPE, new semiRectange());
		getAttributeMap().put(AttributeMap.SQUAREBB, true); 
		getAttributeMap().put(AttributeMap.RESIZABLE, true);
		getAttributeMap().put(AttributeMap.SIZE, new Dimension(stdWidth, stdHeight));
	}
	
	
	public Swimlane getParentSwimlane() {
		if (getParent() != null) {
			if (getParent() instanceof Swimlane)
				return (Swimlane) getParent();
			else
				return null;
		}
		return null;
	}

	public SubProcess getParentSubProcess() {
		if (getParent() != null) {
			if (getParent() instanceof SubProcess)
				return (SubProcess) getParent();
			else
				return null;
		}
		return null;
	}
	
	
	

	
	public void decorate(Graphics2D g2d, double x, double y, double width,
			double height) {
		
		
		/*int nrDecorators = 0;

		GeneralPath activityDecorator = new GeneralPath();
		
		activityDecorator.moveTo(width-20, 10);
		activityDecorator.lineTo(width, 10);
		activityDecorator.moveTo(width-20,10);
		activityDecorator.lineTo(width-20, 0);
		
		activityDecorator.moveTo(width-20, 0);
		activityDecorator.lineTo(width, 10);
		
		
		
		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		activityDecorator.transform(at);

		g2d.draw(activityDecorator);*/
		
	}

}
 class semiRectange extends AbstractShape {

	

	public semiRectange() {
		
	}


	public GeneralPath getPath(double x, double y, double width, double height) {

		
		GeneralPath path = new GeneralPath();
			
		path.moveTo(0,height );
		path.lineTo(0, 0);
		path.moveTo(0,height );
		path.lineTo(width/3, height);
		path.moveTo(0,0 );
		path.lineTo(width/3, 0);
		// Width and height have correct ratio;
		
		
		
		return path;

	}

}