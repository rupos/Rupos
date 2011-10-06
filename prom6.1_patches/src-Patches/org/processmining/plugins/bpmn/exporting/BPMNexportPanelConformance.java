package org.processmining.plugins.bpmn.exporting;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;


import javax.swing.JComponent;
import javax.swing.JFileChooser;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;


import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;

import org.processmining.framework.plugin.Progress;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;

import org.processmining.plugins.bpmn.TraslateBPMNResult;
import org.processmining.plugins.petrinet.replayfitness.conformance.LegendConformancePanel;
import org.processmining.plugins.petrinet.replayfitness.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replayfitness.util.LogViewInteractivePanel;
import org.processmining.plugins.petrinet.replayfitness.util.PetriNetDrawUtil;
import org.processmining.plugins.xpdl.Xpdl;
import org.processmining.plugins.xpdl.converter.BPMN2XPDLConversionExt;



public class BPMNexportPanelConformance extends JPanel{




	/**
	 * 
	 */

	private static final long serialVersionUID = -7258945029957912308L;
	private  ProMJGraphPanel netPNView;
	private  JComponent netBPMNView;
	
	private LegendConformancePanel legendInteractionPanel;
	private LogViewInteractivePanel logInteractionPanel;
	private TotalConformanceResult tovisualize;
	private UIPluginContext context;
	private Petrinet net;
	
	private TraslateBPMNResult trbpmn;
	private BPMNDiagramExt bpmn;
	private TabTraceConfPanel tabinteractivepanel;


	public BPMNexportPanelConformance(UIPluginContext c, Petrinet n,
			XLog log, Progress progress, BPMNexportResult export) {

		context=c;
		net=n;
		trbpmn=export.getTraslateBpmnresult();
		bpmn=export.getBPMNtraslate();
		
		 tovisualize = export.getTotalconformanceresult();
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawconformance(netx,tovisualize.getTotal());
		
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		legendInteractionPanel = new LegendConformancePanel(netPNView, "Legend");
		netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);

		
		netBPMNView= ProMJGraphVisualizer.instance().visualizeGraph(context, export.getBPMNtraslate());

		//JComponent logView = new LogViewUI(log);
	
		logInteractionPanel = new LogViewInteractivePanel(netPNView, log);
		netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);

		tabinteractivepanel = new TabTraceConfPanel(netPNView, "Trace_Sel", tovisualize, this);
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

		saveDialog.setSelectedFile(new File(bpmn.getLabel()+"Summary.xpdl")); 
		if (saveDialog.showSaveDialog(context.getGlobalContext().getUI()) == JFileChooser.APPROVE_OPTION) {
			File outFile = saveDialog.getSelectedFile();
			try {
				BufferedWriter outWriter = new BufferedWriter(new FileWriter(outFile));
				BPMN2XPDLConversionExt xpdlConversion = new BPMN2XPDLConversionExt(bpmn);
				Xpdl newxpdl = xpdlConversion.fills_layout(context);
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




	public void updateone(int i) {
		// TODO Auto-generated method stub
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawconformance(netx,tovisualize.getList().get(i));
		BPMNDiagramExt bpmnx = BPMNexportUtil.exportConformancetoBPMN(trbpmn, tovisualize.getList().get(i));
		remove(netPNView);
		remove(netBPMNView);
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);
		
		netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);
		netPNView.addViewInteractionPanel(tabinteractivepanel, SwingConstants.SOUTH);
		
		netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmnx);
		//add (netPNView, "1, 5, 5, 5");
		//add (netBPMNView, "1, 1, 5, 1");
		add(netBPMNView, "0, 0");
		
		add(netPNView, "0, 1");
		revalidate();
		repaint();
	}




	public void updateall() {
		// TODO Auto-generated method stub
		Petrinet netx = PetrinetFactory.clonePetrinet(net);
		PetriNetDrawUtil.drawconformance(netx,tovisualize.getTotal());
		remove(netPNView);
		remove(netBPMNView);
		netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, netx);
		netPNView.addViewInteractionPanel(legendInteractionPanel, SwingConstants.NORTH);
		netPNView.addViewInteractionPanel(logInteractionPanel, SwingConstants.SOUTH);
		netPNView.addViewInteractionPanel(tabinteractivepanel, SwingConstants.SOUTH);
		
		netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, bpmn);
		
		add(netBPMNView, "0, 0");
		
		add(netPNView, "0, 1");
		
		revalidate();
		repaint();
	}

	
	

}
