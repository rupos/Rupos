package org.processmining.plugins.bpmn.exporting;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


import javax.swing.JComponent;
import javax.swing.JFileChooser;

import javax.swing.JOptionPane;
import javax.swing.JPanel;



import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;


import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;

import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.plugins.bpmn.BPMNtoPNConnection;

import org.processmining.plugins.petrinet.replay.performance.LegendPerfomancePanel;
import org.processmining.plugins.petrinet.replay.performance.PerformanceResult;
import org.processmining.plugins.petrinet.replay.performance.TotalPerformanceResult;
import org.processmining.plugins.petrinet.replay.util.LogViewInteractivePanel;
import org.processmining.plugins.petrinet.replay.util.PetriNetDrawUtil;
import org.processmining.plugins.petrinet.replay.util.ReplayAnalysisConnection;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.converter.BPMN2XPDLConversionExt;



public class BPMNMeasuresPanelPerformance extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3531962030544060794L;
	
	private  ProMJGraphPanel netPNView;
	private  JComponent netBPMNView;
	private BPMNDiagramExt bpmnvisulizated;
	

	private LegendPerfomancePanel legendInteractionPanel;

	private LogViewInteractivePanel logInteractionPanel;

	private UIPluginContext context;

	private Petrinet net;

	private Map<String, Place> placeMap;

	private TabTracePerfPanel tabinteractivepanel;
	private BPMNDiagram bpmn;


	public BPMNMeasuresPanelPerformance(UIPluginContext c,
			TotalPerformanceResult resultc) {
		
		
		context=c;
		try {
			ReplayAnalysisConnection connection = context.getConnectionManager().getFirstConnection(
					ReplayAnalysisConnection.class, context, resultc);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			XLog log = connection.getObjectWithRole(ReplayAnalysisConnection.XLOG);
			 net= connection.getObjectWithRole(ReplayAnalysisConnection.PNET);
		
			BPMNtoPNConnection connection2 = context.getConnectionManager().getFirstConnection(
					BPMNtoPNConnection.class, context, net);

			// connection found. Create all necessary component to instantiate inactive visualization panel
			
		    bpmn = connection2.getObjectWithRole(BPMNtoPNConnection.BPMN);
		    placeMap = connection2.getObjectWithRole(BPMNtoPNConnection.MAPARCTOPLACE);
			
		    bpmnvisulizated = BPMNDecorateUtil.exportPerformancetoBPMN(bpmn,  resultc.getListperformance().get(0), placeMap,net);
			
			 
				
				init(log,resultc);

		} catch (ConnectionCannotBeObtained e) {
			// No connections available
			context.log("Connection does not exist", MessageLevel.DEBUG);
			
		}
	}

	

	

	private void init(XLog log,TotalPerformanceResult tovisualize) {
		
	
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawperformancenet(netx, tovisualize.getListperformance().get(0).getList(), tovisualize.getListperformance().get(0).getMaparc());
		
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);

		legendInteractionPanel = new LegendPerfomancePanel(netPNView, "Legend");
		netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);

		
		netBPMNView= ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnvisulizated);

		//JComponent logView = new LogViewUI(log);
		
		logInteractionPanel = new LogViewInteractivePanel(netPNView, log);
		netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);
		
		tabinteractivepanel = new TabTracePerfPanel(netPNView, "Trace_Sel", tovisualize, this);
		netPNView.addViewInteractionPanel(tabinteractivepanel, SwingConstants.SOUTH);
		
		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL,  TableLayoutConstants.FILL} };
		setLayout(new TableLayout(size));
		
		add(netBPMNView, "0, 0");
		
		add(netPNView, "0, 1");

		
	}



	


	public void savefile() {
		// TODO Auto-generated method stub
		JFileChooser saveDialog = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "XPDL", "xpdl");
		saveDialog.setFileFilter(filter);

		saveDialog.setSelectedFile(new File(bpmnvisulizated.getLabel()+"Summary.xpdl")); 
		if (saveDialog.showSaveDialog(context.getGlobalContext().getUI()) == JFileChooser.APPROVE_OPTION) {
			File outFile = saveDialog.getSelectedFile();
			try {
				BufferedWriter outWriter = new BufferedWriter(new FileWriter(outFile));
				Xpdl newxpdl=null;
				try {
					BPMN2XPDLConversionExt xpdlConversion = new BPMN2XPDLConversionExt(bpmnvisulizated);
					newxpdl = xpdlConversion.fills_layout(context);
				
				} catch (Exception e1) {
					
					//e1.printStackTrace();
					
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



	public void repainone(PerformanceResult performanceResult) {
		// TODO Auto-generated method stub
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		
		PetriNetDrawUtil.drawperformancenet(netx, performanceResult.getList(), performanceResult.getMaparc());
		bpmnvisulizated =	BPMNDecorateUtil.exportPerformancetoBPMN(bpmn, performanceResult,placeMap,net);
		
		remove(netPNView);
		remove(netBPMNView);
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);
		netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);
		netPNView.addViewInteractionPanel(tabinteractivepanel, SwingConstants.SOUTH);
		
		netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnvisulizated);
		//add (netPNView, "1, 5, 5, 5");
		//add (netBPMNView, "1, 1, 5, 1");
		add(netBPMNView, "0, 0");
		
		add(netPNView, "0, 1");
		
		revalidate();
		repaint();
	}

}
