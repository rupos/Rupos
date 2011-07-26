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
import org.processmining.plugins.petrinet.replayfitness.PetriNetDrawUtil;
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
		PetriNetDrawUtil.drawfitnessnet(netx,tovisualize.getTotal());
		
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
				PetriNetDrawUtil.drawfitnessnet(netx,tcr.getTotal());
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
					PetriNetDrawUtil.drawfitnessnet(netx,tcr.getList().get(i));
					BPMNDiagram bpmnx = BPMNexportUtil.exportConformancetoBPMN(export.getTraslateBpmnresult(), tcr.getList().get(i));
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

	
	

}
