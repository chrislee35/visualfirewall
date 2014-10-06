package edu.gatech.csc.visualfirewall.view;

import java.awt.Color;

class Line
{
	public int x1;
	public int x2;
	
	public int y1;
	public int y2;
	
	// default TCP color
	Color color = Color.GREEN; 
	
	public long age;
	
	public Line(float x1, float x2, float y1, float y2)
	{
		this.x1 = (int)x1;
		this.x2 = (int)x2;
		this.y1 = (int)y1;
		this.y2 = (int)y2;
		this.age = System.currentTimeMillis();
	}
	
	public Line(float x1, float x2, float y1, float y2, Color color)
	{
		this(x1, x2, y1, y2);
		this.color = color;
	}
	
	public String toString()
	{
		return "("+x1+","+y1+"), ("+x2+","+y2+"), "+color+ ", "+age;
	}
	
	/**
	 * @return Returns the color.
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * @param color The color to set.
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	/**
	 * @return Returns the x1.
	 */
	public float getX1() {
		return x1;
	}
	/**
	 * @param x1 The x1 to set.
	 */
	public void setX1(float x1) {
		this.x1 = (int)x1;
	}
	/**
	 * @return Returns the x2.
	 */
	public float getX2() {
		return x2;
	}
	/**
	 * @param x2 The x2 to set.
	 */
	public void setX2(float x2) {
		this.x2 = (int)x2;
	}
	/**
	 * @return Returns the y1.
	 */
	public float getY1() {
		return y1;
	}
	/**
	 * @param y1 The y1 to set.
	 */
	public void setY1(float y1) {
		this.y1 = (int)y1;
	}
	/**
	 * @return Returns the y2.
	 */
	public float getY2() {
		return y2;
	}
	/**
	 * @param y2 The y2 to set.
	 */
	public void setY2(float y2) {
		this.y2 = (int)y2;
	}
	
	public void setAge(long age) {
		this.age = age;
	}
	
	public boolean equals(Object line)
	{
		if(line.getClass().equals(this.getClass()) )
		{
			Line l = (Line)line;
			
			if(l.x1 == x1 && l.x2 == x2 && l.y1 == y1 && l.y2 == y2 )
				return true;
		}
		
		return false;
		
	}
	
	public static void main(String[] args)
	{
		Line l1 = new Line(1,2,3,4);
		int x = 1;
				
		Line l2 = new Line(1,2,3,4);
		
		if(l1.equals(l2))
		{
			System.out.println("l1 == l2");
		}
		else
			System.out.println("l1 != l2");

	}

}
