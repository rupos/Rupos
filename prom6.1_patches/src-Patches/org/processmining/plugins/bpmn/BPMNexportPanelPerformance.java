package org.processmining.plugins.bpmn;

import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
import javax.swing.table.AbstractTableModel;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.plugins.log.ui.logdialog.LogViewUI;
import org.processmining.plugins.petrinet.replayfitness.ConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.PetriNetDrawUtil;
import org.processmining.plugins.petrinet.replayfitness.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;
import org.processmining.plugins.xpdl.Xpdl;

import com.fluxicon.slickerbox.components.AutoFocusButton;
import com.fluxicon.slickerbox.components.SlickerButton;
import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class BPMNexportPanelPerformance extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3531962030544060794L;
	
	private  JComponent netPNView;
	private  JComponent netBPMNView;
	private BPMNDiagram bpmnvisulizated;
	private JTable tab;



	public BPMNexportPanelPerformance(UIPluginContext context, Petrinet net,
			XLog log, Progress progress, BPMNexportResult export) {

		
		TotalPerformanceResult tovisualize = export.getTotalPerformanceresult();
		
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawperformancenet(netx, tovisualize.getListperformance().get(0).getList(), tovisualize.getListperformance().get(0).getMaparc());
		
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		bpmnvisulizated= export.getBPMNtraslate();
		netBPMNView= ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnvisulizated);

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

	
	public  JComponent tabtrace(final TotalPerformanceResult tpr, final Petrinet net, final UIPluginContext context,  final BPMNexportResult export) {

		JPanel jp = new JPanel();
		JPanel jp1 = new JPanel();
		tab = new JTable(new AbstractTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -325779969853904892L;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return tpr.getListperformance().get(rowIndex).getTraceName();//rowIndex;
			}

			@Override
			public int getRowCount() {

				return tpr.getListperformance().size();
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
		
		JButton saveButton = new SlickerButton("save as BPMN...");
		
		update.setOpaque(false);
		
		saveButton.setOpaque(false);
		
		update.setFont(new Font("Monospaced", Font.PLAIN, 12));
	
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
						Xpdl newxpdl=null;
						try {
							 newxpdl = BPMNexportUtil.exportToXpdl(context,export.getTraslateBpmnresult().getXpdl(), export.getTraslateBpmnresult(), bpmnvisulizated);
						} catch (Exception e1) {
							
							//e1.printStackTrace();
							newxpdl= export.getXpdltraslate();
						}
						outWriter.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +newxpdl.exportElement());
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
		
		jp.add(saveButton,BorderLayout.EAST);
		
		jp1.add(jp);
		jp1.add(scrollpane,BorderLayout.SOUTH);

		

		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i=tab.getSelectedRow();
				if(i>=0){
					Petrinet netx = PetrinetFactory.clonePetrinet(net);
					
					PetriNetDrawUtil.drawperformancenet(netx, tpr.getListperformance().get(i).getList(), tpr.getListperformance().get(i).getMaparc());
					bpmnvisulizated =	BPMNexportUtil.exportPerformancetoBPMN(export.getTraslateBpmnresult(), tpr.getListperformance().get(i).getList(), tpr.getListperformance().get(i).getMaparc());
					
					remove(netPNView);
					remove(netBPMNView);
					netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
					netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnvisulizated);
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
