/*
 * Created on Apr 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import edu.gatech.csc.visualfirewall.VisualFirewall;

/**
 * @author trost
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VFW_WindowListener implements WindowListener {

	VisualFirewall parent;

	/**
	 * 
	 */
	public VFW_WindowListener(VisualFirewall parent) {
		super();
		
		this.parent = parent;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		parent.jSplitPane.setDividerLocation(parent.jSplitPane.getDividerLocation());
		parent.jSplitPane.updateUI();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent e) {
		parent.jSplitPane.setDividerLocation(parent.jSplitPane.getDividerLocation());
		parent.jSplitPane.updateUI();

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
		parent.jSplitPane.setDividerLocation(parent.jSplitPane.getDividerLocation());
		parent.jSplitPane.updateUI();

	}

}
