/*
 * Created on Apr 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.datasource;

import edu.gatech.csc.visualfirewall.data.listener.IPTableResultListener;

/**
 * @author chris
 * Apr 22, 2005
 * FirewallLog
 */
public abstract class FirewallLog extends AbstractDataSource {
	public abstract void addIPTableResultListener( IPTableResultListener listener );
}
