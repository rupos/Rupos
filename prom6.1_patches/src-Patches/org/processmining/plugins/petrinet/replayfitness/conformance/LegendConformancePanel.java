package org.processmining.plugins.petrinet.replayfitness.conformance;


import info.clearthought.layout.TableLayout;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.processmining.framework.util.ui.scalableview.ScalableComponent;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.framework.util.ui.scalableview.interaction.ViewInteractionPanel;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class LegendConformancePanel extends JPanel implements MouseListener, MouseMotionListener, ViewInteractionPanel {

	private static final long serialVersionUID = 5563202352636336868L;

	protected SlickerFactory factory = SlickerFactory.instance();
	protected SlickerDecorator decorator = SlickerDecorator.instance();
	private JComponent component;
	private String panelName;

	public LegendConformancePanel(ScalableViewPanel panel, String panelName) {
		super(new BorderLayout());

		this.setBorder(BorderFactory.createEmptyBorder());
		this.setOpaque(true);
		this.setSize(new Dimension(90, 250));

		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		panel.getViewport();
		this.panelName = panelName;
		paintLegend();
	}

	public synchronized void paintLegend() {
		this.setBackground(new Color(30, 30, 30));

		JPanel legendPanel = new JPanel();
		legendPanel.setBorder(BorderFactory.createEmptyBorder());
		legendPanel.setBackground(new Color(30, 30, 30));
		TableLayout layout = new TableLayout(new double[][] { { 0.10, TableLayout.FILL }, {} });
		legendPanel.setLayout(layout);

		layout.insertRow(0, 0.2);

		int row = 1;

		layout.insertRow(row, TableLayout.PREFERRED);
		JLabel legend = factory.createLabel("LEGEND");
		legend.setForeground(Color.WHITE);
		legendPanel.add(legend, "0,1,1,1,c, c");
		row++;

		layout.insertRow(row, 0.2);

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel greenPanel = new JPanel();
		greenPanel.setBackground(Color.RED);
		legendPanel.add(greenPanel, "0," + row + ",r, c");
		JLabel syncLbl = factory.createLabel(" Remaining or produced token");
		syncLbl.setForeground(Color.WHITE);
		legendPanel.add(syncLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel yellowPanel = new JPanel();
		yellowPanel.setBackground(Color.ORANGE);
		legendPanel.add(yellowPanel, "0," + row + ",r, c");
		JLabel moveLogLbl = factory.createLabel(" Transition force enabled");
		moveLogLbl.setForeground(Color.WHITE);
		legendPanel.add(moveLogLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel redPanel = new JPanel();
		redPanel.setBackground(Color.WHITE);
		legendPanel.add(redPanel, "0," + row + ",r, c");
		JLabel moveViolLbl = factory.createLabel(" Transition or place witout problem");
		moveViolLbl.setForeground(Color.WHITE);
		legendPanel.add(moveViolLbl, "1," + row++ + ",l, c");
		
		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel grayPanel = new JPanel();
		grayPanel.setBackground(Color.BLACK);
		legendPanel.add(grayPanel, "0," + row + ",r, c");
		JLabel grayLbl = factory.createLabel(" Invisible transition");
		grayLbl.setForeground(Color.WHITE);
		legendPanel.add(grayLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, 0.2);

		legendPanel.setOpaque(false);
		this.add(legendPanel, BorderLayout.WEST);
		this.setOpaque(false);

	
		this.setOpaque(false);
	}

	public double getVisWidth() {
		return component.getSize().getWidth();
	}

	public double getVisHeight() {
		return component.getSize().getHeight();
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);
	}

	public synchronized void mouseDragged(MouseEvent evt) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public synchronized void mousePressed(MouseEvent e) {

	}

	public synchronized void mouseReleased(MouseEvent e) {

	}

	public void setScalableComponent(ScalableComponent scalable) {
		this.component = scalable.getComponent();
	}

	public void setParent(ScalableViewPanel parent) {

	}

	public JComponent getComponent() {
		return this;
	}

	public int getPosition() {
		return SwingConstants.NORTH;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String name) {
		this.panelName = name;
	}

	public void updated() {
		
	}

	public double getHeightInView() {
		return 90;
	}

	public double getWidthInView() {
		return 250;
	}

	public void willChangeVisibility(boolean to) {
		
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
	}

}
