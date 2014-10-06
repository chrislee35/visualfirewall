/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.datasource;

import edu.gatech.csc.visualfirewall.data.*;
import edu.gatech.csc.visualfirewall.data.listener.IPTableResultListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.*;

import javax.swing.event.EventListenerList;

/**
 * @author chris
 * Mar 30, 2005
 * IPTablesLog
 */
public class IPTablesLog extends FirewallLog {
	EventListenerList iptableResultListeners = new EventListenerList();
	static boolean DEBUG = false;
	
	static final Pattern protopattern = Pattern.compile( "PROTO=(\\w+)" );
	static final Pattern udppattern = Pattern.compile( "(\\w+\\s+\\d+\\s+[\\d\\d:]+)\\s+.*?(DROP|ACCEPT)\\s+.*?SRC=([\\d\\.]+)\\s+DST=([\\d\\.]+)\\s+"
			+"LEN=(\\d+).*?SPT=(\\d+)\\s+DPT=(\\d+)\\s+LEN=(\\d+)" );
	static final Pattern tcppattern = Pattern.compile( "(\\w+\\s+\\d+\\s+[\\d\\d:]+)\\s+.*?(DROP|ACCEPT)\\s+.*?SRC=([\\d\\.]+)\\s+DST=([\\d\\.]+)\\s+"
			+"LEN=(\\d+).*?SPT=(\\d+)\\s+DPT=(\\d+)" );
	static final Pattern icmppattern = Pattern.compile( "(\\w+\\s+\\d+\\s+[\\d\\d:]+)\\s+.*?(DROP|ACCEPT)\\s+.*?SRC=([\\d\\.]+)\\s+DST=([\\d\\.]+)\\s+"
			+"LEN=(\\d+).*?TYPE=(\\d+)\\s+CODE=(\\d+)" );
	SimpleDateFormat sdf = new SimpleDateFormat("MMM d H:mm:ss");
	int year = new Date().getYear();
	
	public IPTablesLog ( File input ) {
		this.input = input;
		produces = IPTableResult.class;
	}
	
	IPTableResult parseData( byte[] data ) {
		String mystr = new String( data );
		Matcher matcher = protopattern.matcher( mystr );
		if ( ! matcher.find() )
			return null;
		String proto = matcher.group(1);
		try {
			int i = 1;
			if ( proto.equals("UDP") ) {
				matcher = udppattern.matcher( mystr );
				if ( ! matcher.find() )
					return null;
				Date timestamp = sdf.parse( matcher.group(i++) );
				timestamp.setYear(year);
				boolean accepted = ( matcher.group(i++) ).startsWith("ACCEPT");
				InetAddress src = java.net.InetAddress.getByName( matcher.group(i++) );
				InetAddress dst = java.net.InetAddress.getByName( matcher.group(i++) );
				short len = Short.parseShort( matcher.group(i++) );
				int spt = Integer.parseInt( matcher.group(i++) );
				int dpt = Integer.parseInt( matcher.group(i++) );
				short udplen = Short.parseShort( matcher.group(i++) );
				return( new IPTableResult( timestamp, accepted, new IPPacket( src, dst, new UDPPacket( spt, dpt, udplen ), len ) ) );
			} else if ( proto.equals("TCP") ) {
				matcher = tcppattern.matcher( mystr );
				if ( ! matcher.find() )
					return null;
				Date timestamp = sdf.parse( matcher.group(i++) );
				timestamp.setYear(year);
				boolean accepted = ( matcher.group(i++) ).startsWith("ACCEPT");
				InetAddress src = java.net.InetAddress.getByName( matcher.group(i++) );
				InetAddress dst = java.net.InetAddress.getByName( matcher.group(i++) );
				short len = Short.parseShort( matcher.group(i++) );
				int spt = Integer.parseInt( matcher.group(i++) );
				int dpt = Integer.parseInt( matcher.group(i++) );
				return( new IPTableResult( timestamp, accepted, new IPPacket( src, dst, new TCPPacket( spt, dpt, 0, len ), len ) ) );
			} else if ( proto.equals("ICMP") ) {
				matcher = icmppattern.matcher( mystr );			
				if ( ! matcher.find() )
					return null;
				Date timestamp = sdf.parse( matcher.group(i++) );
				timestamp.setYear(year);
				boolean accepted = ( matcher.group(i++) ).startsWith("ACCEPT");
				InetAddress src = java.net.InetAddress.getByName( matcher.group(i++) );
				InetAddress dst = java.net.InetAddress.getByName( matcher.group(i++) );
				short len = Short.parseShort( matcher.group(i++) );
				short type = Short.parseShort( matcher.group(i++) );
				short code = Short.parseShort( matcher.group(i++) );
				return( new IPTableResult( timestamp, accepted, new IPPacket( src, dst, new ICMPPacket( type, code, len ), len ) ) );
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addIPTableResultListener( IPTableResultListener listener ) {
		iptableResultListeners.add( IPTableResultListener.class, listener );
	}
	
	public void removeIPTableResultListener( IPTableResultListener listener ) {
		iptableResultListeners.remove( IPTableResultListener.class, listener );
	}
	
	protected void fireIPTableResult( IPTableResult iptr ) {
		Object[] listeners = iptableResultListeners.getListenerList();
		int numListeners = listeners.length;
		if (DEBUG) System.out.println( iptr );
		for ( int i = 0; i < numListeners; i += 2 ) {
			if ( listeners[i] == IPTableResultListener.class )
				((IPTableResultListener)listeners[i+1]).dispatchResult( iptr );
		}
	}
	
	public void run () {
		try {
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ( ( line = br.readLine() ) != null ) {
				if (DEBUG) System.out.println( "Received line." );
				IPTableResult itr = parseData( line.getBytes() );
				if ( itr != null )
				{
					if (DEBUG) System.out.println( itr );
					fireIPTableResult( itr );
				}
				
				
				Thread.sleep( (int)(Math.random()*100 ) );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	public static void main ( String[] args ) {
		DEBUG = true;
		IPTablesLog ipl = new IPTablesLog( new File( "logs/iptables.txt" ) );
		ipl.run();
	}

}
