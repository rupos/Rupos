
package org.processmining.plugins.petrinet.replayfitness;

import info.clearthought.layout.TableLayout;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import org.deckfour.xes.model.XLog;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.plugins.log.ui.logdialog.LogViewUI;

import com.fluxicon.slickerbox.components.AutoFocusButton;
import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class ReplayPerformanceRuposPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -107889379484400541L;
	private  JComponent netPNView,totalresult;
	private JTable tab;

	public ReplayPerformanceRuposPanel(PluginContext context, Petrinet netx,
			XLog log, Progress progress, TotalPerformanceResult tovisualize) {
		
		
		Petrinet net = PetrinetFactory.clonePetrinet(netx);
		PetriNetDrawUtil.drawperformancenet(net, tovisualize.getListperformance().get(0).getList(), tovisualize.getListperformance().get(0).getMaparc());
		String  visualize= PetriNetDrawUtil.toHTMLfromMPP(tovisualize.getListperformance().get(0).getList());
		
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, net);
		
		JComponent logView = new LogViewUI(log);
		
	     totalresult = visualizestring( visualize);
		
		JComponent tab = tabtrace(tovisualize,netx,context);
		
		
		double border = 1;
		double size[][] =
		{{border, 1, 1, TableLayout.FILL, 1, 200, border},  // Columns
				{border, 300, 1, TableLayout.FILL, 1, 160, border}}; // Rows

		setLayout(new TableLayout(size));

		// Add
		add (netPNView, "1, 1, 5, 1"); // Top
		add (logView, "1, 5, 5, 5"); // Bottom
		
		add (tab, "5, 3      "); // Right
		add (totalresult, "3, 3      "); // Center

		/*double size[][] = { { TableLayoutConstants.FILL,200 }, { TableLayoutConstants.FILL,  TableLayoutConstants.FILL} };
		setLayout(new TableLayout(size));
		
		add(netPNView, "0, 0");
		
		add(totalresult, "0, 1");
		add(logView, "1, 1");*/
	}
	
	
	
	
	
	private JComponent tabtrace(final TotalPerformanceResult tovisualize,
			final Petrinet netx, final PluginContext context) {
		JPanel jp = new JPanel();
		JPanel jp1 = new JPanel();
		 tab = new JTable(new AbstractTableModel() {
		
			private static final long serialVersionUID = -2176731961693608635L;

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return tovisualize.getListperformance().get(rowIndex).getTraceName();//rowIndex;//tcr.getList().get(rowIndex).getTracename();//
			}
			
			@Override
			public int getRowCount() {
			
				return tovisualize.getListperformance().size();
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
		
		 jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
		 jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
		 
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
		
		

		JButton button  = new AutoFocusButton("Update");
		
		button.setOpaque(false);
		
		
		button.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		
		jp1.add(button,BorderLayout.NORTH);
		
		jp.add(jp1);
		jp.add(scrollpane,BorderLayout.SOUTH);
		
		
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int i=tab.getSelectedRow();
				if(i>=0){
					Petrinet nety = PetrinetFactory.clonePetrinet(netx);
					
					PetriNetDrawUtil.drawperformancenet(nety,tovisualize.getListperformance().get(i).getList(), tovisualize.getListperformance().get(i).getMaparc());
					remove(netPNView);
					remove(totalresult);
					 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, nety);
					 String  visualize= PetriNetDrawUtil.toHTMLfromMPP(tovisualize.getListperformance().get(i).getList());
					 totalresult = visualizestring( visualize);
					add (netPNView, "1, 1, 5, 1");
					add (totalresult, "3, 3      "); // Center
					revalidate();
					repaint();
				}
				
			}
		});
		
		return jp;

		
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
	
	


}
