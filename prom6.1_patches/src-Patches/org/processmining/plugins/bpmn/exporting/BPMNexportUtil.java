package org.processmining.plugins.bpmn.exporting;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.SwingConstants;

import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.AttributeMap.SerializablePoint2D;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.NodeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExtFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExtImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.FlowAssociation;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;
import org.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.elements.ProMGraphPort;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.bpmn.TraslateBPMNResult;
import org.processmining.plugins.petrinet.replayfitness.conformance.ConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.performance.PerformanceData;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.collections.XpdlArtifacts;
import org.processmining.plugins.xpdl.collections.XpdlAssociations;
import org.processmining.plugins.xpdl.graphics.XpdlConnectorGraphicsInfo;
import org.processmining.plugins.xpdl.graphics.XpdlCoordinates;
import org.processmining.plugins.xpdl.graphics.XpdlNodeGraphicsInfo;
import org.processmining.plugins.xpdl.graphics.collections.XpdlConnectorGraphicsInfos;
import org.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import org.processmining.plugins.xpdl.idname.XpdlActivity;
import org.processmining.plugins.xpdl.idname.XpdlArtifact;
import org.processmining.plugins.xpdl.idname.XpdlAssociation;
import org.processmining.plugins.xpdl.idname.XpdlTransition;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class BPMNexportUtil {

	public BPMNexportUtil() {

	}

	public static BPMNDiagramExt exportPerformancetoBPMN(
			TraslateBPMNResult traslateBpmnresult,
			Map<Place, PerformanceData> Performanceresult,
			Map<Arc, Integer> maparc) {

		// clona bpmn
		BPMNDiagramExt bpmn =BPMNDiagramExtFactory.cloneBPMNDiagram((traslateBpmnresult.getBpmn()));

		Map<String, Place> MapArc2Place = traslateBpmnresult.getPlaceMap();

		Map<String, Integer> ArchiAttivatiBPMN = new HashMap<String, Integer>();
		Map<String, String> archibpmnwithsyncperformance = new HashMap<String, String>();

		Petrinet net = traslateBpmnresult.getPetri();

		// ogni piazza che attraversiamo in performance result conta i token
		// passati sulla pizza
		/*
		 * for(Place p : Performanceresult.keySet()){
		 * 
		 * if(MapArc2Place.containsKey(p.getLabel())){
		 * 
		 * PerformanceData rs= Performanceresult.get(p); int i =
		 * rs.getTokenCount(); ArchiAttivatiBPMN.put(p.getLabel(), i); } }
		 */

		for (Place p : MapArc2Place.values()) {

			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> edges = p
					.getGraph().getOutEdges(p);
			int count = 0;
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : edges) {
				Arc a = (Arc) edge;
				if (maparc.containsKey(a)) {
					Integer i = maparc.get(a);
					count += i;
				}
			}

			ArchiAttivatiBPMN.put(p.getLabel(), count);

		}

		Map<Activity, String> MapActivity = new HashMap<Activity, String>();

		for (Transition t : net.getTransitions()) {
			if (!t.isInvisible()) {
				String tname = t.getLabel();
				String name = (String) tname.subSequence(0, tname.indexOf("+"));
				Activity activity = null;
				// cerco l'attività bpmn a cui collegare l'artifacts
				for (Activity a : bpmn.getActivities()) {
					if (a.getLabel().equals(name)) {
						activity = a;
						break;
					}
				}
				Place preplace = (Place) t.getGraph().getInEdges(t).iterator()
						.next().getSource();
				// Place postplace = (Place)
				// t.getGraph().getOutEdges(t).iterator().next().getTarget();
				String text = "";
				PerformanceData ps = getPerfResult(preplace, Performanceresult);
				if (ps != null) {
					if (t.getLabel().endsWith("start")) {
						if (ps.getWaitTime() > 0) {
							text = "Activation Time: " + ps.getWaitTime()
									+ "<br/>";
						}
					} else if (t.getLabel().endsWith("complete")) {
						if (ps.getWaitTime() > 0) {
							text = "Execution Time: " + ps.getWaitTime()
									+ "<br/>";

						}
					}
					if (MapActivity.containsKey(activity)) {
						text += MapActivity.get(activity);
					}
					MapActivity.put(activity, text);
				}
			} else {
				if (t.getLabel().endsWith("_join")) {

					// controlla la presenza di sync time e inserisce il souj
					// per ogni ramo parallelo
					addsoujandsynctime(Performanceresult, t,
							archibpmnwithsyncperformance);
				}
			}

		}

		for (Activity a : MapActivity.keySet()) {
			String text = MapActivity.get(a);
			String label = "<html>" + text + "</html>";
			ContainingDirectedGraphNode parent = a.getParent();
			Artifacts art = null;
			if (parent instanceof Swimlane) {
				art = bpmn.addArtifacts(label, ArtifactType.TEXTANNOATION,
						a.getParentSwimlane());
				bpmn.addFlowAssociation(art, a,a.getParentSwimlane());
			}
			if (parent instanceof SubProcess) {
				art = bpmn.addArtifacts(label, ArtifactType.TEXTANNOATION,
						a.getParentSubProcess());
				bpmn.addFlowAssociation(art, a,a.getParentSubProcess());
			}
			if (parent == null) {
				art = bpmn.addArtifacts(label, ArtifactType.TEXTANNOATION);
				bpmn.addFlowAssociation(art, a);
			}

			
		}

		// i sync time sono sempre sulle piazze "arco", quindi cerco l'arco a
		// cui si riferisco i place con sync time ed aggiungo
		// il tooltip all'arco e lo coloro di rosso.
		for (Flow f : bpmn.getFlows()) {
			String from = f.getSource().getLabel();
			String to = f.getTarget().getLabel();
			if (archibpmnwithsyncperformance.containsKey(from + to)) {
				String flowsync = archibpmnwithsyncperformance.get(from + to);
				f.getAttributeMap().remove(AttributeMap.TOOLTIP);

				f.getAttributeMap().put(AttributeMap.TOOLTIP, flowsync);
				f.getAttributeMap().remove(AttributeMap.SHOWLABEL);
				f.getAttributeMap().put(AttributeMap.SHOWLABEL, false);
				f.getAttributeMap().put(AttributeMap.EDGECOLOR, Color.RED);

			}
			if (ArchiAttivatiBPMN.containsKey(from + to)) {
				Integer i = ArchiAttivatiBPMN.get(from + to);
				f.getAttributeMap().remove(AttributeMap.SHOWLABEL);
				f.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				f.getAttributeMap().put(AttributeMap.LABEL, i.toString());

			}
		}

		return bpmn;
	}

	private static void addsoujandsynctime(
			Map<Place, PerformanceData> performanceresult, Transition t,
			Map<String, String> archibpmnwithsyncperformance) {

		Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inflows = t
				.getGraph().getInEdges(t);
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inflows) {
			Place source = (Place) edge.getSource();
			PerformanceData rs = performanceresult.get(source);
			if (rs.getSynchTime() > 0) {
				String sourjtime = calcolasojourntime(source, performanceresult);
				archibpmnwithsyncperformance.put(source.getLabel(),
						"Sync time: " + String.valueOf(rs.getSynchTime())
								+ "\n Souj time: " + sourjtime);
			} else {
				if (rs.getTime() >= 0) {
					String sourjtime = calcolasojourntime(source,
							performanceresult);
					archibpmnwithsyncperformance.put(source.getLabel(),
							"Souj time: " + sourjtime);
				}

			}

		}

	}

	private static String calcolasojourntime(PetrinetNode p,
			Map<Place, PerformanceData> performanceresult) {

		myFloat soujour = new myFloat();

		recursiveaddsoujourtime(soujour, performanceresult, p, 0);

		return String.valueOf(soujour.getFlo());
	}

	private static void recursiveaddsoujourtime(myFloat soujour,
			Map<Place, PerformanceData> performanceresult, PetrinetNode p, int i) {

		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> flow : p
				.getGraph().getInEdges(p)) {
			PetrinetNode sourcenode = flow.getSource();
			if (sourcenode instanceof Transition) {
				Transition source = (Transition) flow.getSource();
				if (source.getLabel().endsWith("_join")) {
					// prendo solo un ramo ora devo non mi devo fermare al primo
					// fork che incontro ma al successivo
					PetrinetNode newsource = source.getGraph()
							.getInEdges(source).iterator().next().getSource();
					recursiveaddsoujourtime(soujour, performanceresult,
							newsource, i++);
				} else {
					if (!source.getLabel().endsWith("_fork")) {
						recursiveaddsoujourtime(soujour, performanceresult,
								source, i);
					} else {
						// se i è maggiore di 0 significa che sto calcolando un
						// tempo di soggiorno del branch che contiente almeno
						// un altro branch parallelo nel suo interno manca
						// ancora per i cicli
						if (i > 0) {
							recursiveaddsoujourtime(soujour, performanceresult,
									source, i--);
						}

					}

				}
			} else if (sourcenode instanceof Place) {
				Place source = (Place) flow.getSource();
				PerformanceData ps = performanceresult.get(source);
				if (ps != null) {
					if (ps.getTime() > 0) {
						soujour.add(ps.getTime());
					}
					recursiveaddsoujourtime(soujour, performanceresult, source,
							i);
				}

			}

		}

	}

	private static PerformanceData getPerfResult(Place preplace,
			Map<Place, PerformanceData> performanceresult) {
		for (Place p : performanceresult.keySet()) {
			if (p.getLabel().equals(preplace.getLabel())) {
				return performanceresult.get(p);
			}
		}

		return null;
	}

	public static BPMNDiagramExt exportConformancetoBPMN(
			TraslateBPMNResult traslateBpmnresult,
			ConformanceResult conformanceresult) {

		// clona bpmn
		BPMNDiagramExt bpmn =BPMNDiagramExtFactory.cloneBPMNDiagram(traslateBpmnresult.getBpmn());

		Map<String, Place> MapArc2Place = traslateBpmnresult.getPlaceMap();

		Marking remaning = conformanceresult.getRemainingMarking();
		Marking missing = conformanceresult.getMissingMarking();
		Map<Transition, Integer> transnotfit = conformanceresult
				.getMapTransition();
		Map<Arc, Integer> attivazionearchi = conformanceresult.getMapArc();

		Map<String, Integer> ArchiAttivatiBPMN = new HashMap<String, Integer>();
		Map<String, String> archibpmnwitherrorconformance = new HashMap<String, String>();

		// gli archi che attivo sul bpmn sono gli archi uscenti delle piazze
		// "arco"
		for (Place p : MapArc2Place.values()) {
			int att = 0;
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> egde : p
					.getGraph().getOutEdges(p)) {
				if (attivazionearchi.containsKey(egde)) {
					att += attivazionearchi.get(egde);

				}

			}
			ArchiAttivatiBPMN.put(p.getLabel(), att);

		}
		Petrinet net = traslateBpmnresult.getPetri();
		Map<Activity,Artifacts> mapActiArtic = new HashMap<Activity, Artifacts>();
		// transizioni che nn fittano
		String ret = "<br/>";
		for (Transition t : net.getTransitions()) {
			if (!t.isInvisible()) {
				String tname = t.getLabel();
				String name = (String) tname.subSequence(0, tname.indexOf("+"));
				
				Activity activity = null;
				// cerco l'attività bpmn a cui collegare l'artifacts
				for (Activity a : bpmn.getActivities()) {
					if (a.getLabel().equals(name)) {
						activity = a;
						break;
					}
				}
				String unsoundallert = "";
				for (Place p : remaning.baseSet()) {
					if (p.getLabel().equals(name)) {
						unsoundallert += ret + " Task missing competition\n";
					} else if (p.getLabel().startsWith(name) && !tname.endsWith("start") ) {
						unsoundallert += ret + " Task interrupted executions\n";
					}
				}
				for (Place p : missing.baseSet()) {
					if (p.getLabel().equals(name)) {
						unsoundallert += ret + " Task internal failures";
					}
					if(p.getLabel().endsWith(name)&& tname.endsWith("start")){
						unsoundallert += ret + " Task unsound executions\n";
					}
				}
				if (activity != null && unsoundallert!="") {
					
					
						String label = "<html>"+ unsoundallert + "<html>";
					if(!mapActiArtic.containsKey(activity)){

						
						Artifacts art = null;
						if (activity.getParent() == null) {
							art = bpmn.addArtifacts(label,
									ArtifactType.TEXTANNOATION);
							bpmn.addFlowAssociation(art, activity);

						} else {
							if (activity.getParent() instanceof SubProcess) {
								art = bpmn.addArtifacts(label,
										ArtifactType.TEXTANNOATION,
										activity.getParentSubProcess());
								bpmn.addFlowAssociation(art, activity,activity.getParentSubProcess());	
							} else {
								if (activity.getParent() instanceof Swimlane) {
									art = bpmn.addArtifacts(label,
											ArtifactType.TEXTANNOATION,
											activity.getParentSwimlane());
									bpmn.addFlowAssociation(art, activity,activity.getParentSwimlane());
								}
							}
						}

						mapActiArtic.put(activity, art);
					}else{
						Artifacts art = mapActiArtic.get(activity);
						label+=art.getLabel();
						art.getAttributeMap().remove(AttributeMap.LABEL);
						art.getAttributeMap().put(AttributeMap.LABEL, label);
						

					}

				}

			}

		
			// cerco la transizione del fork
			if (t.getLabel().endsWith("_fork")) {
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> p = t
				.getGraph().getOutEdges(t);
				Vector<String> targ = new Vector<String>();
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : p) {
					Place target = (Place) e.getTarget();
					targ.add(target.getLabel());
				}
				for (String placename : targ) {
					for (Place place : remaning.baseSet()) {
						if (place.getLabel().equals(placename)) {
							System.out.println(ret + " Fork internal failures");
							archibpmnwitherrorconformance.put(place.getLabel(),
							" Fork internal failures");
						}

					}
				}
			}
		}
		
		
		// metto gli attraversamenti sugli archi bpmn
		for (Flow f : bpmn.getFlows()) {
			String from = f.getSource().getLabel();
			String to = f.getTarget().getLabel();
			if (ArchiAttivatiBPMN.containsKey(from + to)) {
				Integer i = ArchiAttivatiBPMN.get(from + to);
				if (i > 0) {
					f.getAttributeMap().put(AttributeMap.LABEL, i.toString());
					f.getAttributeMap().put(AttributeMap.TOOLTIP, i.toString());
					f.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				}

			}
			// metto eventuali errore sul arco di fork
			if (archibpmnwitherrorconformance.containsKey(from + to)) {

				String flowerr = archibpmnwitherrorconformance.get(from + to);
				f.getAttributeMap().remove(AttributeMap.TOOLTIP);

				f.getAttributeMap().put(AttributeMap.TOOLTIP, flowerr);
				f.getAttributeMap().remove(AttributeMap.SHOWLABEL);
				f.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				f.getAttributeMap().put(AttributeMap.EDGECOLOR, Color.RED);

			}
		}

		return bpmn;

	}

/*	public static Xpdl exportToXpdlold(PluginContext c, Xpdl xpdl,
			TraslateBPMNResult traslateBpmnresult, BPMNDiagramExt newbpmn)
			throws Exception {

		Map<String, BPMNNode> id2node = traslateBpmnresult.getid2nodeMap();
		Map<String, String> node2id = traslateBpmnresult.getNode2id();

		// clono xpdl
		String export = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
				+ xpdl.exportElement();
		InputStream input = new ByteArrayInputStream(export.getBytes());
		Xpdl newxpdl = importXpdlFromStream(input, xpdl.getName() + "_export");
		Map<String, String> archiAttivatiBPMN = new HashMap<String, String>();

		String Pageid = newxpdl.getPages().getList().get(0).getId();
		String laneid = newxpdl.getPools().getList().get(0).getLanes().getList().get(0).getId();

		// creo la layout del bpmn
		GraphLayoutConnection layout = layoutcreate(c, newbpmn);

		for (Flow f : newbpmn.getFlows()) {
			String i = f.getLabel();
			String name = f.getSource().getLabel() + f.getTarget().getLabel();
			archiAttivatiBPMN.put(name, i);
			// f.getAttributeMap().put(AttributeMap.EDGECOLOR, Color.RED);
			if (f.getAttributeMap().containsKey(AttributeMap.EDGECOLOR)) {
				if (f.getAttributeMap().get(AttributeMap.EDGECOLOR)
						.equals(Color.RED)) {
					String label = f.getAttributeMap()
							.get(AttributeMap.TOOLTIP).toString()
							.replaceAll("<html>", "").replaceAll("</html>", "")
							.replaceAll("<br/>", " ");

					NodeID id = new NodeID();
					String targetassid = id.toString().replaceAll("node ", "");

					BPMNNode source = f.getSource();
					BPMNNode target = f.getTarget();
					String sourceid = node2id.get(source.getLabel());
					String targetid = node2id.get(target.getLabel());

					for (XpdlTransition xpdlt : newxpdl.getWorkflowProcesses()
							.getList().get(0).getTransitions().getList()) {
						if (xpdlt.getFrom().toString().equals(sourceid)
								&& xpdlt.getTo().toString().equals(targetid)) {
							String sourceassoid = xpdlt.getId();
							 Pageid = xpdlt.getConnectorGraphicsInfos().getList().get(0).getPageId();
							
							// cambiare e mettere primo ed ultimo
							String x1 = xpdlt.getConnectorGraphicsInfos()
									.getList().get(0).getCoordinatesList()
									.get(0).getxCoordinate();
							String y1 = xpdlt.getConnectorGraphicsInfos()
									.getList().get(0).getCoordinatesList()
									.get(0).getyCoordinate();

							String x2 = xpdlt.getConnectorGraphicsInfos()
									.getList().get(0).getCoordinatesList()
									.get(1).getxCoordinate();
							String y2 = xpdlt.getConnectorGraphicsInfos()
									.getList().get(0).getCoordinatesList()
									.get(1).getyCoordinate();

							double xx1 = Double.parseDouble(x1);
							double yy1 = Double.parseDouble(y1);

							double xx2 = Double.parseDouble(x2);
							double yy2 = Double.parseDouble(y2);

							Point2D xytarget = new Point2D.Double(
									(xx1 + xx2) / 2, (yy1 + yy2) / 2);

							Point2D xysource = new Point2D.Double(
									(xx1 + xx2) / 2, (yy1 + yy2) / 2);

							XpdlAssociations setass = null;
							if (newxpdl.getAssociations() == null) {
								setass = new XpdlAssociations("Associations");
							} else {
								setass = newxpdl.getAssociations();
							}

							XpdlAssociation newassociation = new XpdlAssociation(
									"Association");
							setAssociation(newassociation, sourceassoid,
									targetassid, Pageid, xysource, xytarget);

							setass.getList().add(newassociation);

							newxpdl.setAssociations(setass);

							XpdlArtifact newartifact = new XpdlArtifact(
									"Artifact");
							setArtifact(newartifact, Pageid,String.valueOf(xysource.getX()),String.valueOf(xysource.getY()),"Annotation", label,laneid);
							newartifact.setId(targetassid);
							
							
							if (newxpdl.getArtifacts() == null) {
								XpdlArtifacts xas = new XpdlArtifacts(
										"Artifacts");
								xas.getList().add(newartifact);
								newxpdl.setArtifacts(xas);
							} else {
								newxpdl.getArtifacts().getList()
										.add(newartifact);
							}
						}
					}

				}
			}

		}

		// esportazione in xpdl
		// archi visitati
		for (XpdlTransition tr : newxpdl.getWorkflowProcesses().getList()
				.get(0).getTransitions().getList()) {

			String from = id2node.get(tr.getFrom()).getLabel();
			String to = id2node.get(tr.getTo()).getLabel();

			String i = (archiAttivatiBPMN.containsKey(from + to)) ? archiAttivatiBPMN
					.get(from + to) : "0";
			if (i.equals("no label"))
				i = "0";
			tr.setName(i.toString());

		}
		// aggiunta degli artifact
		for (Artifacts a : newbpmn.getArtifacts()) {

			if (a.getArtifactType().equals(ArtifactType.TEXTANNOATION)) {
				Point2D xy = layout.getPosition(a);
				String label = a.getLabel().replaceAll("<html>", "")
						.replaceAll("<br/>", " ").replaceAll("</html>", "");
				XpdlArtifact newartifact = new XpdlArtifact("Artifact");// ,Pageid,String.valueOf(xy.getX()),String.valueOf(xy.getY()),"Annotation",label);
				setArtifact(newartifact, Pageid, String.valueOf(xy.getX()),
						String.valueOf(xy.getY()), "Annotation", label,laneid);
				newartifact.setId(a.getId().toString().replaceAll("node ", ""));

				if (newxpdl.getArtifacts() == null) {
					XpdlArtifacts xas = new XpdlArtifacts("Artifacts");
					xas.getList().add(newartifact);
					newxpdl.setArtifacts(xas);
				} else {
					newxpdl.getArtifacts().getList().add(newartifact);
				}
			}

		}

		XpdlAssociations setass = null;
		if (newxpdl.getAssociations() == null) {
			setass = new XpdlAssociations("Associations");
		} else {
			setass = newxpdl.getAssociations();
		}

		for (FlowAssociation f : newbpmn.getFlowAssociation()) {
			BPMNNode source = f.getSource();
			BPMNNode target = f.getTarget();
			String sourceid = source.getId().toString().replaceAll("node ", "");
			String targetid = node2id.get(target.getLabel());
			Point2D xysource = layout.getPosition(source);

			Point2D xytarget = null;
			for (XpdlActivity activity : newxpdl.getWorkflowProcesses()
					.getList().get(0).getActivities().getList()) {

				if (activity.getId().equals(targetid)) {
					String x = activity.getNodeGraphicsInfos().getList().get(0)
							.getCoordinates().getxCoordinate();

					String y = activity.getNodeGraphicsInfos().getList().get(0)
							.getCoordinates().getyCoordinate();
					double xx = Double.parseDouble(x);
					double yy = Double.parseDouble(y);
					xytarget = new Point2D.Double(xx, yy);
					break;
				}

			}
			XpdlAssociation newassociation = new XpdlAssociation("Association");
			newassociation.setSource(sourceid);
			newassociation.setTarget(targetid);
			XpdlConnectorGraphicsInfos xcgis = new XpdlConnectorGraphicsInfos(
					"ConnectorGraphicsInfos");
			XpdlConnectorGraphicsInfo xpdlginfo = new XpdlConnectorGraphicsInfo(
					"ConnectorGraphicsInfo");
			xpdlginfo.setPageId(Pageid);
			xpdlginfo.setIsVisible("true");
			xpdlginfo.setToolId("ProM");
			XpdlCoordinates xpdlcsx = new XpdlCoordinates("Coordinates");
			xpdlcsx.setxCoordinate(String.valueOf(xysource.getX()));
			xpdlcsx.setyCoordinate(String.valueOf(xysource.getY()));

			XpdlCoordinates xpdlcsy = new XpdlCoordinates("Coordinates");
			xpdlcsy.setxCoordinate(String.valueOf(xytarget.getX()));
			xpdlcsy.setyCoordinate(String.valueOf(xytarget.getY()));

			xpdlginfo.getCoordinatesList().add(xpdlcsx);
			xpdlginfo.getCoordinatesList().add(xpdlcsy);
			xcgis.add2List(xpdlginfo);

			setass.getList().add(newassociation);
		}
		newxpdl.setAssociations(setass);

		
		 * for(Transition t : transnotfit.keySet()){ if(!t.isInvisible()){
		 * String tname = t.getLabel(); if(tname.endsWith("start")){ String name
		 * = (String) tname.subSequence(0, tname.indexOf("+"));
		 * 
		 * for(Activity a : bpmn.getActivities()){
		 * if(a.getLabel().equals(name)){ String id = xp.getNode2id().get(a);
		 * System.out.println("Settings: " + id); for(XpdlActivity activity :
		 * xpdl
		 * .getWorkflowProcesses().getList().get(0).getActivities().getList()){
		 * 
		 * }
		 * 
		 * } } }else if(tname.endsWith("complete")){
		 * 
		 * 
		 * } }
		 * 
		 * }
		 
		// File file = null;
		// String text = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
		// xpdl.exportElement();

		// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new
		// FileOutputStream(file)));
		// bw.write(text);
		// bw.close();

		return newxpdl;

	}

	private static void setArtifact(XpdlArtifact newartifact, String pageid,
			String x, String y, String type, String label, String laneid) {

		newartifact.setArtifactType(type);
		newartifact.setTextAnnotation(label);
		XpdlNodeGraphicsInfos xpdlngi = new XpdlNodeGraphicsInfos(
				"NodeGraphicsInfos");

		XpdlNodeGraphicsInfo xpdlng = new XpdlNodeGraphicsInfo(
				"NodeGraphicsInfo");
		
		xpdlng.setPageId(pageid);
		
		xpdlng.setLaneId(laneid);
		
		XpdlCoordinates xpdlcsx = new XpdlCoordinates("Coordinates");
		xpdlcsx.setxCoordinate(x);
		xpdlcsx.setyCoordinate(y);

		xpdlng.setCoordinates(xpdlcsx);

		xpdlngi.add2List(xpdlng);
		newartifact.setNodeGraphicsInfos(xpdlngi);
	}

	private static void setAssociation(XpdlAssociation newassociation,
			String sourceid, String targetid, String pageid, Point2D xysource,
			Point2D xytarget) {

		newassociation.setSource(sourceid);
		newassociation.setTarget(targetid);
		XpdlConnectorGraphicsInfos xcgis = new XpdlConnectorGraphicsInfos(
				"ConnectorGraphicsInfos");
		XpdlConnectorGraphicsInfo xpdlginfo = new XpdlConnectorGraphicsInfo(
				"ConnectorGraphicsInfo");
		xpdlginfo.setPageId(pageid);
		xpdlginfo.setIsVisible("true");
		xpdlginfo.setToolId("ProM");
		XpdlCoordinates xpdlcsx = new XpdlCoordinates("Coordinates");
		xpdlcsx.setxCoordinate(String.valueOf(xysource.getX()));
		xpdlcsx.setyCoordinate(String.valueOf(xysource.getY()));

		XpdlCoordinates xpdlcsy = new XpdlCoordinates("Coordinates");
		xpdlcsy.setxCoordinate(String.valueOf(xytarget.getX()));
		xpdlcsy.setyCoordinate(String.valueOf(xytarget.getY()));

		xpdlginfo.getCoordinatesList().add(xpdlcsx);
		xpdlginfo.getCoordinatesList().add(xpdlcsy);
		xcgis.add2List(xpdlginfo);
		newassociation.setConnectorGraphicsInfos(xcgis);

	}

	private static Xpdl importXpdlFromStream(InputStream input, String filename)
			throws Exception {
		
		 // Get an XML pull parser.
		 
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		
		 //* Initialize the parser on the provided input.
	
		xpp.setInput(input, null);
		
		// * Get the first event type.
		 
		int eventType = xpp.getEventType();
		
		// * Create a fresh XPDL object.
		
		Xpdl xpdl = new Xpdl();

	
		// * Skip whatever we find until we've found a start tag.
		 
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		
		// * Check whether start tag corresponds to XPDL start tag.
		 
		if (xpp.getName().equals(xpdl.tag)) {
			
			// * Yes it does. Import the XPDL element.
			
			xpdl.importElement(xpp, xpdl);
		} else {
			
			// * No it does not. Return null to signal failure.
			
			xpdl.log(xpdl.tag, xpp.getLineNumber(), "Expected " + xpdl.tag
					+ ", got " + xpp.getName());
		}
		if (xpdl.hasErrors()) {
			// context.getProvidedObjectManager().createProvidedObject(
			// "Log of XPDL import", xpdl.getLog(), XLog.class, context);
			return null;
		}
		return xpdl;
	}

	private static GraphLayoutConnection layoutcreate(PluginContext c,
			BPMNDiagramExt net) {

		GraphLayoutConnection layout = new GraphLayoutConnection(net);
		try {
			layout = c.getConnectionManager().getFirstConnection(
					GraphLayoutConnection.class, c, net);
			return layout;
		} catch (ConnectionCannotBeObtained e) {
			// TODO Auto-generated catch block
			
			// * Get a jgraph for this graph.
			 

			ProMJGraph jgraph = ProMJGraphVisualizer.instance()
					.visualizeGraph(c, net).getGraph();
			
			// * Layout this jgraph.
			
			JGraphFacade facade = new JGraphFacade(jgraph);
			//layOutFMJGraph(net, jgraph, facade);
			System.out.print("Creata Layout del BPMN diagram");
		}
		try {
			layout = c.getConnectionManager().getFirstConnection(
					GraphLayoutConnection.class, c, net);
			return layout;
		} catch (ConnectionCannotBeObtained e) {
			return null;
		}

	}

	private static void layOutFMJGraph(
			DirectedGraph<? extends AbstractDirectedGraphNode, ? extends AbstractDirectedGraphEdge<?, ?>> graph,
			ProMJGraph jgraph, JGraphFacade facade) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(20);
		layout.setOrientation(graph.getAttributeMap().get(
				AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

		facade.setOrdered(true);
		facade.setEdgePromotion(true);
		facade.setIgnoresCellsInGroups(false);
		facade.setIgnoresHiddenCells(false);
		facade.setIgnoresUnconnectedCells(false);
		facade.setDirected(false);
		facade.resetControlPoints();

		facade.run(layout, false);

		fixParallelTransitions(facade, 15);

		Map<?, ?> nested = facade.createNestedMap(true, false);

		jgraph.getGraphLayoutCache().edit(nested);
		jgraph.setUpdateLayout(layout);

	}

	private static void fixParallelTransitions(JGraphFacade facade,
			double spacing) {
		ArrayList<Object> edges = getEdges(facade);
		for (Object edge : edges) {
			List<Object> points = getPoints(facade, edge);
			if (points.size() != 2) {
				continue;
			}
			Object sourceCell = facade.getSource(edge);
			Object targetCell = facade.getTarget(edge);
			Object sourcePort = facade.getSourcePort(edge);
			Object targetPort = facade.getTargetPort(edge);
			Object[] between = facade.getEdgesBetween(sourcePort, targetPort,
					false);
			if ((between.length == 1) && !(sourcePort == targetPort)) {
				continue;
			}
			Rectangle2D sCP = facade.getBounds(sourceCell);
			Rectangle2D tCP = facade.getBounds(targetCell);
			Point2D sPP = GraphConstants.getOffset(((ProMGraphPort) sourcePort)
					.getAttributes());

			if (sPP == null) {
				sPP = new Point2D.Double(sCP.getCenterX(), sCP.getCenterY());
			}
			Point2D tPP = GraphConstants.getOffset(((ProMGraphPort) targetPort)
					.getAttributes());
			// facade.getBounds(sourcePort);

			if (tPP == null) {
				tPP = new Point2D.Double(tCP.getCenterX(), tCP.getCenterY());
			}

			if (sourcePort == targetPort) {
				assert (sPP.equals(tPP));
				double x = sPP.getX();
				double y = sPP.getY();
				for (int i = 2; i < between.length + 2; i++) {
					List<Point2D> newPoints = new ArrayList<Point2D>(5);
					newPoints.add(new Point2D.Double(x
							- (spacing + i * spacing), y));
					newPoints.add(new Point2D.Double(x
							- (spacing + i * spacing), y
							- (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x, y
							- (2 * spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x
							+ (spacing + i * spacing), y
							- (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x + (spacing), y
							- (spacing / 2 + i * spacing)));
					facade.setPoints(between[i - 2], newPoints);
				}

				continue;
			}

			double dx = (sPP.getX()) - (tPP.getX());
			double dy = (sPP.getY()) - (tPP.getY());
			double mx = (tPP.getX()) + dx / 2.0;
			double my = (tPP.getY()) + dy / 2.0;
			double slope = Math.sqrt(dx * dx + dy * dy);
			for (int i = 0; i < between.length; i++) {
				List<Point2D> newPoints = new ArrayList<Point2D>(3);
				double pos = 2 * i - (between.length - 1);
				if (facade.getSourcePort(between[i]) == sourcePort) {
					newPoints.add(sPP);
					newPoints.add(tPP);
				} else {
					newPoints.add(tPP);
					newPoints.add(sPP);
				}
				if (pos != 0) {
					pos = pos / 2;
					double x = mx + pos * spacing * dy / slope;
					double y = my - pos * spacing * dx / slope;
					newPoints.add(1, new SerializablePoint2D.Double(x, y));
				}
				facade.setPoints(between[i], newPoints);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static List<Object> getPoints(JGraphFacade facade, Object edge) {
		return facade.getPoints(edge);
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Object> getEdges(JGraphFacade facade) {
		return new ArrayList<Object>(facade.getEdges());
	}
 */
}

class myFloat {

	float flo = 0;

	public float getFlo() {
		return flo;
	}

	public void setFlo(float flo) {
		this.flo = flo;
	}

	public void add(float a) {
		flo += a;
	}

	myFloat() {
		flo = 0;
	}

}
