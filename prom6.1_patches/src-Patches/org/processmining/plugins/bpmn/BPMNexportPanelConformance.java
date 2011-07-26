package org.processmining.plugins.bpmn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import info.clearthought.layout.TableLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Artifacts.ArtifactType;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.log.ui.logdialog.LogViewUI;
import org.processmining.plugins.petrinet.replayfitness.ConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.TotalConformanceResult;

import com.fluxicon.slickerbox.components.AutoFocusButton;
import com.fluxicon.slickerbox.components.SlickerButton;
import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class BPMNexportPanelConformance extends JPanel{




	/**
	 * 
	 */

	private static final long serialVersionUID = -7258945029957912308L;
	private  JComponent netPNView;
	private  JComponent netBPMNView;
	private JTable tab;



	public BPMNexportPanelConformance(UIPluginContext context, Petrinet net,
			XLog log, Progress progress, BPMNexportResult export) {

		TotalConformanceResult tovisualize = export.getTotalconformanceresult();
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		drawfitnessnet(netx,tovisualize.getTotal());
		
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		netBPMNView= ProMJGraphVisualizer.instance().visualizeGraph(context, export.getBPMNtraslate());

		JComponent logView = new LogViewUI(log);

		//JComponent totalresult = visualizestring( UItotalResult(tovisualize));

		JComponent tab = tabtrace(tovisualize,net,context,export);

		double border = 1;
		double size[][] =
		{{border, 1, 1, TableLayout.FILL, 1, 450, border},  // Columns
				{border, 250, 1, TableLayout.FILL, 1, 250, border}}; // Rows

		setLayout(new TableLayout(size));

		
		add (netBPMNView, "1, 1, 5, 1"); // Top
		add (netPNView, "1, 5, 5, 5"); // Bottom

		add (tab, "5, 3      "); // Right
		add (logView, "3, 3      "); // Center


	}

	private String UItotalResult(TotalConformanceResult result){

		return "<html>"+this.toHTMLfromFRT(result)+"</html>";
	}

	public static JComponent visualizestring( String tovisualize) {
		JScrollPane sp = new JScrollPane();
		sp.setOpaque(false);
		sp.getViewport().setOpaque(false);
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setViewportBorder(BorderFactory.createLineBorder(new Color(10, 10, 10), 2));
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		SlickerDecorator.instance().decorate(sp.getVerticalScrollBar(), new Color(0, 0, 0, 0),
				new Color (140, 140, 140), new Color(80, 80, 80));
		sp.getVerticalScrollBar().setOpaque(false);
		SlickerDecorator.instance().decorate(sp.getHorizontalScrollBar(), new Color(0, 0, 0, 0),
				new Color (140, 140, 140), new Color(80, 80, 80));
		sp.getHorizontalScrollBar().setOpaque(false);
		JLabel l = new JLabel(tovisualize);
		sp.setViewportView(l);

		return sp;
	}

	private String toHTMLfromFRT(TotalConformanceResult tt){
		String ret="</p>";
		String out = this.toHTMLfromTR(tt.getTotal());
		Integer index = 1;
		for(ConformanceResult f : tt.getList()){

			out += "<p>------------ Traccia n."+(index++)+" ------------"+ret;
			out +=this.toHTMLfromTR(f); 
			out += "<p>-----------------------------------"+ret;


		}

		return out;
	}

	private String toHTMLfromTR(ConformanceResult totalResult){
		String ret="</p>";
		String tot ="<p>Trace Name:" +totalResult.getTracename()+"</p>";
		tot +="<p> Conformance totale:" +totalResult.getConformance()+"</p>";
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
	public  JComponent tabtrace(final TotalConformanceResult tcr, final Petrinet net, final UIPluginContext context,  final BPMNexportResult export) {

		JPanel jp = new JPanel();
		JPanel jp1 = new JPanel();
		tab = new JTable(new AbstractTableModel() {


			private static final long serialVersionUID = -3512537393802566662L;
	

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return tcr.getList().get(rowIndex).getTracename();//rowIndex;
			}

			@Override
			public int getRowCount() {

				return tcr.getList().size();
			}

			@Override
			public int getColumnCount() {

				return 1;
			}

			public String getColumnName(int col) { 

				return "List of trace"; 
			}

			public boolean isCellEditable(int row, int col) 
			{ 

				return false; 
			}
		});
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp1.setLayout(new BoxLayout(jp1, BoxLayout.Y_AXIS));
		JScrollPane scrollpane = new JScrollPane(tab); 
		scrollpane.setOpaque(false);
		scrollpane.getViewport().setOpaque(false);
		scrollpane.setBorder(BorderFactory.createEmptyBorder());
		scrollpane.setViewportBorder(BorderFactory.createLineBorder(new Color(10, 10, 10), 2));
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		SlickerDecorator.instance().decorate(scrollpane.getVerticalScrollBar(), new Color(0, 0, 0, 0),
				new Color (140, 140, 140), new Color(80, 80, 80));
		scrollpane.getVerticalScrollBar().setOpaque(false);
		SlickerDecorator.instance().decorate(scrollpane.getHorizontalScrollBar(), new Color(0, 0, 0, 0),
				new Color (140, 140, 140), new Color(80, 80, 80));
		scrollpane.getHorizontalScrollBar().setOpaque(false);



		JButton update  = new AutoFocusButton("Update");
		JButton	updateall  = new AutoFocusButton("UpdateAll");
		JButton saveButton = new SlickerButton("save as BPMN...");
		
		update.setOpaque(false);
		updateall.setOpaque(false);
		saveButton.setOpaque(false);
		
		update.setFont(new Font("Monospaced", Font.PLAIN, 12));
		updateall.setFont(new Font("Monospaced", Font.PLAIN, 12));
		saveButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser saveDialog = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        "XPDL", "xpdl");
				saveDialog.setFileFilter(filter);

				saveDialog.setSelectedFile(new File(export.getXpdltraslate().getName()+"Summary.xpdl")); 
				if (saveDialog.showSaveDialog(context.getGlobalContext().getUI()) == JFileChooser.APPROVE_OPTION) {
					File outFile = saveDialog.getSelectedFile();
					try {
						BufferedWriter outWriter = new BufferedWriter(new FileWriter(outFile));
						outWriter.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +export.getXpdltraslate().exportElement());
						outWriter.flush();
						outWriter.close();
						JOptionPane.showMessageDialog(context.getGlobalContext().getUI(),
								"BPMN has been saved\nto XPDL file!", "BPMN saved.",
								JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		jp.add(update,BorderLayout.NORTH);
		jp.add(updateall,BorderLayout.WEST);
		jp.add(saveButton,BorderLayout.EAST);
		
		jp1.add(jp);
		jp1.add(scrollpane,BorderLayout.SOUTH);

		updateall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Petrinet netx = PetrinetFactory.clonePetrinet(net);
				drawfitnessnet(netx,tcr.getTotal());
				remove(netPNView);
				remove(netBPMNView);
				netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
				netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, export.getBPMNtraslate());
				add (netPNView, "1, 5, 5, 5");
				add (netBPMNView, "1, 1, 5, 1");
				revalidate();
				repaint();

			}

		});

		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i=tab.getSelectedRow();
				if(i>=0){
					Petrinet netx = PetrinetFactory.clonePetrinet(net);
					drawfitnessnet(netx,tcr.getList().get(i));
					BPMNDiagram bpmnx = exportConformancetoBPMN(export.getTraslateBpmnresult(), tcr.getList().get(i));
					remove(netPNView);
					remove(netBPMNView);
					netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
					netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnx);
					add (netPNView, "1, 5, 5, 5");
					add (netBPMNView, "1, 1, 5, 1");
					revalidate();
					repaint();
				}

			}
		});

		return jp1;///scrollpane;

	}

	public  void drawfitnessnet(Petrinet net,ConformanceResult totalResult) {

		Map<String,Integer> missplacename2occ = new HashMap<String, Integer>();
		Map<String,Integer> remplacename2occ = new HashMap<String, Integer>();

		Map<Arc,Integer> archiattivati =totalResult.getMapArc();

		for(Arc a : archiattivati.keySet()){
			String afrom=a.getSource().getLabel();
			String ato=a.getTarget().getLabel();
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> newa : net.getEdges()){
				String from = newa.getSource().getLabel();
				String to = newa.getTarget().getLabel();
				if((afrom==from) && (ato==to)){
					Integer i = archiattivati.get(a);
					newa.getAttributeMap().put(AttributeMap.LABEL,i.toString() );
					newa.getAttributeMap().put(AttributeMap.TOOLTIP,i.toString() );
					newa.getAttributeMap().put(AttributeMap.SHOWLABEL,true );
				}

			}

		}



		Marking miss = totalResult.getMissingMarking();
		for(Place p : miss.baseSet()){
			int i = miss.occurrences(p);
			missplacename2occ.put(p.getLabel(), i);
		}
		Marking rem = totalResult.getRemainingMarking();
		for(Place p : rem.baseSet()){
			int i = rem.occurrences(p);
			remplacename2occ.put(p.getLabel(), i);
		}


		for (Place pl : net.getPlaces()) {
			int i = 0;
			int ii =0;
			if(missplacename2occ.containsKey(pl.getLabel())){
				i = missplacename2occ.get(pl.getLabel());
			}
			if(remplacename2occ.containsKey(pl.getLabel())){
				ii = remplacename2occ.get(pl.getLabel());
			}
			if(ii>0 && i>0){
				String r=String.valueOf(ii)+"/-"+String.valueOf(i);
				pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				pl.getAttributeMap().remove(AttributeMap.TOOLTIP);
				pl.getAttributeMap().put(AttributeMap.TOOLTIP, r);
				pl.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				//this.inserPlace(pl.getLabel(), x, y, "red", r);
			}else if (ii>0 && i<=0){
				pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				pl.getAttributeMap().remove(AttributeMap.TOOLTIP);
				pl.getAttributeMap().put(AttributeMap.TOOLTIP, String.valueOf(ii));
				pl.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				//this.inserPlace(pl.getLabel(), x, y, "red", String.valueOf(ii));
			}else if (i>0 && ii<=0){
				//this.inserPlace(pl.getLabel(), x, y, "red", "-"+String.valueOf(i));
				pl.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
				pl.getAttributeMap().remove(AttributeMap.TOOLTIP);
				pl.getAttributeMap().put(AttributeMap.TOOLTIP, String.valueOf(-i));
				pl.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
			}


		}
		for (Transition ts : net.getTransitions()) {

			for (Transition tsx : totalResult.getMapTransition().keySet()){

				if(tsx.getLabel().equals(ts.getLabel())){
					ts.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.ORANGE);
				}
			}
		}





	}
	
	private BPMNDiagram exportConformancetoBPMN(
			TraslateBPMNResult traslateBpmnresult,
			ConformanceResult conformanceresult) {

		//clona bpmn
		BPMNDiagram bpmn =BPMNDiagramFactory.cloneBPMNDiagram(traslateBpmnresult.getBpmn()) ;

		Map<String, Place> MapArc2Place = traslateBpmnresult.getPlaceMap();
		
		Marking remaning = conformanceresult.getRemainingMarking();
		Marking missing = conformanceresult.getMissingMarking();
		Map<Transition, Integer> transnotfit = conformanceresult.getMapTransition(); 
		Map<Arc, Integer> attivazionearchi = conformanceresult.getMapArc(); 


		Map<String,Integer> ArchiAttivatiBPMN = new HashMap<String, Integer>();
		Map<String,String> archibpmnwitherrorconformance = new HashMap<String, String>();


		//gli archi che attivo sul bpmn sono gli archi uscenti delle piazze "arco"
		for(Place p : MapArc2Place.values()){
			int att=0;
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> egde : p.getGraph().getOutEdges(p)){
				if(attivazionearchi.containsKey(egde)){
					att += attivazionearchi.get(egde);

				}

			}
			ArchiAttivatiBPMN.put(p.getLabel(), att);
			
		}

		//transizioni che nn fittano
		String ret = "<br/>";
		for(Transition t : transnotfit.keySet()){
			if(!t.isInvisible()){
				String tname = t.getLabel();
				String name = (String) tname.subSequence(0, tname.indexOf("+"));
				Activity activity = null;
				//cerco l'attività bpmn a cui collegare l'artifacts
				for(Activity a: bpmn.getActivities()){
					if(a.getLabel().equals(name)){
						activity= a;
						break;
					}
				}
				String unsoundallert = "";
				for(Place p :remaning.baseSet()){
					if(p.getLabel().equals(name)){
						unsoundallert+=ret+" Task missing competition\n"; 
					}else if(p.getLabel().startsWith(name)){
						unsoundallert+=ret+" Task unsound executions\n"; 
					}else if(p.getLabel().endsWith(name)){
						unsoundallert+=ret+" Task interrupted executions\n"; 
					}
				}
				for(Place p :missing.baseSet()){
					if(p.getLabel().equals(name)){
						unsoundallert+=ret+" Task internal failures"; 
					}
				}
				if(activity!=null){
					String numtracce = String.valueOf(transnotfit.get(t));
					String plusname = (String) tname.subSequence( tname.indexOf("+"),tname.length());
					String label = "<html>Unsound "+plusname+":"+numtracce+" tracce"+unsoundallert+"<html>";
					Artifacts art =  bpmn.addArtifacts(label, ArtifactType.TEXTANNOATION, null);

					bpmn.addFlowAssociation(art, activity, null);

				}
			}

		}
		for(Transition t : traslateBpmnresult.getPetri().getTransitions()){
			//cerco la transizione del fork
			if(t.getLabel().endsWith("_fork")){
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> p = t.getGraph().getOutEdges(t);
				Vector<String> targ = new Vector<String>();
				for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> e : p){
					Place target = (Place) e.getTarget();
					targ.add(target.getLabel());
				}
				for(String placename: targ ){
					for(Place place :remaning.baseSet()){
						if(place.getLabel().equals(placename)){
							System.out.println(ret+" Fork internal failures");
							archibpmnwitherrorconformance.put(place.getLabel()," Fork internal failures");
						}

					}
				}				
			}
		}
		//metto gli attraversamenti sugli archi bpmn
		for (Flow f : bpmn.getFlows()){
			String from = f.getSource().getLabel();
			String to = f.getTarget().getLabel();
			if(ArchiAttivatiBPMN.containsKey(from+to)){
				Integer i = ArchiAttivatiBPMN.get(from+to);
				if(i>0){
					f.getAttributeMap().put(AttributeMap.LABEL,i.toString() );
					f.getAttributeMap().put(AttributeMap.TOOLTIP,i.toString() );
					f.getAttributeMap().put(AttributeMap.SHOWLABEL,true );
				}

			}
			//metto eventuali errore sul arco di fork
			if(archibpmnwitherrorconformance.containsKey(from+to)){

				String flowerr = archibpmnwitherrorconformance.get(from+to);
				f.getAttributeMap().remove(AttributeMap.TOOLTIP);

				f.getAttributeMap().put(AttributeMap.TOOLTIP, flowerr);
				f.getAttributeMap().remove(AttributeMap.SHOWLABEL);
				f.getAttributeMap().put(AttributeMap.SHOWLABEL,true );
				f.getAttributeMap().put(AttributeMap.EDGECOLOR, Color.RED);

			}
		}

		return bpmn;

	}


}
