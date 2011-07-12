
package org.processmining.plugins.petrinet.replayfitness;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.util.StringVisualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.jgraph.ProMJGraphVisualizer;
import org.processmining.plugins.log.ui.logdialog.LogViewUI;

import com.fluxicon.slickerbox.factory.SlickerDecorator;

public class ReplayPerformanceRuposPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -107889379484400541L;

	public ReplayPerformanceRuposPanel(PluginContext context, Petrinet net,
			XLog log, Progress progress, String tovisualize) {
		
		JComponent netPNView = ProMJGraphVisualizer.instance().visualizeGraph(context, net);
		
		JComponent logView = new LogViewUI(log);
		
		JComponent totalresult = visualizestring( tovisualize);
		

		double size[][] = { { TableLayoutConstants.FILL,200 }, { TableLayoutConstants.FILL,  TableLayoutConstants.FILL} };
		setLayout(new TableLayout(size));
		
		add(netPNView, "0, 0");
		
		add(totalresult, "0, 1");
		add(logView, "1, 1");
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
