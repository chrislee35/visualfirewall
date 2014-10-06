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
 * ICMPPacket
 */
public class ICMPPacket extends AbstractPacket {
	public short type;
	public short code;
	public ICMPPacket ( short type, short code, int length ) {
		this.type = type;
		this.code = code;
		this.length = length;
	}
	public String toString() {
		return( "ICMP [ TYPE="+type+" CODE="+code+" LEN="+length+" ] "+pdu );
	}
}
