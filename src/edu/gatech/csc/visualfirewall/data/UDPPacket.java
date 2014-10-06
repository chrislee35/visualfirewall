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
 * UDPPacket
 */
public class UDPPacket extends AbstractPacket {
	public int srcport;
	public int dstport;
	public UDPPacket ( int srcport, int dstport, int length ) {
		this.srcport = srcport;
		this.dstport = dstport;
		this.length = length;
	}
	public String toString() {
		return( "UDP [ SPT="+srcport+" DPT="+dstport+" LEN="+length+" ] "+pdu );
	}
}
