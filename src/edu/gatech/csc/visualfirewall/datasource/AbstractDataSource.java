/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.datasource;

import java.io.File;
/**
 * @author chris
 * Mar 30, 2005
 * AbstractDataSource
 */
public abstract class AbstractDataSource implements Runnable {
	File input;
	public Class produces;	

}
