/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.data;

import java.net.InetAddress;
import java.util.Date;

/**
 * @author chris
 * Mar 30, 2005
 * SnortAlarm
 */
public class SnortAlarm {
	public Date timestamp;
	public short[] type;
	public String desc;
	public InetAddress srcip;
	public InetAddress dstip;
	public byte priority;
	public String protocol;
	public int spt;
	public int dpt;
	
	public SnortAlarm ( Date timestamp, short[] type, String desc, byte priority, InetAddress srcip, InetAddress dstip, 
			String protocol, int spt, int dpt ) {
		this.timestamp = timestamp;
		this.type = type;
		this.desc = desc;
		this.srcip = srcip;
		this.dstip = dstip;
		this.priority = priority;
		this.protocol = protocol;
		this.spt = spt;
		this.dpt = dpt;
	}
	
	public String toString() {
		if ( spt < 0 )
			return( timestamp+" ["+type[0]+":"+type[1]+":"+type[2]+"] "+desc+" [Priority: "+priority+"] {"+protocol+"} "
					+srcip+" -> "+dstip );
		else
			return( timestamp+" ["+type[0]+":"+type[1]+":"+type[2]+"] "+desc+" [Priority: "+priority+"] {"+protocol+"} "
					+srcip+":"+spt+" -> "+dstip+":"+dpt );
	}
}
