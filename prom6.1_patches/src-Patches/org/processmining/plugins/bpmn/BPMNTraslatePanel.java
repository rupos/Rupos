package org.processmining.plugins.bpmn;


import info.clearthought.layout.TableLayoutConstants;
import info.clearthought.layout.TableLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.jgraph.ProMJGraphVisualizer;

public class BPMNTraslatePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1078379484400548931L;
	public BPMNTraslatePanel(PluginContext context,Progress progress, TraslateBPMNResult result) {

		JComponent netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, result.getPetri());
		JComponent netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, result.getBpmn());
		
		
		

		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL,  TableLayoutConstants.FILL } };
		setLayout(new TableLayout(size));
		
		add(netBPMNView, "0, 0");
		
		add(netPNView, "0, 1");
		
		
	}

}
