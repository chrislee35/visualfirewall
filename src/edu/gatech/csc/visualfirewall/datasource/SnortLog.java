/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.datasource;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.InetAddress;

import javax.swing.event.EventListenerList;

import edu.gatech.csc.visualfirewall.data.SnortAlarm;
import edu.gatech.csc.visualfirewall.data.listener.SnortAlarmListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chris Mar 30, 2005 SnortLog
 */
public class SnortLog extends AbstractDataSource {
	
	EventListenerList snortAlarmListeners = new EventListenerList();
	static final boolean DEBUG = false;
	SimpleDateFormat sdf = new SimpleDateFormat("mm/dd-H:mm:ss.SSSSSS");
	int year = new Date().getYear();

	Pattern p = Pattern
			.compile("^(.{21}).*?\\[(\\d+):(\\d+):(\\d+)\\] (.*?) \\[\\*\\*\\].*?\\[Priority: (\\d+)\\] \\{(PIM|TCP|UDP|ICMP)\\} ([\\d\\.]+):?(\\d+)? \\-> ([\\d\\.]+):?(\\d+)?");

	Pattern p2 = Pattern
		.compile("^(.{21}).*?\\[(\\d+):(\\d+):(\\d+)\\]\\s+(.*?)\\s+\\[\\*\\*\\].*?\\s[{]([^}]*)[}]\\s+([\\d\\.]+):?(\\d+)?\\s+\\->\\s+([\\d\\.]+):?(\\d+)?");
	
	public SnortLog(File input) {
		this.input = input;
		this.produces = SnortAlarm.class;
	}

	SnortAlarm parseData(byte[] data) {
		String mystr = new String(data);
		
		Matcher m = p.matcher(mystr);
		Matcher m2 = p2.matcher(mystr);
		
		if (m.find())
		{
			
			//if(DEBUG)System.out.println(mystr);
			
			try {
				int i = 1;
				Date timestamp = sdf.parse(m.group(i++));
				timestamp.setYear( year );
				short[] type = new short[3];
				type[0] = Short.parseShort(m.group(i++));
				type[1] = Short.parseShort(m.group(i++));
				type[2] = Short.parseShort(m.group(i++));
				String desc = m.group(i++);
				byte priority = Byte.parseByte(m.group(i++));
				String proto = m.group(i++);
				InetAddress srcip = InetAddress.getByName(m.group(i++));
				int spt = -1;
				if ( m.group(i) != null ) 
					spt = Integer.parseInt(m.group(i++));
				else
					i++;
				InetAddress dstip = InetAddress.getByName(m.group(i++));
				int dpt = -1;
				if ( m.group(i) != null )
					dpt = Integer.parseInt(m.group(i++));
				else
					i++;
				return (new SnortAlarm(timestamp, type, desc, priority, srcip, dstip, proto, spt, dpt));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else if(m2.find())
		{
			//if(DEBUG)System.out.println(mystr);
			
			try 
			{
				int i = 1;
				Date timestamp = sdf.parse(m2.group(i++));
				timestamp.setYear( year );
				
				short[] type = new short[3];
				type[0] = Short.parseShort(m2.group(i++));
				type[1] = Short.parseShort(m2.group(i++));
				type[2] = Short.parseShort(m2.group(i++));
				
				String desc = m2.group(i++);
				
				byte priority = 0;
				String proto = m2.group(i++);
				
				InetAddress srcip = InetAddress.getByName(m2.group(i++));
				
				int spt = -1;
				if ( m2.group(i) != null ) 
					spt = Integer.parseInt(m2.group(i++));
				else
					i++;
				InetAddress dstip = InetAddress.getByName(m2.group(i++));
				int dpt = -1;
				if ( m2.group(i) != null )
					dpt = Integer.parseInt(m2.group(i++));
				else
					i++;
				
				return (new SnortAlarm(timestamp, type, desc, priority, srcip, dstip, proto, spt, dpt));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			if(DEBUG)System.out.println("DID NOT MATCH: "+mystr);
			return null;
		}
		
	}

	public void run() {
		//System.out.println( "SnortLog Started" );
		try {
			FileReader fr = new FileReader(input);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null) {
				SnortAlarm sa = parseData(line.getBytes());
				if (sa != null)
					fireSnortAlarm( sa );
				
				Thread.sleep( (int)(Math.random()*100));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void addSnortAlarmListener( SnortAlarmListener listener ) {
		snortAlarmListeners.add( SnortAlarmListener.class, listener );
	}
	
	public void removeSnortAlarmListener( SnortAlarmListener listener ) {
		snortAlarmListeners.remove( SnortAlarmListener.class, listener );
	}
	
	protected void fireSnortAlarm( SnortAlarm iptr ) {
		Object[] listeners = snortAlarmListeners.getListenerList();
		int numListeners = listeners.length;

		if (DEBUG) System.out.println(iptr);
		
		for ( int i = 0; i < numListeners; i += 2 ) {
			if ( listeners[i] == SnortAlarmListener.class )
				((SnortAlarmListener)listeners[i+1]).dispatchAlarm( iptr );
		}
	}

	public static void main(String[] args) {
		new SnortLog(new File("logs/snort.txt")).run();
	}
}