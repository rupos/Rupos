package org.processmining.plugins.bpmn;


import java.awt.Color;
import java.awt.Label;

import info.clearthought.layout.TableLayoutConstants;
import info.clearthought.layout.TableLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.jgraph.ProMJGraphVisualizer;

import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class BPMNTraslatePanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1078379484400548931L;
	public BPMNTraslatePanel(PluginContext context,Progress progress, TraslateBPMNResult result) {

		JComponent netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, result.getPetri());
		JComponent netBPMNView = ProMJGraphVisualizer.instance().visualizeGraph(context, result.getBpmn());
		
		JComponent ErrorView = visualizestring("<html>"+result.getError().toString()+"</html>");
		

		double size[][] = { { TableLayoutConstants.FILL }, { TableLayoutConstants.FILL,  TableLayoutConstants.FILL,150} };
		setLayout(new TableLayout(size));
		
		add(netBPMNView, "0, 0");
		
		add(netPNView, "0, 1");
		add(ErrorView, "0, 2");
		
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
		
		
		JLabel l = new JLabel(tovisualize);
		sp.setViewportView(l);

		return sp;
	}

}
