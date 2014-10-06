/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;

import edu.gatech.csc.visualfirewall.VisualFirewall;

import org.jfree.chart.JFreeChart;

/**
 * @author chris
 * Mar 31, 2005
 * VFW_MouseListener
 */
public class VFW_MouseListener implements MouseListener, ChartMouseListener {
	
	VisualFirewall parent;

	public VFW_MouseListener ( VisualFirewall parent ) {
		this.parent = parent;
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		Component c = e.getComponent();
		
		if(VisualFirewall.needBounds)
		{
			VisualFirewall.needBounds = false;
			
			for(int x = 0; x < VisualFirewall.NUMVIEWS; ++x)
			{
				parent.bounds[x] = parent.canvases[x].getBounds();
			}
		}
		
		for(int x = 1; x < VisualFirewall.NUMVIEWS; ++x)
		{
			if(c == (Component) parent.canvases[x])
			{											
				parent.sideJPanel.removeAll();
				parent.mainJPanel.removeAll();
				
				int tmp = parent.perm[0];
				parent.perm[0] = parent.perm[x];
				parent.perm[x] = tmp;
				
				Component tmpCanvas = parent.canvases[0];
				parent.canvases[0] = parent.canvases[x];
				parent.canvases[x] = tmpCanvas;
				
				for (int a = 0; a < VisualFirewall.NUMVIEWS; ++a)
				{	
					parent.canvases[a].setBounds( parent.bounds[a] );
					
					if(a == 0)
					{
						parent.mainJPanel.add(parent.canvases[a], BorderLayout.CENTER);
					}
					else
					{
						parent.sideJPanel.add( parent.canvases[a] );
					}
				}
				
				for(int i = 0; i < VisualFirewall.NUMVIEWS; ++i)
				{
					if(parent.canvases[0].equals(parent.views[i].getCanvas()))
					{
						parent.views[i].isMaximized = true;
					}
					else
					{
						parent.views[i].isMaximized = false;
					}
				}
				
				parent.jSplitPane.setDividerLocation(parent.jSplitPane.getDividerLocation());
				parent.jSplitPane.updateUI();
				break;
			}
		} // end for()

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseEntered(e);
	}
	
	public void chartMouseClicked(ChartMouseEvent event)
	{
		JFreeChart chart = event.getChart();
						
		for(int x = 1; x < VisualFirewall.NUMVIEWS; ++x)
		{
			if( parent.views[x].chart != null && 
			    ( chart == parent.views[x].chart ||
			      chart.equals( parent.views[x].chart) ) )
			{
				mouseClicked(new MouseEvent(parent.views[x].getCanvas(), 0,0,0,0,0,0, false));
				break;
			}
		} // end for()
	}

	public void chartMouseMoved(ChartMouseEvent event)
	{
		
	}

	public static void main(String[] args) {
	}
}
