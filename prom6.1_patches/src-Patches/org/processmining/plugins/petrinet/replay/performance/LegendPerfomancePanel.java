package org.processmining.plugins.petrinet.replay.performance;

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

public class LegendPerfomancePanel extends JPanel implements MouseListener, MouseMotionListener, ViewInteractionPanel {

	private static final long serialVersionUID = 5563202305263696868L;

	protected SlickerFactory factory = SlickerFactory.instance();
	protected SlickerDecorator decorator = SlickerDecorator.instance();
	private JComponent component;
	private String panelName;

	public LegendPerfomancePanel(ScalableViewPanel panel, String panelName) {
		super(new BorderLayout());

		this.setBorder(BorderFactory.createEmptyBorder());
		this.setOpaque(true);
		this.setSize(new Dimension(90, 150));

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
		greenPanel.setBackground(Color.cyan);
		legendPanel.add(greenPanel, "0," + row + ",r, c");
		JLabel syncLbl = factory.createLabel(" Wait Time Value");
		syncLbl.setForeground(Color.WHITE);
		legendPanel.add(syncLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel yellowPanel = new JPanel();
		yellowPanel.setBackground(Color.GREEN);
		legendPanel.add(yellowPanel, "0," + row + ",r, c");
		JLabel moveLogLbl = factory.createLabel(" Sync Time Value");
		moveLogLbl.setForeground(Color.WHITE);
		legendPanel.add(moveLogLbl, "1," + row++ + ",l, c");

		layout.insertRow(row, TableLayout.PREFERRED);
		JPanel redPanel = new JPanel();
		redPanel.setBackground(Color.WHITE);
		legendPanel.add(redPanel, "0," + row + ",r, c");
		JLabel moveViolLbl = factory.createLabel(" No Sync o Wait Time");
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

	/*	JPanel picPanel = new JPanel() {
			*//**
			 * 
			 *//*
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g; // Downcast to Graphics2D

				int PADDINGFROMBOX = 5;

				int VERTICALBOXWIDTH = 20;
				int VERTICALBOXHEIGHT = 60;

				int HORIZONTALBOXWIDTH = 140;
				int HORIZONTALBOXHEIGHT = 10;

				int PADDINGFROMBOXTOTEXT = 3;
				int VERTICALPADDINGFROMBOXTOTEXT = 7;

				double x = 20;
				double y = 20;
				double width = 160;
				double height = 80;
				
				
				
				g2d.setFont(new Font(g2d.getFont().getFamily(), g2d.getFont().getStyle(), 10));
				FontMetrics fm = g2d.getFontMetrics();

				// fill in the semantic box
				GeneralPath path = new GeneralPath();

				// general stroke
				BasicStroke stroke = new BasicStroke((float) 1.0);
				g2d.setStroke(stroke);
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape activityShape = new RoundRectangle2D.Double(x, y, width, height,15,15);
				path.reset();
				path.append(activityShape, false);
				g2d.fill(path);
				
				g2d.setColor(Color.LIGHT_GRAY);
				java.awt.Shape activityShape2 =new RoundRectangle2D.Double(x+1, y+1, width-2, height-2, 15,15);
				
				path.reset();
				path.append(activityShape2, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));

				double xForSynchTimeBox = x + PADDINGFROMBOXTOTEXT;
				double yForSynchTimeBox = y + (2 * VERTICALPADDINGFROMBOXTOTEXT);
				java.awt.Shape synchTimeShape = new Rectangle2D.Double(xForSynchTimeBox, yForSynchTimeBox, VERTICALBOXWIDTH, VERTICALBOXHEIGHT);
				path.reset();
				path.append(synchTimeShape, false);
				g2d.fill(path);
				
				g2d.setColor(Color.RED);
				java.awt.Shape synchTimeSemanticShape = new Rectangle2D.Double(xForSynchTimeBox, yForSynchTimeBox+ VERTICALBOXHEIGHT/2, VERTICALBOXWIDTH, VERTICALBOXHEIGHT/2);
				path.reset();
				path.append(synchTimeSemanticShape, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape synchTimeArc = new Rectangle2D.Double(xForSynchTimeBox +VERTICALBOXWIDTH/2, yForSynchTimeBox + VERTICALBOXHEIGHT, 1, 20);
				path.reset();
				path.append(synchTimeArc, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				String text = "Synchronization Time is relatively high";
				g2d.drawString(text, Math.round(xForSynchTimeBox)+185- fm.stringWidth(text), Math.round(yForSynchTimeBox+VERTICALBOXHEIGHT + 25));
				
				
				
				double xForTotalTimeBox = (xForSynchTimeBox == x) ? xForSynchTimeBox + PADDINGFROMBOX : (xForSynchTimeBox + PADDINGFROMBOX + VERTICALBOXWIDTH);
				double yForTotalTimeBox = y + height - 2 * HORIZONTALBOXHEIGHT - PADDINGFROMBOX;
				double totalTimeShapeWidth = HORIZONTALBOXWIDTH - ((xForSynchTimeBox == x) ? 0 : (xForSynchTimeBox + PADDINGFROMBOX + VERTICALBOXWIDTH));
				double waitingTimeShapeWidth = 0;
				waitingTimeShapeWidth = 30;
				double workingTimeShapeWidth = totalTimeShapeWidth - waitingTimeShapeWidth;

				// total time box
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape totalTimeShape = new Rectangle2D.Double(xForTotalTimeBox, yForTotalTimeBox, totalTimeShapeWidth, HORIZONTALBOXHEIGHT);
				path.reset();
				path.append(totalTimeShape, false);
				g2d.fill(path);

				// waiting time box
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape waitingTimeShape = new Rectangle2D.Double(xForTotalTimeBox, yForTotalTimeBox + HORIZONTALBOXHEIGHT, waitingTimeShapeWidth, HORIZONTALBOXHEIGHT);
				path.reset();
				path.append(waitingTimeShape, false);
				g2d.fill(path);

				// working time box
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape workingTimeShape = new Rectangle2D.Double(xForTotalTimeBox + waitingTimeShapeWidth, yForTotalTimeBox + HORIZONTALBOXHEIGHT, workingTimeShapeWidth,
						HORIZONTALBOXHEIGHT);
				path.reset();
				path.append(workingTimeShape, false);
				g2d.fill(path);

				// Total Time semantics
				g2d.setColor(Color.RED);
				java.awt.Shape totalTimeSemanticShape = new Rectangle2D.Double(xForTotalTimeBox, yForTotalTimeBox, totalTimeShapeWidth, HORIZONTALBOXHEIGHT - 1);
				path.reset();
				path.append(totalTimeSemanticShape, false);
				g2d.fill(path);

				g2d.setColor(new Color(50, 50, 50));
				text = "16.56 h";
				g2d.drawString(text, Math.round(xForTotalTimeBox + totalTimeShapeWidth - (PADDINGFROMBOXTOTEXT) - fm.stringWidth(text)), Math.round(yForTotalTimeBox) - 1);
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape totalTimeArc = new Rectangle2D.Double(xForTotalTimeBox +totalTimeShapeWidth, yForTotalTimeBox+5, 50, 1);
				path.reset();
				path.append(totalTimeArc, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				text = "Total Time is relatively high";
				g2d.drawString(text, Math.round(xForTotalTimeBox +totalTimeShapeWidth+ yForTotalTimeBox)+110- fm.stringWidth(text), Math.round(yForTotalTimeBox+8) - 1);
				
				// Waiting Time semantics
				g2d.setColor(Color.YELLOW);
				java.awt.Shape waitingTimeSemanticShape = new Rectangle2D.Double(xForTotalTimeBox, yForTotalTimeBox + HORIZONTALBOXHEIGHT + 1, waitingTimeShapeWidth - 1,
						HORIZONTALBOXHEIGHT);
				path.reset();
				path.append(waitingTimeSemanticShape, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape waitingTimeArc = new Rectangle2D.Double(xForTotalTimeBox +waitingTimeShapeWidth/2, yForTotalTimeBox + HORIZONTALBOXHEIGHT + 10, 1, 35);
				path.reset();
				path.append(waitingTimeArc, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				text = "Waiting Time is relatively moderate";
				g2d.drawString(text, Math.round(xForTotalTimeBox)+150- fm.stringWidth(text), Math.round(yForTotalTimeBox+HORIZONTALBOXHEIGHT + 50));
				
				
				// Working Time semantics
				g2d.setColor(Color.GREEN);
				java.awt.Shape workingTimeSemanticShape = new Rectangle2D.Double(xForTotalTimeBox + waitingTimeShapeWidth + 1, yForTotalTimeBox + HORIZONTALBOXHEIGHT + 1,
						workingTimeShapeWidth, HORIZONTALBOXHEIGHT);
				path.reset();
				path.append(workingTimeSemanticShape, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape workingTimeArc = new Rectangle2D.Double(xForTotalTimeBox +totalTimeShapeWidth, yForTotalTimeBox+5+ HORIZONTALBOXHEIGHT + 1, 50, 1);
				path.reset();
				path.append(workingTimeArc, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				text = "Working Time is relatively low";
				g2d.drawString(text, Math.round(xForTotalTimeBox +totalTimeShapeWidth+ yForTotalTimeBox)+120- fm.stringWidth(text), Math.round(yForTotalTimeBox+HORIZONTALBOXHEIGHT + 10) - 1);
				
				
				g2d.setColor(new Color(50, 50, 50));

				text = "10.45 h";
				g2d.drawString(text, Math.round(xForTotalTimeBox), Math.round(yForTotalTimeBox + 2 * HORIZONTALBOXHEIGHT) - 1);

				g2d.setColor(new Color(50, 50, 50));

				text = "6.11 h";
				g2d.drawString(text, Math.round(xForTotalTimeBox + totalTimeShapeWidth - fm.stringWidth(text)), Math.round(yForTotalTimeBox + 2 * HORIZONTALBOXHEIGHT) - 1);

				*//**
				 * Draw frequency the node 
				 *//*
				g2d.setColor(Color.BLACK);

				text = String.valueOf("#138");
				g2d.drawString(text, Math.round(x + width - PADDINGFROMBOX - fm.stringWidth(text)) - 5, Math.round(y + 15));
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape frequencyArc = new Rectangle2D.Double(x + width-10, y + 12, 30, 1);
				path.reset();
				path.append(frequencyArc, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				text = "Frequency";
				g2d.drawString(text, Math.round(x + width+70- fm.stringWidth(text)), Math.round(y + 15));
				
				*//**
				 * Draw frequency the node 
				 *//*
				g2d.setColor(Color.BLACK);

				text = String.valueOf("%100");
				g2d.drawString(text, Math.round(x + 2*PADDINGFROMBOX+VERTICALBOXWIDTH), Math.round(y + 15));
				
				g2d.setColor(new Color(50, 50, 50));
				java.awt.Shape percentageArc = new Rectangle2D.Double(x + 2*PADDINGFROMBOX+VERTICALBOXWIDTH +fm.stringWidth(text)/2, y-10, 1, 18);
				path.reset();
				path.append(percentageArc, false);
				g2d.fill(path);
				
				g2d.setColor(new Color(50, 50, 50));
				text = "Percentage/Probability";
				g2d.drawString(text, Math.round(x), Math.round(y - 12));
				
				
				final int labelX = (int) Math.round(x + 30);
				final int labelY = (int) Math.round(y + 10);
				final int labelW = (int) Math.round(width - 10);
				final int labelH = (int) Math.round(height - 40);

				JLabel label = new JLabel("Sample Activity");
				label.setPreferredSize(new Dimension(labelW, labelH));
				label.setSize(new Dimension(labelW, labelH));

				label.setFont(new Font(label.getFont().getFamily(), label.getFont().getStyle(), 12));
				label.validate();
				label.paint(g2d.create(labelX, labelY, labelW, labelH));

			}
		};

		picPanel.setOpaque(false);
		this.add(picPanel, BorderLayout.CENTER);*/
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
		return 150;
	}

	public void willChangeVisibility(boolean to) {
		
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
	}

}
