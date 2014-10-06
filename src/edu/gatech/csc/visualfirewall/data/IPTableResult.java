/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.data;

import java.util.Date;

/**
 * @author chris
 * Mar 30, 2005
 * IPTableResult
 */
public class IPTableResult {
	public Date timestamp;
	public boolean accepted;
	public IPPacket packet;
	
	public IPTableResult( Date timestamp, boolean accepted, IPPacket packet ) {
		this.timestamp = timestamp;
		this.accepted = accepted;
		this.packet = packet;
	}
	
	public String toString() {
		return(timestamp+" "+((accepted)?"ACCEPT ":"DENY ") + packet );
	}
}
