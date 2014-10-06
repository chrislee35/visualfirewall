/*
 * Created on Apr 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.util.Comparator;

import edu.gatech.csc.visualfirewall.view.Dot;

/**
 * @author trost
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DotComparator implements Comparator {

	/**
	 * 
	 */
	public DotComparator() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2)
	{
		Dot dot1 = (Dot)o1;
		Dot dot2 = (Dot)o2;
		
		if(dot1.equals(dot2))
			return 0;
		
		if(dot1.age > dot2.age)
			return 1;
		else if(dot1.age < dot2.age)
			return -1;
		else
		{
			if(dot1.x < dot2.x)
				return 1;
			else if(dot1.x > dot2.x)
				return -1;
			else
				if(dot1.y < dot2.y)
					return 1;
				else if(dot1.y > dot2.y)
					return -1;
				else
					return 0;
		}
	}

}
