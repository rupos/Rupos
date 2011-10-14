package org.processmining.plugins.petrinet.replay.util;




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

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;


import org.processmining.framework.util.ui.scalableview.ScalableComponent;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.framework.util.ui.scalableview.interaction.ViewInteractionPanel;





import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class StringInteractivePanel extends JPanel implements MouseListener, MouseMotionListener, ViewInteractionPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	
	
	
	protected SlickerFactory factory = SlickerFactory.instance();
	protected SlickerDecorator decorator = SlickerDecorator.instance();
	private JComponent component;
	private JScrollPane sp;
	private String panelName;
	private String tovisualize;
	
	
	
	
	public StringInteractivePanel(ScalableViewPanel panel, String panelName,
			String tpr ){
		super(new BorderLayout());

		this.setBorder(BorderFactory.createEmptyBorder());
		this.setOpaque(true);
		
		this.setSize(new Dimension(160, 640));

		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		panel.getViewport();
		this.panelName = panelName;
		this.tovisualize=tpr;
		
		
		painttabtrace();
	}
	

	private void painttabtrace() {
		// TODO Auto-generated method stub
		this.setBackground(new Color(30, 30, 30));
		
		//JPanel legendPanel = new JPanel();
		//legendPanel.setBorder(BorderFactory.createEmptyBorder());
		//legendPanel.setSize(60, 50);

		JLabel l = new JLabel(tovisualize);
		//l.setPreferredSize(new Dimension(840,160));
			 sp = new JScrollPane(l);
			
		sp.setBorder(BorderFactory.createEmptyBorder());
		sp.setBackground(new Color(30, 30, 30));
		
	
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
		
		//sp.setLayout(new BoxLayout(sp,BoxLayout.Y_AXIS));
		
		//sp.setViewportView(l);
		

		//this.setLayout(layout);
		
		//legendPanel.add(l,BorderLayout.SOUTH);
		//legendPanel.getcon
		//legendPanel.setSize(150,550);
		sp.setPreferredSize(new Dimension(640,160));
		//this.setPreferredSize(new Dimension(640,160));
		//this.add(l, BorderLayout.CENTER);
		this.add(sp, BorderLayout.WEST);
		this.setOpaque(false);

	
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
		return SwingConstants.SOUTH;
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
		return 160;
	}

	public double getWidthInView() {
		if(sp.getWidth()>640){
			return 640;
		}
		return 640;//sp.getWidth();
	}

	public void willChangeVisibility(boolean to) {
		
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
	}
}
