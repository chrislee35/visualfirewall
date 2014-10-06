/*
 * Created on Apr 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.util.Comparator;

/**
 * @author trost
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LineComparator implements Comparator {

	/**
	 * 
	 */
	public LineComparator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) 
	{
		Line line1 = (Line)o1;
		Line line2 = (Line)o2;
		
		if(line1.equals(line2))
			return 0;
		
		if(line1.age > line2.age)
			return 1;
		else if(line1.age < line2.age)
			return -1;
		else
		{
			if(line1.x1 < line2.x2)
				return 1;
			else if(line1.x1 > line2.x2)
				return -1;
			else
			{
				if(line1.y1 < line2.y2)
					return 1;
				else if(line1.y1 > line2.y2)
					return -1;
				else
				{
					return 0;
				}
			}
		}
	}
}
