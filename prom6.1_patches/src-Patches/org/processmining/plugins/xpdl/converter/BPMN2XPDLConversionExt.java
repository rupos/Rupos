package org.processmining.plugins.xpdl.converter;


import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.NodeID;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.FlowAssociation;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.collections.XpdlArtifacts;
import org.processmining.plugins.xpdl.collections.XpdlAssociations;

import org.processmining.plugins.xpdl.graphics.XpdlCoordinates;
import org.processmining.plugins.xpdl.graphics.XpdlNodeGraphicsInfo;

import org.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;

import org.processmining.plugins.xpdl.idname.XpdlArtifact;
import org.processmining.plugins.xpdl.idname.XpdlAssociation;
import org.processmining.plugins.xpdl.idname.XpdlTransition;


public class BPMN2XPDLConversionExt extends BPMN2XPDLConversion {

	BPMNDiagramExt bpmnext;
	Xpdl xpdl;

	public BPMN2XPDLConversionExt(BPMNDiagramExt bpmn) {
		super(bpmn);
		bpmnext=bpmn;
	}


	public Xpdl fills_nolayout(){
		xpdl =this.convert2XPDL_noLayout();
		XpdlAssociations associations = new XpdlAssociations("Associations");	
		XpdlArtifacts artifatcs = new XpdlArtifacts("Artifacts");	
		xpdl.setArtifacts(artifatcs);
		xpdl.setAssociations(associations);
		fillArtifacts();

		fillAssociations();
		fillArcAttivated();
		fillSwim();
		return xpdl;
	}


	public Xpdl fills_layout(PluginContext context){
		xpdl =this.convert2XPDL(context);
		XpdlAssociations associations = new XpdlAssociations("Associations");

		XpdlArtifacts artifatcs = new XpdlArtifacts("Artifacts");

		xpdl.setArtifacts(artifatcs);
		xpdl.setAssociations(associations);
		fillArtifacts();

		fillXpdlArtifactsPositions(context);

		fillAssociations();
		fillArcAttivated();
		fillSwim();
		return xpdl;
	}



	private void fillArcAttivated() {
		Map<String, String> archiAttivatiBPMN = new HashMap<String, String>();
		for (Flow f : bpmnext.getFlows()) {
			String i = f.getLabel();

			archiAttivatiBPMN.put(String.valueOf(f.hashCode()), i);

			if (f.getAttributeMap().containsKey(AttributeMap.EDGECOLOR)) {
				if (f.getAttributeMap().get(AttributeMap.EDGECOLOR).equals(Color.RED)) {
					String label = f.getAttributeMap().get(AttributeMap.TOOLTIP).toString()
					.replaceAll("<html>", "").replaceAll("</html>", "").replaceAll("<br/>", " ");

					NodeID id = new NodeID();
					String targetassid = id.toString().replaceAll("node ", "");

					XpdlAssociations setass = null;
					if (xpdl.getAssociations() == null) {
						setass = new XpdlAssociations("Associations");
					} else {
						setass = xpdl.getAssociations();
					}

					XpdlAssociation newassociation = new XpdlAssociation("Association");



					newassociation.setSource("" +  f.hashCode());
					newassociation.setTarget("" + targetassid.hashCode());
					newassociation.setId("" + newassociation.hashCode());

					setass.add2List(newassociation);


					xpdl.setAssociations(setass);

					XpdlArtifact newartifact = new XpdlArtifact("Artifact");



					newartifact.setArtifactType("Annotation");

					newartifact.setTextAnnotation(label);
					newartifact.setId("" + targetassid.hashCode());
					XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");

					XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
					nodeGraphicsInfo.setToolId("ProM");
					
					nodeGraphicsInfo.setLaneId(String.valueOf(f.getTarget().getParentSwimlane().hashCode()));
					nodeGraphicsInfos.add2List(nodeGraphicsInfo);
					newartifact.setNodeGraphicsInfos(nodeGraphicsInfos);

					if (xpdl.getArtifacts() == null) {
						XpdlArtifacts xas = new XpdlArtifacts("Artifacts");
						xas.getList().add(newartifact);
						xpdl.setArtifacts(xas);
					} else {
						xpdl.getArtifacts().add2List(newartifact);
					}

				}
			}

		}

		// archi visitati
		for (XpdlTransition tr : xpdl.getWorkflowProcesses().getList()
				.get(0).getTransitions().getList()) {



			String i = (archiAttivatiBPMN.containsKey(tr.getId())) ? archiAttivatiBPMN
					.get(tr.getId()) : "0";
					if (i.equals("no label"))
						i = "0";
					tr.setName(i.toString());

		}


	}

	private void fillSwim() {
		Map<String, String> map3 = new HashMap<String, String>();
		for (BPMNNode bpmnNode : bpmnext.getNodes()) {
			if (bpmnNode.getParentSwimlane() != null) {
				map3.put("" + bpmnNode.hashCode(), "" + bpmnNode.getParentSwimlane().hashCode());
			}
		}
		if (!map3.isEmpty()) {
			for (XpdlArtifact xpdlArtifact : xpdl.getArtifacts().getList() ) {
				if (xpdlArtifact.getNodeGraphicsInfos() != null) {
					List<XpdlNodeGraphicsInfo> infos = xpdlArtifact.getNodeGraphicsInfos().getList();
					for (XpdlNodeGraphicsInfo xpdlNodeGraphicsInfo : infos) {
						if(map3.containsKey((xpdlArtifact.getId()))){
						xpdlNodeGraphicsInfo.setLaneId(map3.get(xpdlArtifact.getId()));
						}
					}
				}
			}
		}

	}


	private void fillXpdlArtifactsPositions(PluginContext context) {
		ProMJGraphPanel graphPanel = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnext);
		ProMGraphModel graphModel = graphPanel.getGraph().getModel();

		@SuppressWarnings("rawtypes")
		List proMGraphCellList = graphModel.getRoots();

		Map<String, XpdlArtifact> map = new HashMap<String, XpdlArtifact>();
		for (XpdlArtifact xpdlArtifact : xpdl.getArtifacts().getList()) {
			map.put(xpdlArtifact.getId(), xpdlArtifact);
		}


		for (Object object : proMGraphCellList) {
			if (object instanceof ProMGraphCell) {
				ProMGraphCell proMGraphCell = (ProMGraphCell) object;
				XpdlArtifact xpdlArtifact = map.get("" + proMGraphCell.hashCode());
				if (xpdlArtifact != null) {
					Rectangle2D rectangle = proMGraphCell.getView().getBounds();
					XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");

					XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
					nodeGraphicsInfo.setToolId("ProM");

					XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");

					nodeGraphicsInfo.setHeight("" + (int) rectangle.getHeight());
					nodeGraphicsInfo.setWidth("" + (int) rectangle.getWidth());

					coordinates.setxCoordinate("" + (int) rectangle.getX());
					coordinates.setyCoordinate("" + (int) rectangle.getY());
					nodeGraphicsInfo.setCoordinates(coordinates);

					nodeGraphicsInfos.add2List(nodeGraphicsInfo);
					xpdlArtifact.setNodeGraphicsInfos(nodeGraphicsInfos);

				}else {
					@SuppressWarnings("unchecked")
					List<Object> cells = proMGraphCell.getChildren();
					for (Object object2 : cells) {
						if (object2 instanceof ProMGraphCell) {
							ProMGraphCell proMGraphCell2 = (ProMGraphCell) object2;
							XpdlArtifact xpdlArtifact2 = map.get("" + proMGraphCell2.hashCode());
							if (xpdlArtifact2 != null) {
								Rectangle2D rectangle = proMGraphCell2.getView().getBounds();
								XpdlNodeGraphicsInfos nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");

								XpdlNodeGraphicsInfo nodeGraphicsInfo = new XpdlNodeGraphicsInfo("NodeGraphicsInfo");
								nodeGraphicsInfo.setToolId("ProM");

								XpdlCoordinates coordinates = new XpdlCoordinates("Coordinates");

								nodeGraphicsInfo.setHeight("" + (int) rectangle.getHeight());
								nodeGraphicsInfo.setWidth("" + (int) rectangle.getWidth());

								coordinates.setxCoordinate("" + (int) rectangle.getX());
								coordinates.setyCoordinate("" + (int) rectangle.getY());
								nodeGraphicsInfo.setCoordinates(coordinates);

								nodeGraphicsInfos.add2List(nodeGraphicsInfo);
								xpdlArtifact2.setNodeGraphicsInfos(nodeGraphicsInfos);
							}
						}
					}
				}
			}
		}

	}


	private void fillArtifacts() {
		for (Artifacts a : bpmnext.getArtifacts()){


			XpdlArtifact t =  new XpdlArtifact("Artifact");

			t.setArtifactType("Annotation");
			String label = a.getLabel().replaceAll("<html>", "").replaceAll("<br/>", " ").replaceAll("</html>", "");
			t.setTextAnnotation(label);
			t.setId("" + a.hashCode());
		

			XpdlArtifacts artiss = xpdl.getArtifacts();


			artiss.add2List(t);


		}


	}


	private void fillAssociations() {
		for (FlowAssociation flowa : bpmnext.getFlowAssociation()) {

			XpdlAssociation t = new XpdlAssociation("Association");
		
			XpdlAssociations associations = xpdl.getAssociations();

			t.setSource("" + flowa.getSource().hashCode());
			t.setTarget("" + flowa.getTarget().hashCode());
			t.setId("" + flowa.hashCode());

			associations.add2List(t);


		}

	}

}
