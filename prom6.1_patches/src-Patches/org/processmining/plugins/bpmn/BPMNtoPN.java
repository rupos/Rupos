package org.processmining.plugins.bpmn;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.NodeID;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.xpdl.Xpdl;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

@Plugin(name = "Import BPMN model from XPDL 2.1 file to PetriNet", parameterLabels = { "Filename" }, returnLabels = {
		"Petri Net", "Marking",  "BPMNDiagram" }, returnTypes = { Petrinet.class, Marking.class, BPMNDiagram.class })
		@UIImportPlugin(description = "XPDL 2.1 files to PN", extensions = { "xpdl" })
		public class BPMNtoPN extends AbstractImportPlugin {

	private ExpandableSubNet subNet = null;

	protected FileFilter getFileFilter() {
		return new FileNameExtensionFilter("XPDL 2.1 files", "xpdl");
	}

	protected Object importFromStream(PluginContext context, InputStream input,
			String filename, long fileSizeInBytes) throws Exception {
		Xpdl xpdl = importXpdlFromStream(context, input, filename,
				fileSizeInBytes);
		if (xpdl == null) {
			/*
			 * No PNML found in file. Fail.
			 */
			return null;
		}
		/*
		 * XPDL file has been imported. Now we need to convert the contents to a
		 * BPMN diagram.
		 */
		BPMNDiagram bpmn = BPMNDiagramFactory.newBPMNDiagram(filename);
		Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();

		/*
		 * Initialize the BPMN diagram from the XPDL element.
		 */
		xpdl.convertToBpmn(bpmn, id2node);

		/*
		 * Set the label of the BPMN diagram.
		 */
		context.getFutureResult(0).setLabel(filename);

		Boolean well = this.isWellFormed(bpmn);

		Object result = this.BPMN2Translate(bpmn);

		return result;
	}

	private void translateTask(BPMNDiagram bpmn, Map<String, Place> placeMap,
			PetrinetGraph net) {

		for (Activity c : bpmn.getActivities()) {
			String id = c.getLabel();

			Transition t = net.addTransition(id + "_start", this.subNet);
			Place p = net.addPlace(id, this.subNet);
			Arc a = net.addArc(t, p, 1, this.subNet);
			Transition t1 = net.addTransition(id + "_complete", this.subNet);
			Arc a1 = net.addArc(p, t1, 1, this.subNet);

			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
					.getGraph().getInEdges(c)) {
				String source = s.getSource().getLabel();
				String target = s.getTarget().getLabel();

				Place pst = placeMap.get(source + target);

				Arc st = net.addArc(pst, t, 1, this.subNet);

			}
			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : c
					.getGraph().getOutEdges(c)) {
				String source = s.getSource().getLabel();
				String target = s.getTarget().getLabel();

				Place pst = placeMap.get(source + target);

				Arc st = net.addArc(t1, pst, 1, this.subNet);

			}

		}

	}

	private void translateGateway(BPMNDiagram bpmn,	Map<String, Place> placeMap, PetrinetGraph net) {
		for (Gateway g : bpmn.getGateways()) {
			//gateway databased
			if (g.getGatewayType().equals(GatewayType.DATABASED)) {
				int i = 0;
				Map<String, Transition> tranMap = new HashMap<String, Transition>();
				//gateway data-based if branch 
				if (g.getGraph().getOutEdges(g).size()>1 && g.getGraph().getInEdges(g).size()==1 ){
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g.getGraph().getOutEdges(g)) {
						String source = s.getSource().getLabel();
						String target = s.getTarget().getLabel();

						Transition t = net.addTransition(g.getLabel() + "_" + i++,
								this.subNet);
						t.setInvisible(true);
						tranMap.put(target + source, t);

						Place pst = placeMap.get(source + target);

						net.addArc(t, pst, 1, this.subNet);

					}
					for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g.getGraph().getInEdges(g)) {
						String source = s.getSource().getLabel();
						String target = s.getTarget().getLabel();

						Place pst = placeMap.get(source + target);

						for (Transition t : tranMap.values()) {

							net.addArc(pst, t, 1, this.subNet);

						}
					}
				}else{
					//gateway merge
					if (g.getGraph().getOutEdges(g).size()==1 && g.getGraph().getInEdges(g).size()>1 ){
						String out = g.getGraph().getOutEdges(g).iterator().next().getSource().getLabel();
						String in  = g.getGraph().getOutEdges(g).iterator().next().getTarget().getLabel();
						Place ps = placeMap.get(out + in);
						i=0;
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g.getGraph().getInEdges(g)){
							String source = s.getSource().getLabel();
							String target = s.getTarget().getLabel();


							Place pst = placeMap.get(source + target);

							Transition t = net.addTransition(g.getLabel() + "_" + i++,this.subNet );
							t.setInvisible(true);
							net.addArc( pst,t, 1, this.subNet);

							net.addArc(t, ps, this.subNet);

						}
					}
				}

			}else{
				if (g.getGatewayType().equals(GatewayType.PARALLEL)) {
					//gateway parallel fork 
					if (g.getGraph().getOutEdges(g).size()>1 && g.getGraph().getInEdges(g).size()==1 ){
						String so = g.getGraph().getInEdges(g).iterator().next().getSource().getLabel();
						String ta  = g.getGraph().getInEdges(g).iterator().next().getTarget().getLabel();
						Place ps = placeMap.get(so+ta);
						Transition t = net.addTransition(g.getLabel() + "_fork",this.subNet );
						t.setInvisible(true);
						net.addArc( ps,t, 1, this.subNet);
						for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g.getGraph().getOutEdges(g)){
							String source = s.getSource().getLabel();
							String target = s.getTarget().getLabel();


							Place pst = placeMap.get(source + target);
							net.addArc(t, pst, 1, this.subNet);

						}


					}else{
						//gateway parallel Join 
						if (g.getGraph().getOutEdges(g).size()==1 && g.getGraph().getInEdges(g).size()>1 ){
							String so = g.getGraph().getOutEdges(g).iterator().next().getSource().getLabel();
							String ta  = g.getGraph().getOutEdges(g).iterator().next().getTarget().getLabel();
							Place ps = placeMap.get(so+ta);
							Transition t = net.addTransition(g.getLabel() + "_join",this.subNet );
							t.setInvisible(true);
							net.addArc( t,ps, 1, this.subNet);
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g.getGraph().getInEdges(g)){
								String source = s.getSource().getLabel();
								String target = s.getTarget().getLabel();


								Place pst = placeMap.get(source + target);
								net.addArc(pst,t, 1, this.subNet);

							}


						}
					}
				}else{
					//gateway event-based
					if (g.getGatewayType().equals(GatewayType.EVENTBASED)) {
						//Exclusive event gateway 
						if (g.getGraph().getOutEdges(g).size()>1 && g.getGraph().getInEdges(g).size()==1 ){
							String so = g.getGraph().getInEdges(g).iterator().next().getSource().getLabel();
							String ta  = g.getGraph().getInEdges(g).iterator().next().getTarget().getLabel();
							Place ps = placeMap.get(so+ta);
							int i=0;
							for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : g.getGraph().getOutEdges(g)){
								String source = s.getSource().getLabel();
								String target = s.getTarget().getLabel();


								Place pst = placeMap.get(source + target);

								Transition t = net.addTransition(g.getLabel() + "_" + i++,this.subNet );
								t.setInvisible(true);
								net.addArc( t,pst, 1, this.subNet);

								net.addArc( ps,t , this.subNet);

							}


						}


					}
				}
			}
		}
	}

	private void translateEvent(BPMNDiagram bpmn,	Map<String, Place> placeMap, PetrinetGraph net, Marking marking){
		for (Event e : bpmn.getEvents()) {
			if (e.getEventType().equals(EventType.START) && e.getEventTrigger().equals(EventTrigger.NONE)) {

				// Place p = new Place(e.getLabel(), net);
				Place p = net.addPlace(e.getLabel(), this.subNet);

				Transition t = net.addTransition(e.getLabel(), this.subNet);
				t.setInvisible(true);
				Arc a = net.addArc(p, t, 1, this.subNet);
				marking.add(p, 1);

				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : e.getGraph().getOutEdges(e)) {
					String source = s.getSource().getLabel();
					String target = s.getTarget().getLabel();


					Place pst = placeMap.get(source + target);

					net.addArc(t, pst, 1, this.subNet);

				}


			}
			if (e.getEventType().equals(EventType.END) && e.getEventTrigger().equals(EventTrigger.NONE)) {


				Place p = net.addPlace(e.getLabel(), this.subNet);

				Transition t = net.addTransition(e.getLabel(), this.subNet);

				t.setInvisible(true);
				Arc a = net.addArc(t, p, 1, this.subNet);

				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> s : e.getGraph().getInEdges(e)) {
					String source = s.getSource().getLabel();
					String target = s.getTarget().getLabel();


					Place pst = placeMap.get(source + target);

					net.addArc( pst, t, 1, this.subNet);

				}

			}
			if (e.getEventType().equals(EventType.INTERMEDIATE) && !e.getEventTrigger().equals(EventTrigger.NONE)) {
				
				

				Transition t = net.addTransition(e.getLabel(), this.subNet);			
							

				
				String g = e.getGraph().getInEdges(e).iterator().next().getSource().getLabel();
				String s  = e.getGraph().getInEdges(e).iterator().next().getTarget().getLabel();
				Place ps_pre = placeMap.get(g+s);
				
				
				g  = e.getGraph().getOutEdges(e).iterator().next().getSource().getLabel();
				s  = e.getGraph().getOutEdges(e).iterator().next().getTarget().getLabel();
				Place ps_post = placeMap.get(g+s);
				
				net.addArc(ps_pre,t, 1, this.subNet);
				net.addArc(t,ps_post, 1, this.subNet);
				
				
				
			}

		}


	}
	/**
	 * @param bpmn
	 * @return
	 */
	private Object BPMN2Translate(BPMNDiagram bpmn) {
		Map<String, Place> placeMap = new HashMap<String, Place>();

		PetrinetGraph net = PetrinetFactory.newPetrinet(bpmn.getLabel());
		Marking marking = new Marking();

		for (Flow g : bpmn.getFlows()) {
			String f = g.getSource().getLabel();
			String z = g.getTarget().getLabel();

			Place p = net.addPlace(f + z, this.subNet);
			placeMap.put(f + z, p);
		}

		translateTask(bpmn, placeMap, net);

		translateGateway(bpmn, placeMap, net);

		translateEvent(bpmn, placeMap, net, marking);

		Object[] objects = new Object[3];
		objects[0] = net;
		objects[1] = marking;
		objects[2] = bpmn;
		return objects;

	}


	private Boolean isWellFormed(BPMNDiagram bpmn){
		Map<String,String> maperror = new HashMap<String, String>();
		//  Elementi BPMN che possono essere mappati:
		//  Event-Start  End != NONE
		//  IntermediateCompensation-Event 
		//  EndCompensation-Event 
		//  EndLink-Event 
		//  StartLink-Event 

		GatewayWellFormed(bpmn,maperror);
		EventWellFormed(bpmn,maperror);

		ActivityWellFormed(bpmn,maperror);

		//every object is on a path from a start event or an exception event to an end event

		//if Q is a set of well-formed core BPMN processes and the relation HR is a Direct Acyclic Graph, HR* is a connected graph

		// Nessuna SequenceFlow deve essere collegata allo stesso elemento
		for(Flow g : bpmn.getFlows()) {
			BPMNNode s = g.getSource();
			BPMNNode t = g.getTarget();
			if(s.equals(t)){
				maperror.put("SequenceFlow connessa allo stesso elemento", "flow_s->t");
			}
		}


		if(!maperror.isEmpty()){
			new Error(maperror.toString());
			return false;
		}
		else
			return true;


	}

	private void ActivityWellFormed(BPMNDiagram bpmn, Map<String, String> maperror) {

		for (Activity c : bpmn.getActivities()) {

			if(c.isBCompensation() || c.isBMultiinstance()){
				maperror.put("Attività non valida", c.getId().toString());
			}
			// activities  events have an in-degree of one and an out-degree of one
			if (c.getGraph().getInEdges(c).size()!=1 || c.getGraph().getOutEdges(c).size()!=1){
				maperror.put("Attività non valida troppi archi", c.getId().toString());
			}
			if(c.getLabel().isEmpty()){
				maperror.put("manca il nome dell'attività", c.toString()); 
			}
		}
	}

	private void GatewayWellFormed(BPMNDiagram bpmn,Map<String,String> maperror ){
		for (Gateway g : bpmn.getGateways()) {
			//DATABASED, EVENTBASED, INCLUSIVE, COMPLEX, PARALLEL
			GatewayType gtype = g.getGatewayType();

			switch (gtype) {
			case  INCLUSIVE :   maperror.put("Gateway non valido", g.getId().toString());    break;
			case COMPLEX : maperror.put(" Gateway non valido", g.getId().toString()); break;
			}
			if(g.getLabel().isEmpty()){
				maperror.put("manca il nome del gateway", g.toString()); 
			}

			//fork or decision gateways have an in-degree of one and an out-degree of more than one,
			//join or merge gateways have an out-degree of one and an in-degree of more than one
			if(gtype.equals(GatewayType.DATABASED) || gtype.equals(GatewayType.PARALLEL)){
				if(!(g.getGraph().getInEdges(g).size()==1 && g.getGraph().getOutEdges(g).size()>1 )){

					if(!(g.getGraph().getInEdges(g).size()>1 && g.getGraph().getOutEdges(g).size()==1 )){
						maperror.put(" Gateway non valido troppi archi in entrata o in uscita", g.getLabel()); 
					}
				}

			}
		}
	}

	private void EventWellFormed(BPMNDiagram bpmn,Map<String,String> maperror ){
		for (Event e : bpmn.getEvents()){
			//START, INTERMEDIATE, END;
			EventType type = e.getEventType();
			//MESSAGE, TIMER, ERROR, CANCEL, COMPENSATION, CONDITIONAL, LINK, SIGNAL, TERMINATE, MULTIPLE
			EventTrigger trigger = e.getEventTrigger();

			if(trigger==null)
				trigger=EventTrigger.NONE;
			switch (trigger) {
			case COMPENSATION :   maperror.put("Evento non valido", e.getId().toString());    break;
			case LINK : maperror.put("Evento non valido", e.getId().toString()); break;
			case CONDITIONAL : maperror.put("Evento non valido", e.getId().toString()); break;
			case SIGNAL : maperror.put("Evento non valido", e.getId().toString()); break;


			}

			if(e.getLabel().isEmpty()){
				maperror.put("manca il nome dell'evento", e.toString()); 
			}
			//se trovo start o end che non sono di tipo NONE
			if(type.equals(EventType.START) || type.equals(EventType.END)){
				if (!trigger.equals(EventTrigger.NONE)){
					maperror.put("Evento non valido", e.getId().toString());
				}
			}

			// start events and exception events have an in-degree of zero and an out-degree of one
			if(type.equals(EventType.START) && ( trigger.equals(EventTrigger.ERROR) || trigger.equals(EventTrigger.NONE) )){
				if(e.getGraph().getInEdges(e).size()!=0){
					maperror.put("Errore Evento start con ramo in entrata", e.getId().toString());
				}
				if(e.getGraph().getOutEdges(e).size()!=1){
					maperror.put("Errore Evento start senza o con troppi rami in uscita", e.getId().toString());
				}
			}

			//end events have an out-degree of zero and an in-degree of one
			if(type.equals(EventType.END) &&  trigger.equals(EventTrigger.NONE)){
				if(e.getGraph().getInEdges(e).size()!=1){
					maperror.put("Errore Evento end senza ramo o con troppi rami in entrata", e.getId().toString());
				}
				if(e.getGraph().getOutEdges(e).size()!=0){
					maperror.put("Errore Evento end con ramo in uscita", e.getId().toString());
				}
			}
			//  non-exception intermediate events have an in-degree of one and an out-degree of one
			if(type.equals(EventType.INTERMEDIATE) &&  !trigger.equals(EventTrigger.ERROR)){
				if(e.getGraph().getInEdges(e).size()!=1 && e.getGraph().getOutEdges(e).size()!=1){
					maperror.put("Evento  con piu di un arco", e.getId().toString());
				}
			}

		}
	}

	private Xpdl importXpdlFromStream(PluginContext context, InputStream input,
			String filename, long fileSizeInBytes) throws Exception {
		/*
		 * Get an XML pull parser.
		 */
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		/*
		 * Initialize the parser on the provided input.
		 */
		xpp.setInput(input, null);
		/*
		 * Get the first event type.
		 */
		int eventType = xpp.getEventType();
		/*
		 * Create a fresh XPDL object.
		 */
		Xpdl xpdl = new Xpdl();

		/*
		 * Skip whatever we find until we've found a start tag.
		 */
		while (eventType != XmlPullParser.START_TAG) {
			eventType = xpp.next();
		}
		/*
		 * Check whether start tag corresponds to XPDL start tag.
		 */
		if (xpp.getName().equals(xpdl.tag)) {
			/*
			 * Yes it does. Import the XPDL element.
			 */
			xpdl.importElement(xpp, xpdl);
		} else {
			/*
			 * No it does not. Return null to signal failure.
			 */
			xpdl.log(xpdl.tag, xpp.getLineNumber(), "Expected " + xpdl.tag
					+ ", got " + xpp.getName());
		}
		if (xpdl.hasErrors()) {
			context.getProvidedObjectManager().createProvidedObject(
					"Log of XPDL import", xpdl.getLog(), XLog.class, context);
			return null;
		}
		return xpdl;
	}
}
