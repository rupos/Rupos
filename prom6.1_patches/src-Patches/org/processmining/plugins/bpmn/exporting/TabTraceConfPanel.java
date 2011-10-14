package org.processmining.plugins.bpmn.exporting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;



import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;


import javax.swing.table.AbstractTableModel;

import org.processmining.framework.util.ui.scalableview.ScalableComponent;
import org.processmining.framework.util.ui.scalableview.ScalableViewPanel;
import org.processmining.framework.util.ui.scalableview.interaction.ViewInteractionPanel;


import org.processmining.plugins.petrinet.replay.conformance.TotalConformanceResult;



import com.fluxicon.slickerbox.components.AutoFocusButton;
import com.fluxicon.slickerbox.components.SlickerButton;
import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class TabTraceConfPanel extends JPanel implements MouseListener, MouseMotionListener, ViewInteractionPanel {
	
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
	private JTable tab;
	private String panelName;
	private TotalConformanceResult TCR;
	private BPMNMeasuresPanelConformance panel;
	
	
	public TabTraceConfPanel(ScalableViewPanel panels, String panelName, TotalConformanceResult tcr, BPMNMeasuresPanelConformance bpanel){
		super(new BorderLayout());

		this.setBorder(BorderFactory.createEmptyBorder());
		this.setOpaque(true);
		this.setSize(new Dimension(160, 260));

		this.addMouseMotionListener(this);
		this.addMouseListener(this);

		panels.getViewport();
		this.panelName = panelName;
		TCR=tcr;
		panel=bpanel;
		painttabtrace();
	}
	

	private void painttabtrace() {
		// TODO Auto-generated method stub
		this.setBackground(new Color(30, 30, 30));

		JPanel jp = new JPanel();
		JPanel jp1 = new JPanel();
		tab = new JTable(new AbstractTableModel() {


			private static final long serialVersionUID = -3512537393802566662L;
	

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return TCR.getList().get(rowIndex).getTracename();//rowIndex;
			}

			@Override
			public int getRowCount() {

				return TCR.getList().size();
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
		jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));
		jp1.setLayout(new BoxLayout(jp1, BoxLayout.Y_AXIS));
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



		JButton update  = new AutoFocusButton("Update");
		JButton	updateall  = new AutoFocusButton("UpdateAll");
		JButton saveButton = new SlickerButton("save as BPMN...");
		
		update.setOpaque(false);
		updateall.setOpaque(false);
		saveButton.setOpaque(false);
		
		update.setFont(new Font("Monospaced", Font.PLAIN, 12));
		updateall.setFont(new Font("Monospaced", Font.PLAIN, 12));
		saveButton.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.savefile();
			}
		});

		jp.add(update,BorderLayout.NORTH);
		jp.add(updateall,BorderLayout.WEST);
		jp.add(saveButton,BorderLayout.EAST);
		
		jp1.add(jp);
		jp1.add(scrollpane,BorderLayout.SOUTH);

		updateall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				panel.updateall();

			}

		});

		update.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i=tab.getSelectedRow();
				if(i>=0){
					panel.updateone(i);
				}

			}
		});
		
		
		
		this.add(jp1, BorderLayout.WEST);
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
		return tab.getWidth();
	}

	public void willChangeVisibility(boolean to) {
		
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
	}
}
