
package org.processmining.plugins.petrinet.replay.performance;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;


import java.util.LinkedList;

import java.util.Map;


import org.processmining.framework.connections.Connection;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionID;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.models.connections.GraphLayoutConnection;

import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;


/**
 * @author Dipartimento di Informatica - Rupos
 *
 */

public class PerformanceVisualJS {

	private ConnectionManager cm;
	private int lworld=800;
	private int hworld=300;
	private String place="";
	private String tran="";
	private String arc="";
	private String head = "<!DOCTYPE html><html><head>\n" +
	"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
	"<title>Performance Analysis</title> \n" +
	"<script src=\"raphael.js\" type=\"text/javascript\"></script>\n" +
	"<script src=\"popup.js\" type=\"text/javascript\"></script>\n" +
	"<script src=\"jquery.js\" type=\"text/javascript\"></script>\n" +
	"<script src=\"joint.js\" type=\"text/javascript\"></script>\n" +
	"<script src=\"joint.dia.js\" type=\"text/javascript\"></script>\n" +
	"<script src=\"joint.dia.fsa.js\" type=\"text/javascript\"></script>\n" +
	"<script src=\"joint.dia.pn.js\" type=\"text/javascript\"></script>\n" +
	"</head><body><div id=\"world\"></div>\n"+
	"<script type=\"text/javascript\">\n " +
	" Raphael.fn.drawGrid = function (x, y, w, h, wv, hv, color) {\n" +
	" color = color || \"#000\";\n" +
	" var path = [\"M\", Math.round(x) + .5, Math.round(y) + .5, \"L\", Math.round(x + w) + .5, Math.round(y) + .5, Math.round(x + w) + .5, Math.round(y + h) + .5, Math.round(x) + .5, Math.round(y + h) + .5, Math.round(x) + .5, Math.round(y) + .5],\n" +
	"    rowHeight = h / hv,\n" +
	"    columnWidth = w / wv;\n " +
	"  return this.path(path.join(\",\")).attr({stroke: color});  };\n " +
	" window.onload = function () {\n";


	private String head3 = 	"leftgutter = 30, \n" +
	"bottomgutter = 20,\n " +
	"topgutter = 20,\n " +
	"colorhue = .6 || Math.random(), color = \"hsb(\" + [colorhue, .5, 1] + \")\",\n " +
	"r = Raphael(\"world\", width, height), \n" +
	"txt = {font: '12px Helvetica, Arial', fill: \"#fff\"},\n " +
	"txt1 = {font: '12px Helvetica, Arial', fill: \"#fff\"},\n " +
	"txt2 = {font: '12px Helvetica, Arial', fill: \"#000\"}; \n" +
	"var pn = Joint.dia.pn; var f = Joint.paper(r);\n";

	private String head4 = " var path = r.path().attr({stroke: color, \"stroke-width\": 4, \"stroke-linejoin\": \"round\"}),\n" +
	" label = r.set(),\n " +
	"is_label_visible = true,\n " +
	"leave_timer, blanket = r.set();\n " +
	"label.push(r.text(60, 12, \"Sincte 24 hits--------\").attr(txt).attr({fill: \"#0f0\"}));\n " +
	"label.push(r.text(60, 27, \"22 October 2008\").attr(txt1).attr({fill: \"#f15\"}));\n " +
	"label.push(r.text(60, 42, \"22 October 2008\").attr(txt1).attr({fill: \"#ff0\"})); \n" +
	"label.hide(); \n" +
	"var frame = r.popup(100, 100, label, \"right\").attr({fill: \"#000\", stroke: \"#666\", \"stroke-width\": 2, \"fill-opacity\": .7}).hide();\n ";



	private String foot = "</script></body></html>";
	private String Posplacex = "var Posplacex = new Array(";
	private String Posplacey ="var Posplacey = new Array(";
	private String NamePlace ="var NamePlace = new Array(";
	private String SyncPlace="var SyncPlace = new Array(";
	private String AttPlace="var AttPlace = new Array(";
	private String SoggPlace="var SoggPlace = new Array(";

	private String ePlace = ");\n";


	public PerformanceVisualJS(ConnectionManager c){
		this.cm=c;
	}





	/** Inserisce un place
	 * @param name: nome del place
	 * @param xx: coordinate posizione x
	 * @param yy: coordinate posizione y
	 * @param color: colore del place
	 * @param occ: aggiunge la stringa alla label
	 */
	public void inserPlace(String name, int xx, int yy, String color,String occ){

		Point coord = updateworld(xx,yy);
		String label="";
		int token=0;
		if ((name.equals("Start"))||(name.equals("End"))){
			label+=name;
		}
		if ((name.trim() == "Start")){
			token=1;
		}
		name=name.replaceAll("\\W", "");
		label+=name+" "+occ;

		Posplacex+=coord.x+",";
		Posplacey+=coord.y+",";
		NamePlace+=name+",";


		this.inserPlace(name, label, token, coord.x, coord.y, color);


	}


	/** Inserisce un place
	 * @param name: nome del place
	 * @param label del place
	 * @param token del place
	 * @param xx: coordinate posizione x
	 * @param yy: coordinate posizione y
	 * @param color: colore del place
	 * @param occ: aggiunge la stringa alla label
	 */
	public void inserPlace(String name,String label,int token, int xx, int yy, String color){

		place +="var "+name+" = pn.Place.create({position: {x: "+xx+", y: "+yy+"}, label: \""+label+"\", tokens: "+token+", attrs: {stroke: \""+color+"\" , fill : \"white\" } });\n ";
		//all+=name+",";

	}
	/** Inserisce una transizione 
	 * @param name: nome della transizione
	 * @param xx: coordinata x
	 * @param yy: coordinata y
	 * @param color: colore transizione
	 */
	public void inserTransiction(String name, int xx, int yy, String color){
		Point coord = updateworld(xx,yy);
		String label=name;

		name=name.replaceAll("\\W", "");

		tran +="var "+name+" = pn.Event.create({rect: {x: "+coord.x+", y: "+coord.y+" , width: 7, height: 50}, label: \""+label+"\", attrs: { stroke: \"black\", fill: \""+color+"\" }});\n ";



	}

	/** Inserisce una transizione 
	 * @param name: nome della transizione
	 * @param xx: coordinata x
	 * @param yy: coordinata y
	 */
	public void inserTransiction(String name, int xx, int yy){

		this.inserTransiction(name, xx, yy, "black");

	}

	/**Inserisci una connessione
	 * @param in Input connessione
	 * @param out Output connessione
	 * @param label Etichetta
	 */
	public void inserArrow(String in, String out, String label){


		in=in.replaceAll("\\W", "");

		out=out.replaceAll("\\W", "");
		arc +=  in+".joint("+out+", (pn.arrow.label = \""+label+"\", pn.arrow));\n ";


	}


	/** Scrivi su file html la rete con i risultati annessi
	 * @param path del file
	 * @param Result Struttura dati TotalFitnessResult contenente i risultati della fitness
	 */
	public void toFile(String path,Map<Place, PerformanceData> Result) {
		FileWriter w;
		String head2 = "// Draw \n" +
		"var width = "+lworld+", " +
		"    height = "+hworld+", ";
		try {
			w = new FileWriter(path);
			BufferedWriter b = new BufferedWriter(w);

			b.write(head);
			b.write(head2);
			b.write(head3);
			b.write(place);
			b.write(tran);
			b.write(arc);
			b.write(head4);
			Posplacex=Posplacex.substring(0,Posplacex.length()-1 );
			Posplacey=Posplacey.substring(0,Posplacey.length()-1 );
			NamePlace=NamePlace.substring(0,NamePlace.length()-1 );
			b.write(Posplacex+ePlace);
			b.write(Posplacey+ePlace);
			b.write(NamePlace+ePlace);
			b.write(SyncPlace+ePlace);
			b.write(AttPlace+ePlace);
			b.write(SoggPlace+ePlace);

			String popup = "for (var j = 0, ii = Posplacex.length; j < ii; j++) { \n" +
			"var y =Posplacey[j],\n " +
			"x =Posplacex[j];\n " +
			"var dot = NamePlace[j];\n " +
			"blanket.push(r.circle(x,y,40).attr({stroke: \"none\", fill: \"#fff\", opacity: 0}));\n " +
			"var rect = blanket[blanket.length - 1];\n " +
			"(function (x, y, SyncPlace, AttPlace,SoggPlace ,dot,j) {\n " +
			"var timer; \n" +
			"rect.hover(function () { \n" +
			"clearTimeout(leave_timer);\n " +
			"var side = \"right\"; if (x + frame.getBBox().width > width) { side = \"left\"; }\n " +
			"var ppp = r.popup(x, y, label, side, 1); \n" +
			"frame.show().stop().animate({path: ppp.path}, 200 * is_label_visible);\n " +
			"label[0].attr({text: \"Tempo Attesa: \"+AttPlace[j]}).show().stop().animateWith(frame, {translation: [ppp.dx, ppp.dy]}, 200 * is_label_visible).toFront();\n " +
			"label[1].attr({text: \"Tempo Sincro: \"+SyncPlace[j] }).show().stop().animateWith(frame, {translation: [ppp.dx, ppp.dy]}, 200 * is_label_visible).toFront(); \n" +
			"label[2].attr({text: \"Tempo Soggiorno: \"+SoggPlace[j]}).show().stop().animateWith(frame, {translation: [ppp.dx, ppp.dy]}, 200 * is_label_visible).toFront();\n " +
			"dot.attr(\"r\", 21); is_label_visible = true; },\n " +
			"function () { dot.attr(\"r\", 20); leave_timer = setTimeout(function () {\n " +
			"frame.hide(); label[0].hide(); label[1].hide(); label[2].hide(); is_label_visible = false; }, 1); }); })(x, y, SyncPlace, AttPlace,SoggPlace, dot,j); }\n" +
			" frame.toFront(); label[0].toFront(); label[1].toFront(); label[2].toFront(); blanket.toFront(); };\n";
			b.write(popup);
			b.write("</script>");
			b.write(this.toHTMLfromMPP(Result));
			b.write("</body></html>");

			b.flush();
			b.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**Scrivi su file solo la rete di petri
	 * @param path del file

	public void toFile(String path) {
		String head2 = "Joint.paper(\"world\", "+lworld+", "+hworld+");\n "
		+"var arrow = pn.arrow; ";
		FileWriter w;
		try {
			w = new FileWriter(path);
			BufferedWriter b = new BufferedWriter(w);

			b.write(head);
			b.write(head2);
			b.write(place);
			b.write(tran);

			b.write(arc);
			b.write(foot);
			b.flush();
			b.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	} */

	/**
	 *Inserisce gli archi attraversati con etichetta il numero di attraversamenti

	private void generateJSR(FitnessResult totalResult){

		for (Arc c : totalResult.getMapArc().keySet()){

			int i = totalResult.getMapArc().get(c);

			this.inserArrow(c.getSource().getLabel(), c.getTarget().getLabel(), String.valueOf(i));

		}


	}*/


	/**Genera una pagina html in cui vengono visualizzate le informazioni di conformance
	 * @param file nome del file nel quale scrivere le informazioni di cornformance
	 * @param net rete di petri
	 * @param totalResult informazioni di conformance
	 */
	public void generateJS(String file, PetrinetGraph net, Map<Place, PerformanceData> Result,Map<Arc, Integer> maparc){
		GraphLayoutConnection layout = findConnection(cm, net);
		Collection<Place> temp = null;
		temp = net.getPlaces();
		LinkedList<Place>	 pop =new LinkedList<Place>(temp);
		for(Place pla : Result.keySet()){
			//	for (Place pl : net.getPlaces()) {
			//		if(pla.equals(pl)){
			Point2D xy = layout.getPosition(pla);
			//Object point =	pla.getAttributeMap().get(AttributeMap.POLYGON_POINTS);
			//Point2D xy = (Point2D) point;
			int x = (int) xy.getX();
			int y = (int) xy.getY();
			this.inserPlace(pla.getLabel(), x, y, "black", "");
			pop.remove(pla);
			//	}
			//}
		}
		if(!pop.isEmpty()){
			for(Place pp : pop) {
				Point2D xy = layout.getPosition(pp);
				//Object point =	pp.getAttributeMap().get(AttributeMap.POLYGON_POINTS);
				//Point2D xy = (Point2D) point;
				int x = (int) xy.getX();
				int y = (int) xy.getY();
				this.inserPlace(pp.getLabel(), x, y, "black", "");
			}
		}


		/*for (Place pl : net.getPlaces()) {
			Object point =	pl.getAttributeMap().get(AttributeMap.POSITION);
			Point2D xy = (Point2D) point;
			int x = (int) xy.getX();
			int y = (int) xy.getY();




				this.inserPlace(pl.getLabel(), x, y, "black", "");

		}*/

		for (Transition ts : net.getTransitions()) {
			Point2D xy = layout.getPosition(ts);
			//Point2D xy = (Point2D) ts.getAttributeMap().get(AttributeMap.POLYGON_POINTS);
			int x = (int) xy.getX();
			int y = (int) xy.getY();


			this.inserTransiction(ts.getLabel(),x,y);

			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outset = net
			.getOutEdges(ts);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					if(maparc.containsKey(arc)){
						Integer i = maparc.get(arc);
						this.inserArrow(ts.getLabel(), arc.getTarget().getLabel(),i.toString());
					}else{
						this.inserArrow(ts.getLabel(), arc.getTarget().getLabel(),"0");
					}
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inset = net
			.getInEdges(ts);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;
					if(maparc.containsKey(arc)){
						Integer i = maparc.get(arc);
						this.inserArrow(arc.getSource().getLabel(), ts.getLabel(), i.toString());
					}else{
						this.inserArrow(arc.getSource().getLabel(), ts.getLabel(), "0");
					}

				}
			}
		}
		this.PerfRes(Result);

		//this.generateJSR(totalResult); //archi con peso
		this.toFile(file,Result);
	}
	private GraphLayoutConnection findConnection(ConnectionManager manager, PetrinetGraph graph) {
		Collection<ConnectionID> cids = manager.getConnectionIDs();
		for (ConnectionID id : cids) {
			Connection c;
			try {
				c = manager.getConnection(id);
			} catch (ConnectionCannotBeObtained e) {
				continue;
			}
			if (c != null && !c.isRemoved() && c instanceof GraphLayoutConnection
					&& c.getObjectWithRole(GraphLayoutConnection.GRAPH) == graph) {
				return (GraphLayoutConnection) c;
			}
		}
		return null;

	}

	private void PerfRes(Map<Place, PerformanceData> Result){

		for(PerformanceData perfRes : Result.values()){
			SyncPlace+=perfRes.synchTime+",";
			AttPlace+=perfRes.waitTime+",";
			SoggPlace+=perfRes.time+",";
			//place.tokenCount
		}
		SyncPlace=SyncPlace.substring(0,SyncPlace.length()-1 );
		AttPlace=AttPlace.substring(0,AttPlace.length()-1 );
		SoggPlace=SoggPlace.substring(0,SoggPlace.length()-1 );
	}


	private String toHTMLfromFRT(Map<Place, PerformanceData> Result ){
		String ret="</p>";
		String out = "";
		for(Map.Entry pairs :Result.entrySet()){
			Place place =(Place) pairs.getKey();
			PerformanceData perRes = (PerformanceData) pairs.getValue();
			out += "<p>------------ Place Name "+place.getLabel()+" ------------"+ret;
			out += "<p>Wait Time: "+perRes.waitTime+ret;
			out += "<p>SyncTime Time: "+perRes.synchTime+ret;
			out += "<p>Soggiorno Time: "+perRes.time+ret;
			out += "<p>-----------------------------------"+ret;
		}
		/*Integer index = 1;
		Iterator it = Result.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			Place place =(Place) pairs.getKey();
			PerformanceData perRes = (PerformanceData) pairs.getValue();
			out += "<p>------------ Place Name "+place.getLabel()+" ------------"+ret;
			out += "<p>Wait Time: "+perRes.waitTime+ret;
			out += "<p>SyncTime Time: "+perRes.synchTime+ret;
			out += "<p>Soggiorno Time: "+perRes.time+ret;
			out += "<p>-----------------------------------"+ret;
		}*/


		return out;
	}


	private String toHTMLfromMPP(Map<Place, PerformanceData> Result ){
		String start = "<table border=\"2px\" style=\"width: 100%\">";
		String end ="</table>";
		String placen ="<td>Name Place</td>\n";
		String waitt="<td>WaitTime</td>\n";
		String synct="<td>SyncTime</td>\n";
		String sogtime="<td>SoggTime</td>\n";

		String out = "";
		for(Map.Entry pairs:Result.entrySet() ){
			Place place =(Place) pairs.getKey();
			PerformanceData perRes = (PerformanceData) pairs.getValue();
			placen+="<td>"+place.getLabel()+"</td>\n";
			waitt += "<td>"+perRes.waitTime+"</td>\n";
			synct += "<td>"+perRes.synchTime+"</td>\n";
			sogtime += "<td>"+perRes.time+"</td>\n";
		}
		/*Iterator it = Result.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			Place place =(Place) pairs.getKey();
			PerformanceData perRes = (PerformanceData) pairs.getValue();
			placen+="<td>"+place.getLabel()+"</td>\n";
			waitt += "<td>"+perRes.waitTime+"</td>\n";
			synct += "<td>"+perRes.synchTime+"</td>\n";
			sogtime += "<td>"+perRes.time+"</td>\n";

		}*/

		out=start+"<tr>"+placen+"</tr>"+"<tr>"+waitt+"</tr>"+"<tr>"+synct+"</tr>"+"<tr>"+sogtime+"</tr>"+end;
		return out;
	}

	private Point updateworld(int xx, int yy){
		if(xx>lworld){
			lworld=xx+50;
		}
		if(yy>hworld){
			hworld=yy+50;
		}
		if(xx<20){
			xx=24;
		}
		if(yy<50){
			yy=50;
		}

		return new Point(xx,yy);

	}

}
