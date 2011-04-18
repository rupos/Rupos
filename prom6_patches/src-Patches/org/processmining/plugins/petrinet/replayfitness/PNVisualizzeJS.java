package org.processmining.plugins.petrinet.replayfitness;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

/**
 * @author spagnolo1
 *
 */
public class PNVisualizzeJS {

	private int lworld=800;
	private int hworld=300;
	private String place="";
	private String tran="";
	private String arc="";
	private String head = "<html> <head> <script src=\"raphael-min.js\"type=\"text/javascript\"></script>"
		+ "<script src=\"joint.min.js\" type=\"text/javascript\"></script>"
		+ "<script src=\"joint.dia.min.js\" type=\"text/javascript\"></script>"
		+ "<script src=\"joint.dia.fsa.min.js\" type=\"text/javascript\"></script>"
		+ "<script src=\"joint.dia.pn.min.js\" type=\"text/javascript\"></script>"
		+ "</head><body><div id=\"world\"></div>"
		+ "<script type=\"text/javascript\"> "
		+ "var pn = Joint.dia.pn;\n ";

	private String foot = "</script></body></html>";
	private String all = "var all = [";

	public PNVisualizzeJS(){

	}


	/**Inserisce un place
	 * @param name: nome del place 
	 * @param token: numero di token
	 * @param xx: coordinate posizione x
	 * @param yy: coordinate posizione y
	 */
	public void inserPlace(String name, int token, int xx, int yy){
		Point coord = updateworld(xx,yy);
		String label=name;

		name=name.replaceAll("\\W", "");

		place +="var "+name+" = pn.Place.create({position: {x: "+coord.x+", y: "+coord.y+"}, label: \""+label+"\", tokens: "+token+"});\n ";
		all+=name+",";


	}

	/** Inserisce un place e se questo ha il nome Start token=1
	 * @param name: nome del place
	 * @param xx: coordinate posizione x
	 * @param yy: coordinate posizione y
	 */
	public void inserPlace(String name, int xx, int yy){
		Point coord = updateworld(xx,yy);
		int token=0;
		String label="";
		if ((name.equals("Start"))||(name.equals("End"))){
			label=name;
		}
		if ((name.trim() == "Start")){
			token=1;
		}


		name=name.replaceAll("\\W", "");

		place +="var "+name+" = pn.Place.create({position: {x: "+coord.x+", y: "+coord.y+"}, label: \""+label+"\", tokens: "+token+"});\n ";
		all+=name+",";



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
		int token=0;
		String label="";
		if ((name.equals("Start"))||(name.equals("End"))){
			label+=name;
		}
		if ((name.trim() == "Start")){
			token=1;
		}
		label+=" "+occ;


		name=name.replaceAll("\\W", "");

		place +="var "+name+" = pn.Place.create({position: {x: "+coord.x+", y: "+coord.y+"}, label: \""+label+"\", tokens: "+token+", attrs: {stroke: \""+color+"\" , fill : \"white\" } });\n ";
		all+=name+",";



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
		all+=name+",";



	}

	/** Inserisce una transizione 
	 * @param name: nome della transizione
	 * @param xx: coordinata x
	 * @param yy: coordinata y
	 */
	public void inserTransiction(String name, int xx, int yy){
		/*Point coord = updateworld(xx,yy);
		String label=name;

		name=name.replaceAll("\\W", "");

		tran +="var "+name+" = pn.Event.create({rect: {x: "+coord.x+", y: "+coord.y+" , width: 7, height: 50}, label: \""+label+"\"});\n ";
		all+=name+",";*/
		this.inserTransiction(name, xx, yy, "white");

	}

	/**Inserisci una connessione
	 * @param in Input connessione
	 * @param out Output connessione
	 * @param label Etichetta
	 */
	public void inserArrow(String in, String out, String label){


		in=in.replaceAll("\\W", "");

		out=out.replaceAll("\\W", "");
		arc +=  in+".joint("+out+", (arrow.label = \""+label+"\", arrow));\n ";


	}


	/** Scrivi su file la rete
	 * @param path del file
	 * @param Result Struttura dati TotalFitnessResult contenente i risultati della fitness
	 */
	public void toFile(String path, TotalFitnessResult Result) {
		FileWriter w;
		String head2 = "Joint.paper(\"world\", "+lworld+", "+hworld+");\n "
		+"var arrow = pn.arrow; ";
		try {
			w = new FileWriter(path);
			BufferedWriter b = new BufferedWriter(w);
			//all+=all.substring(0, all.length()-1)+"];\n ";
			b.write(head);
			b.write(head2);
			b.write(place);
			b.write(tran);
			//b.write(all);
			b.write(arc);
			b.write("</script>");

			//b.write(this.toHTMLfromTR(Result.total));
			b.write(this.toHTMLfromFRT(Result));
			b.write("</body></html>");

			//b.write(foot);
			b.flush();
			b.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**Scrivi su file la rete di petri
	 * @param path del file
	 */
	public void toFile(String path) {
		String head2 = "Joint.paper(\"world\", "+lworld+", "+hworld+");\n "
		+"var arrow = pn.arrow; ";
		FileWriter w;
		try {
			w = new FileWriter(path);
			BufferedWriter b = new BufferedWriter(w);
			//all+=all.substring(0, all.length()-1)+"];\n ";
			b.write(head);
			b.write(head2);
			b.write(place);
			b.write(tran);
			//b.write(all);
			b.write(arc);
			b.write(foot);
			b.flush();
			b.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 *Inserisce gli archi attraversati con etichetta il numero di attraversamenti
	 */
	private void generateJSR(FitnessResult totalResult){

		for (Arc c : totalResult.getMapArc().keySet()){

			int i = totalResult.getMapArc().get(c);

			this.inserArrow(c.getSource().getLabel(), c.getTarget().getLabel(), String.valueOf(i));

		}


	}


	/**Genera una pagina html in cui vengono visualizzate le informazioni di conformance
	 * @param net rete di petri
	 * @param totalResult informazioni di conformance
	 */
	public void generateJS(PetrinetGraph net, TotalFitnessResult Result){
		FitnessResult totalResult = Result.total;
		for (Place pl : net.getPlaces()) {
			Object point =	pl.getAttributeMap().get(AttributeMap.POSITION);
			Point2D xy = (Point2D) point;
			int x = (int) xy.getX();
			int y = (int) xy.getY();
			Marking miss = totalResult.getMissingMarking();
			int i = miss.occurrences(pl);

			Marking rem = totalResult.getRemainingMarking();
			int ii = rem.occurrences(pl);

			if(ii>0 && i>0){
				String r=String.valueOf(ii)+"/-"+String.valueOf(i);
				this.inserPlace(pl.getLabel(), x, y, "red", r);
			}

			if (ii>0 && i<=0){
				this.inserPlace(pl.getLabel(), x, y, "red", String.valueOf(ii));
			}
			if (i>0 && ii<=0){
				this.inserPlace(pl.getLabel(), x, y, "red", "-"+String.valueOf(i));
			}
			if(i<=0 && ii<=0){
				this.inserPlace(pl.getLabel(), x, y);
			}
		}
		boolean flag=true;
		for (Transition ts : net.getTransitions()) {
			Point2D xy = (Point2D) ts.getAttributeMap().get(AttributeMap.POSITION);
			int x = (int) xy.getX();
			int y = (int) xy.getY();

			for (Transition tsx : totalResult.getMapTransition().keySet()){

				if(tsx.equals(ts)){
					this.inserTransiction(ts.getLabel(),x,y,"orange");
					flag=false;
				}
			}
			if(flag){
				this.inserTransiction(ts.getLabel(),x,y);
			}
			flag=true;
			/*Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outset = net
			.getOutEdges(ts);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;

				//	this.inserArrow(ts.getLabel(), arc.getTarget().getLabel(),
					//		arc.getLabel());
				}
			}
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inset = net
			.getInEdges(ts);
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inset) {
				if (edge instanceof Arc) {
					Arc arc = (Arc) edge;

					//this.inserArrow(arc.getSource().getLabel(), ts.getLabel(),
						//	arc.getLabel());
				}
			}*/
		}

		this.generateJSR(totalResult); //archi con peso
		this.toFile("../javascrips/conformance.html", Result);
	}

	private String toHTMLfromFRT(TotalFitnessResult tt){
		String ret="</p>";
		String out = this.toHTMLfromTR(tt.getTotal());
		Integer index = 1;
		for(FitnessResult f : tt.getList()){

			out += "<p>------------ Traccia n."+(index++)+" ------------"+ret;
			out +=this.toHTMLfromTR(f); 
			out += "<p>-----------------------------------"+ret;


		}

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
	private String toHTMLfromTR(FitnessResult totalResult){
		String ret="</p>";
		String tot ="<p> Fitness totale:" +totalResult.getFitness()+"</p>";
		tot+="<p> Missing Marking:"+totalResult.getMissingMarking()+ret;
		tot+="<p> Remaning Marking: "+ totalResult.getRemainingMarking()+ret;
		tot+="<p> Transizioni che non fittano:"+ret;
		for (Transition t : totalResult.getMapTransition().keySet()){
			Integer i = totalResult.getMapTransition().get(t);
			tot +="<p>       "+t+" : "+i+" tracce"+ret;
		}
		tot+="<p> Attivazioni degli archi:"+ret;
		for (Arc a : totalResult.getMapArc().keySet()){
			String asString = "<p> FROM "+a.getSource()+" TO "+a.getTarget();
			Integer i = totalResult.getMapArc().get(a);
			tot += "     "+asString+" : "+i+" attivazioni"+ret;
		}
		return tot;
	}	
}
