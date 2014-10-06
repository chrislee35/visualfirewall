/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.data;

/**
 * @author chris
 * Mar 30, 2005
 * TCPPacket
 */
public class TCPPacket extends AbstractPacket {
	public int srcport;
	public int dstport;
	public int flags;
	public TCPPacket ( int srcport, int dstport, int flags, int length ) {
		this.srcport = srcport;
		this.dstport = dstport;
		this.flags = flags;
		this.length = length;
	}
	public String toString() {
		return( "TCP [ SPT="+srcport+" DPT="+dstport+" LEN="+length+" ] "+pdu );
	}
}
