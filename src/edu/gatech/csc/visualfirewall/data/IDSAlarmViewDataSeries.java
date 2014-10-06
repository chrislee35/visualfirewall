/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author trost
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IDSAlarmViewDataSeries {

	public List xTime = new ArrayList();
	public List yAttackerIP = new ArrayList();
	public List zCount = new ArrayList();
	String name;
	
	HashMap attackersToAlarms = new HashMap();
	
	/**
	 * 
	 */
	public IDSAlarmViewDataSeries() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public IDSAlarmViewDataSeries(String name) {
		super();
		
		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public void addItem(Number x, Number y, Number z)
	{
		xTime.add(x);
		yAttackerIP.add(y);
		zCount.add(z);
	}
	
	public Number getXItem(int item)
	{
		if(item < xTime.size())
			return (Number)xTime.get(item);
		else
			return null;
	}
	
	public Number getYItem(int item)
	{
		if(item < yAttackerIP.size())
			return (Number)yAttackerIP.get(item);
		else
			return null;
	}
	
	public Number getZItem(int item)
	{
		if(item < zCount.size())
			return (Number)zCount.get(item);
		else
			return null;
	}
}
