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

import org.jfree.data.xy.AbstractXYZDataset;
import org.jfree.data.xy.XYZDataset;

/**
 * @author trost
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IDSAlarmViewDatasource extends AbstractXYZDataset implements XYZDataset 
{
	public static final int NUM_SERIES = 5;
	
	public IDSAlarmViewDataSeries[] series = new IDSAlarmViewDataSeries[NUM_SERIES];
	
	//////////////////////////////////////
	public final int NUM_PRIORITIES = 5;
	List seriesList = new ArrayList(NUM_PRIORITIES);
	
	HashMap attackerToDouble = new HashMap();
	HashMap doubleToAttacker = new HashMap();
	double currentAttackerIndex = 0.0;
	
	public final int NUM_HOURS = 24;
	
	//////////////////////////////////////
	
	/**
	 * 
	 */
	public IDSAlarmViewDatasource() {
		super();
		
		for(int i = 0; i < 5; ++i)
		{
			series[i] = new IDSAlarmViewDataSeries();
		}
		
		for(int i = 0; i < NUM_PRIORITIES; ++i)
		{
			ArrayList hourList = new ArrayList(NUM_HOURS);
			
			for(int x = 0; x < NUM_HOURS; ++x)
			{
				HashMap attackersToAlerts = new HashMap();
				hourList.add(x, attackersToAlerts);
			}
			
			seriesList.add(i, hourList);
		}
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#getSeriesCount()
	 */
	public int getSeriesCount() {
		
		//return series.length;
		
		return seriesList.size();
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.general.SeriesDataset#getSeriesName(int)
	 */
	
	public String getSeriesName(int s) 
	{
		/*
		if(s < this.series.length)
			return this.series[s].getName();
		else
			return null;
		*/
		
		if(s < seriesList.size() )
			return "Priority "+s;
		else
			return null;
		
		
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYZDataset#getZ(int, int)
	 */
	public Number getZ(int s, int item) {
		
		
		if(s < series.length)
		{
			if(item < series[s].zCount.size() )
				return (Number)series[s].zCount.get(item);
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getItemCount(int)
	 */
	public int getItemCount(int s) {
		
		if(s < series.length)
		{
			return series[s].zCount.size();
		}
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getX(int, int)
	 */
	public Number getX(int s, int item) 
	{
		
		if(s < series.length)
		{
			if(item < series[s].xTime.size() )
				return (Number)series[s].xTime.get(item);
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jfree.data.xy.XYDataset#getY(int, int)
	 */
	public Number getY(int s, int item) 
	{
		
		if(s < series.length)
		{
			if(item < series[s].yAttackerIP.size() )
				return (Number)series[s].yAttackerIP.get(item);
		}
		
		return null;
	}

	public Comparable getSeriesKey(int s) throws IndexOutOfBoundsException
	{
		if(s < series.length)
		{
			return new Double(1.0);
		}
		throw new IndexOutOfBoundsException();
	}

	public void addSnortAlarm(SnortAlarm alarm)
	{
		ArrayList hourList = (ArrayList)seriesList.get(alarm.priority);
		
		int hour = alarm.timestamp.getHours() % 24;
		int minute = alarm.timestamp.getMinutes();
		
		double time = hour + ((double)minute)/60.0;
		
		HashMap attackerToAlarm = (HashMap) hourList.get(hour);
		Double attacker;
		
		if(null == attackerToAlarm.get(alarm.dstip))
		{
			if(!attackerToDouble.containsKey(alarm.dstip))
			{
				attackerToDouble.put(alarm.dstip, new Double(++currentAttackerIndex));
			}
			else
			{
				attacker = (Double)attackerToDouble.get(alarm.dstip);
				
			}
			
			
			
			HashMap snortAlarmToFrequency = new HashMap();
			String tmp = alarm.desc +":" + alarm.protocol;
			snortAlarmToFrequency.put(tmp, new Integer(1));
			attackerToAlarm.put(alarm.dstip, snortAlarmToFrequency);
		}
		else
		{
			HashMap snortAlarmToFrequency = (HashMap)attackerToAlarm.get(alarm.dstip);
			String tmp = alarm.desc +":" + alarm.protocol;
			Integer freq = (Integer) snortAlarmToFrequency.get(tmp);
			
			if(freq == null)
			{
				freq = new Integer(1);
				snortAlarmToFrequency.put(tmp, freq);
			}
			else
			{
				int freqInt = freq.intValue();
				freq = new Integer(++freqInt);
				snortAlarmToFrequency.put(tmp, freq);
			}
		}
		
	}

}
