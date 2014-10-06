/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.data;

import java.net.InetAddress;

/**
 * @author chris
 * Mar 30, 2005
 * Packet
 */
public class IPPacket extends AbstractPacket {
	public InetAddress srcip;
	public InetAddress dstip;
	
	public IPPacket( InetAddress srcip, InetAddress dstip, AbstractPacket pdu, int length ) {
		this.srcip = srcip;
		this.dstip = dstip;
		this.pdu = pdu;
		this.length = length;
	}
	
	public String toString() {
		return( "IP [ SRC="+srcip+" DST="+dstip+" LEN="+length+" ] "+pdu );
	}
}
