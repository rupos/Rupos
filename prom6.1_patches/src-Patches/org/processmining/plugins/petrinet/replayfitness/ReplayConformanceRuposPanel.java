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
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.plugins.log.ui.logdialog.LogViewUI;
import com.fluxicon.slickerbox.components.AutoFocusButton;
import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class ReplayConformanceRuposPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -107889379484400541L;
	private  JComponent netPNView;
	private JTable tab;

	public ReplayConformanceRuposPanel(PluginContext context, Petrinet net,
			XLog log, Progress progress, TotalConformanceResult tovisualize) {

		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawfitnessnet(netx,tovisualize.getTotal());
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);

		JComponent logView = new LogViewUI(log);

		JComponent totalresult = visualizestring( UItotalResult(tovisualize));
		JComponent tab = tabtrace(tovisualize,net,context);

		
		double border = 1;
		double size[][] =
		{{border, 1, 1, TableLayout.FILL, 1, 200, border},  // Columns
				{border, 300, 1, TableLayout.FILL, 1, 150, border}}; // Rows

		setLayout(new TableLayout(size));

		// Add
		add (netPNView, "1, 1, 5, 1"); // Top
		add (logView, "1, 5, 5, 5"); // Bottom
		
		add (tab, "5, 3      "); // Right
		add (totalresult, "3, 3      "); // Center
	

	}

	

	private String UItotalResult(TotalConformanceResult result){

		return "<html>"+this.toHTMLfromFRT(result)+"</html>";
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
		/*JTextPane summaryPane = new JTextPane();
		summaryPane = new JTextPane();
		summaryPane.setBorder(BorderFactory.createEmptyBorder());
		summaryPane.setContentType("text/html");
		// pre-populate the text pane with some teaser message
		summaryPane.setText("<html><body bgcolor=\"#888888\" text=\"#333333\">"
				+ "<br><br><br><br><br><center><font face=\"helvetica,arial,sans-serif\" size=\"4\">"
				+ "Please wait while the summary is created...</font></center></body></html>");
		summaryPane.setEditable(false);
		summaryPane.setCaretPosition(0);
		JScrollPane scrollPane = new JScrollPane(summaryPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


		summaryPane.*/
		JLabel l = new JLabel(tovisualize);
		sp.setViewportView(l);

		return sp;
	}
	public  JComponent tabtrace(final TotalConformanceResult tcr, final Petrinet net, final PluginContext context) {
		
		JPanel jp = new JPanel();
		JPanel jp1 = new JPanel();
		 tab = new JTable(new AbstractTableModel() {
		
			private static final long serialVersionUID = -2176731961693608635L;

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
		JButton	button2  = new AutoFocusButton("UpdateAll");

		button.setOpaque(false);
		button2.setOpaque(false);
		
		button.setFont(new Font("Monospaced", Font.PLAIN, 12));
		button2.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		jp1.add(button,BorderLayout.NORTH);
		jp1.add(button2,BorderLayout.WEST);
		jp.add(jp1);
		jp.add(scrollpane,BorderLayout.SOUTH);
		
		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Petrinet netx = PetrinetFactory.clonePetrinet(net);
				PetriNetDrawUtil.drawfitnessnet(netx,tcr.getTotal());
				remove(netPNView);
				 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
				add (netPNView, "1, 1, 5, 1");
				revalidate();
				repaint();
				
			}
			
		});
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int i=tab.getSelectedRow();
				if(i>=0){
					Petrinet netx = PetrinetFactory.clonePetrinet(net);
					PetriNetDrawUtil.drawfitnessnet(netx,tcr.getList().get(i));
					remove(netPNView);
					 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);

					
					add (netPNView, "1, 1, 5, 1");
					revalidate();
					repaint();
				}
				
			}
		});
		//scrollpane.setViewportView(jp);
		// scrollpane.add(button,BorderLayout.SOUTH);
		return jp;///scrollpane;

	}



	



}



