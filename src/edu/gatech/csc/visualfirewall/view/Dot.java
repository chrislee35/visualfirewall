/*
 * Created on Apr 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.awt.Color;

/**
 * @author trost
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Dot
{
	public int x, y;
	public Color color;
	public long age;
	public int radius;
	
	Dot(float x, float y, float radius, Color color)
	{
		this(x,y,color);
		this.radius = (int)radius;
	}
	
	Dot(float x, float y, Color color)
	{
		this.x = (int)x;
		this.y = (int)y;
		this.radius = (int)15;
		this.color = color;
		this.age = System.currentTimeMillis();
	}
	
	public boolean equals(Object d)
	{	
		if(d.getClass().equals(this.getClass()))
		{
			Dot dot = (Dot)d;
			
			if( dot.x == x && 
				dot.y == y && 
				dot.radius == radius )
			{
				//System.out.println("Dot: equals() calles : "+ this + " == "+ d);
				return true;
			}
		}
		
		//System.out.println("Dot: equals() calles : "+ this + "!= "+ d);
		return false;
	}
	
	public String toString()
	{
		return "("+x+","+y+","+ radius+"), "+color;
	}

}
