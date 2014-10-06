/*
 * Created on Apr 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.data.listener;

import java.util.EventListener;

import edu.gatech.csc.visualfirewall.data.AbstractPacket;

/**
 * @author chris
 * Apr 1, 2005
 * AbstractPacketListener
 */
public interface AbstractPacketListener extends EventListener {
	public void dispatchPacket( AbstractPacket packet );
}
