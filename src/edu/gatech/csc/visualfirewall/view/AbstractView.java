/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;

import org.jfree.chart.JFreeChart;

import edu.gatech.csc.visualfirewall.data.AbstractPacket;
import edu.gatech.csc.visualfirewall.data.IPTableResult;
import edu.gatech.csc.visualfirewall.data.SnortAlarm;
import edu.gatech.csc.visualfirewall.data.listener.AbstractPacketListener;
import edu.gatech.csc.visualfirewall.data.listener.IPTableResultListener;
import edu.gatech.csc.visualfirewall.data.listener.SnortAlarmListener;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;


/**
 * @author chris Mar 31, 2005 AbstractView
 */
public abstract class AbstractView implements ActionListener, 
	SnortAlarmListener, IPTableResultListener, AbstractPacketListener {
		
	private boolean DEBUG = false;
	
	public boolean isMaximized;
	
	String name;
	
	Component canvas;
	public long lastUpdateTime;
	Rectangle worldWindowRect;
	boolean worldWindowChanged;
	
	public JFreeChart chart = null;

	//protected Rectangle wallInterior;

	int viewportWidth;
	int viewportHeight;

	public float red = 1.0f, 
	             green = 1.0f,
				 blue = 1.0f,
				 alpha = 1.0f;
	
	public AbstractView(){}
	
	public AbstractView(Rectangle worldWindowRect) {
		
		if(DEBUG)System.out.println("AbstractView: AbstractView() called");
		
		isMaximized = false;
		this.worldWindowRect = worldWindowRect;
		this.worldWindowChanged = false;
				
		//initWall();

		lastUpdateTime = System.currentTimeMillis();
		// get a GLCanvas
		/*
		 GLCapabilities capabilities = new GLCapabilities();
		canvas = GLDrawableFactory.getFactory().createGLCanvas(capabilities);
		// add a GLEventListener, which will get called when the
		// canvas is resized or needs a repaint
		canvas.addGLEventListener(this);
		*/
		// temp debug
		//javax.swing.Timer timer = new javax.swing.Timer(25, this);
		//timer.start();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public Component getCanvas() 
	{
		return canvas;
	}
	
	public GLCanvas getGLCanvas() 
	{
		return (GLCanvas)canvas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	protected void resetWorldWindow(GL gl, GLU glu) {
		
		if(DEBUG)System.out.println("AbstractView: resetWorldWindow() called");
		//System.out.println ("reset world window: " + worldWindowRect );
		// set the world window
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(worldWindowRect.x, worldWindowRect.x
				+ worldWindowRect.width, worldWindowRect.y, worldWindowRect.y
				+ worldWindowRect.height);
		// set viewport
		// args are x, y, width, height
		gl.glViewport(0, 0, viewportWidth, viewportHeight);

		worldWindowChanged = false;
		//initWall();
	}
	
	public void dispatchAlarm(SnortAlarm snortAlarm) {
		//System.out.println(snortAlarm.toString());
	}
	
	public void dispatchResult(IPTableResult ipTableResult)
	{
		//System.out.println(ipTableResult.toString() );
	}
	
	public void dispatchPacket(AbstractPacket packet)
	{
		//System.out.println(packet.toString() );
	}
	

	/**
	 * @return Returns the alpha.
	 */
	public float getAlpha() {
		return alpha;
	}
	/**
	 * @param alpha The alpha to set.
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	/**
	 * @return Returns the blue.
	 */
	public float getBlue() {
		return blue;
	}
	/**
	 * @param blue The blue to set.
	 */
	public void setBlue(float blue) {
		this.blue = blue;
	}
	/**
	 * @return Returns the green.
	 */
	public float getGreen() {
		return green;
	}
	/**
	 * @param green The green to set.
	 */
	public void setGreen(float green) {
		this.green = green;
	}
	/**
	 * @return Returns the lastUpdateTime.
	 */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	/**
	 * @param lastUpdateTime The lastUpdateTime to set.
	 */
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	/**
	 * @return Returns the red.
	 */
	public float getRed() {
		return red;
	}
	/**
	 * @param red The red to set.
	 */
	public void setRed(float red) {
		this.red = red;
	}
	/**
	 * @return Returns the viewportHeight.
	 */
	public int getViewportHeight() {
		return viewportHeight;
	}
	/**
	 * @param viewportHeight The viewportHeight to set.
	 */
	public void setViewportHeight(int viewportHeight) {
		this.viewportHeight = viewportHeight;
	}
	/**
	 * @return Returns the viewportWidth.
	 */
	public int getViewportWidth() {
		return viewportWidth;
	}
	/**
	 * @param viewportWidth The viewportWidth to set.
	 */
	public void setViewportWidth(int viewportWidth) {
		this.viewportWidth = viewportWidth;
	}
	/**
	 * @return Returns the worldWindowChanged.
	 */
	public boolean getWorldWindowChanged() {
		return worldWindowChanged;
	}
	/**
	 * @param worldWindowChanged The worldWindowChanged to set.
	 */
	public void setWorldWindowChanged(boolean worldWindowChanged) {
		this.worldWindowChanged = worldWindowChanged;
	}
	/**
	 * @return Returns the worldWindowRect.
	 */
	public Rectangle getWorldWindowRect() {
		return worldWindowRect;
	}
	/**
	 * @param worldWindowRect The worldWindowRect to set.
	 */
	public void setWorldWindowRect(Rectangle worldWindowRect) {
		this.worldWindowRect = worldWindowRect;
	}
	/**
	 * @param canvas The canvas to set.
	 */
	public void setGLCanvas(GLCanvas canvas) {
		this.canvas = canvas;
	}
	
	public void setCanvas(Component canvas) {
		this.canvas = canvas;
	}
}