/*
 * Created on Apr 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.datasource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * @author chris
 * Apr 22, 2005
 * SnortAlarmDatabase
 */
public class SnortAlarmDatabase {
	static final boolean DEBUG = false;
	HashMap hm;
	
	public String getAlarmType( int sid ) {
		return ( (String)hm.get( new Integer( sid ) ) );
	}
	
	public SnortAlarmDatabase() {
		hm = new HashMap();
		File sadfile = new File("config/snortalarm.dat");
		if ( sadfile.exists() ) {
			if (DEBUG) System.out.println( "Loading database." );
			try {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream( sadfile) );
				hm = (HashMap)ois.readObject();
				ois.close();
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			if (DEBUG) System.out.println( "Creating database." );
			File snortalerts = new File("config/rules.map");
			if ( ! snortalerts.exists() ) {
				System.out.println( "Cannot find a snort database or rules to create one." );
				System.exit(-1);
			}
			try {
				BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(snortalerts) ) );
				ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( sadfile ) );
				String line;
				while ( (line = br.readLine() ) != null ) {
					String[] parts = line.split( "," );
					Integer sid = new Integer( parts[0] );
					hm.put( sid, parts[1] );
				}
				oos.writeObject(hm);
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	public static void main(String[] args) {
		SnortAlarmDatabase sad = new SnortAlarmDatabase();
		System.out.println( sad.hm.get( new Integer( 253 ) ) );
	}
}
