package edu.gatech.csc.visualfirewall.view;

import java.awt.*;

/**
 * @author nic(k)
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PongBall extends Object{

	
	
	//ip addr of them (not host)
	protected String ipaddr;
		
	//port num
	protected int srcPort, dstPort;
	
	//direction of traffic
	protected boolean incoming = false;
	
	//to explode or not ;)
	protected boolean explode = false; 
	
	//vectors of motion
	protected double xV, yV;
	
	//pixel coord
	protected float x,y;
		
	//pixel coordinates for origin and end points
	public double xOrigin, yOrigin, xEnd, yEnd;
	
	//pixel speed
	public static float velocity;
	
	float slope, lineOffset;
	
	protected float red, green, blue;
	
	public float textRed, textGreen, textBlue;
	
	public boolean reflecting, movingLeft, drawCircle;
	
	public int bounceCounter = 2;
	
	public Color textColor;
	
	public boolean isUDP = false;
	public boolean isTCP = false;
	public boolean isICMP = false;
	
	public short type, code;
	
	protected double ballRadius = 25;
	
	public PongBall (float xV, float yV){
		this.xV = xV;
		this.yV = yV;
	
		x = 0;
		y = 0;
		
		red = 0.5f;
		green = 0.5f;
		blue = 0.5f;
		
		velocity = 1000f;
			
    }
		
	

    public double getXV() {
        return xV;
    }

    public double getYV() {
        return yV;
    }

    public void setVector(double xO, double yO, double xE, double yE) {
		this.xOrigin = xO;
		this.yOrigin = yO;
		
		this.xEnd = xE;
		this.yEnd = yE;
		
		//change later?
		x = (float) xO;
    	y = (float) yO;
    	
    	this.xV = xE - xO;
        this.yV = yE - yO;
        
        slope = (float) yV / (float) xV;
        lineOffset = y - slope*x;
        
    }
    

    /*
     * used to reflect ball
     */
    public void reflectVectorSlope() {
    	double oldxO, oldyO;   	
    	
    	//origin is now the end point
    	oldxO = xOrigin;
    	oldyO = yOrigin;
    	xOrigin = xEnd;			
		yOrigin = yEnd;
		
		//switch new x end
		xEnd = oldxO;
		
		//new y end
		yEnd = yEnd*2 - oldyO;
		
		x = (float) xOrigin;
    	y = (float) yOrigin;
    	
    	this.xV = xEnd - xOrigin;
        this.yV = yEnd - yOrigin;
    	
    	slope = (float) yV / (float) xV;
        lineOffset = y - slope*x;
        
        this.reflecting = true;
        this.movingLeft = !movingLeft;
        
        bounceCounter--;
        
    }    
    
        
    public void setXYcoord(float x, float y){
    	this.x = x;
    	this.y = y;    	
    }
    
    public float getXcoord(){
    	return x;
    }
    
    public float getYcoord(){
    	return y;    	
    }
    
	public float getRed() {
        return red;
    }
    public float getGreen() {
        return green;
    }
    public float getBlue() {
        return blue;
    }
    
    public void setColor(float red, float green, float blue){
    	this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    /*
     * converts an AWT Color to the RGB floats that JOGL likes
     */
    public void setTextColor(Color c){
        textRed = c.getRed() / 255f;
        textGreen = c.getGreen() / 255f;
        textBlue = c.getBlue() / 255f;
    }
    
    public void setVelocity(float vel){
    	velocity = vel;
    }
    
    public float getVelocity(){
    	return velocity;
    }
    
    public double getRadius(){
    	return ballRadius;
    }
    
    public void setRadius(double newRadius){
    	ballRadius = newRadius;
    }
    
        
    public void setRejected(boolean yesNo){
    	explode = yesNo;
    }
    
    public boolean isRejected(){
    	return explode;
    }
    
    public void setIncoming(boolean yesNo){
    	
    	incoming = yesNo;
    	
    	if(incoming){
    		drawCircle = true;
    		movingLeft = true;
    	}
    	else{
    		drawCircle = false;
    		movingLeft = false;
    	}
    		    	
    	reflecting = false;    	
    }
    
    public boolean isIncoming(){
    	return incoming;
    }

    public void setIPPorts(String ip, int src, int dst){
    	ipaddr = ip;
    	srcPort = src;
    	dstPort = dst;
    }
    
    public int getSrcPort(){
    	return srcPort;
    }
        
    public int getDstPort(){
    	return dstPort;
    }
    
       
    public String getIPAddr(){
    	return ipaddr;
    }
    
}//end class