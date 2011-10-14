package org.processmining.plugins.petrinet.replay.conformance;

import info.clearthought.layout.TableLayout;


import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;


import org.processmining.plugins.petrinet.replay.util.LogViewInteractivePanel;
import org.processmining.plugins.petrinet.replay.util.PetriNetDrawUtil;
import org.processmining.plugins.petrinet.replay.util.StringInteractivePanel;



public class ReplayConformanceAnalysisPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -107889379484400541L;
	private  ProMJGraphPanel netPNView;

	private LegendConformancePanel legendInteractionPanel;
	private TotalConformanceResult tovisualize;
	private Petrinet net;
	private PluginContext context;
	private TabTraceConformancePanel tabpanel;
	private StringInteractivePanel stringpanel;
	private LogViewInteractivePanel logInteractionPanel;

	public ReplayConformanceAnalysisPanel(PluginContext c, Petrinet n,
			XLog log, Progress progress, TotalConformanceResult visualize) {
		
		tovisualize=visualize;
		net=n;
		context=c;

		inizialize(log);
	

	}

	

	private void inizialize(XLog log) {
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawconformance(netx,tovisualize.getTotal());
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);

		legendInteractionPanel = new LegendConformancePanel(netPNView, "Legend");
		netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.SOUTH);

		
		//JComponent logView = new LogViewUI(log);
		
		logInteractionPanel = new LogViewInteractivePanel(netPNView, log);
		netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);


		//JComponent totalresult = visualizestring( UItotalResult(tovisualize));
		//JComponent tab = tabtrace(tovisualize,net,context);

		  stringpanel = new StringInteractivePanel(netPNView, "Data_Result", UItotalResult(tovisualize));
		 netPNView.addViewInteractionPanel(stringpanel, SwingConstants.SOUTH);
			
		 tabpanel = new TabTraceConformancePanel(netPNView, "Change_Trace", tovisualize,this);
			netPNView.addViewInteractionPanel(tabpanel, SwingConstants.SOUTH);
			
		 
			double size[][] = { { TableLayout.FILL,1 }, { TableLayout.FILL,1  } };
			setLayout(new TableLayout(size));
			add(netPNView, "0, 0");
			
		//	add(logView, "0, 1");
		
		/*double border = 1;
		double size[][] =
		{{border, 1, 1, TableLayout.FILL, 1, 200, border},  // Columns
				{border, 300, 1, TableLayout.FILL, 1, 150, border}}; // Rows

		setLayout(new TableLayout(size));

		// Add
		add (netPNView, "1, 1, 5, 1"); // Top
		add (logView, "1, 5, 5, 5"); // Bottom
		
		//add (tab, "5, 3      "); // Right
		//add (totalresult, "3, 3      "); // Center
*/		
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

	
/*	public  JComponent tabtrace(final TotalConformanceResult tcr, final Petrinet net, final PluginContext context) {
		
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
				PetriNetDrawUtil.drawconformance(netx,tcr.getTotal());
				remove(netPNView);
				 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
				 netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);

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
					PetriNetDrawUtil.drawconformance(netx,tcr.getList().get(i));
					remove(netPNView);
					 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
					 netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);

					
					add (netPNView, "1, 1, 5, 1");
					revalidate();
					repaint();
				}
				
			}
		});
		//scrollpane.setViewportView(jp);
		// scrollpane.add(button,BorderLayout.SOUTH);
		return jp;///scrollpane;

	}*/



	public void onerepaint(int i) {
		// TODO Auto-generated method stub
		ConformanceResult result = tovisualize.getList().get(i);
		if(i>=0){
			Petrinet netx = PetrinetFactory.clonePetrinet(net);
			PetriNetDrawUtil.drawconformance(netx,result);
			remove(netPNView);
			 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
			 netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.SOUTH);
			 netPNView.addViewInteractionPanel(stringpanel, SwingConstants.SOUTH);
				netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);

			 netPNView.addViewInteractionPanel(tabpanel, SwingConstants.SOUTH);
				
			
			add (netPNView, "0,0");
			revalidate();
			repaint();
		}
		
		
	}



	public void fullrepaint() {
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawconformance(netx,tovisualize.getTotal());
		remove(netPNView);
		 netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		 netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.SOUTH);
		 netPNView.addViewInteractionPanel(stringpanel, SwingConstants.SOUTH);
			netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);

		 netPNView.addViewInteractionPanel(tabpanel, SwingConstants.SOUTH);
			
		 add (netPNView, "0,0");
		revalidate();
		repaint();
		
	}



	



}



