/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.view;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

import edu.gatech.csc.visualfirewall.VisualFirewall;
import edu.gatech.csc.visualfirewall.data.SnortAlarm;
import edu.gatech.csc.visualfirewall.datasource.SnortAlarmDatabase;
import java.awt.*;

/**
 * @author Jason Trost
 * Mar 31, 2005
 * VisualSignatureView
 */
public class IDSAlarmView extends AbstractView implements GLEventListener {

	boolean DEBUG = false;
	boolean EXPERIMENTAL = true;
	
	protected static final double TWO_PI = 2 * Math.PI;
	protected static final double ARC_SEGMENT = TWO_PI / 36;
	
	GLCanvas canvas;
	
	protected Rectangle worldWindowRect;
    boolean worldWindowChanged;

    protected Rectangle wallInterior;
	
	int viewportWidth;
    int viewportHeight;
	
	java.util.SortedSet linesSet;
	java.util.SortedSet dotsSet;
	
	// These are used for aging the lines.
	public static final double ONE_MIN = 60000;
	public static final double TWO_MIN = 2*ONE_MIN;
	public static final double THREE_MIN = 3*ONE_MIN;
	public static final double FOUR_MIN = 4*ONE_MIN;
	public static final double FIVE_MIN = 5*ONE_MIN;
	
	// Height and Width of the GL Canvas 
	public static final float HEIGHT = 3000.0f;
	public static final float WIDTH  = 3000.0f;
	
	// X coordinates of the left axis and right axis
	public static final float LEFT_AXIS = WIDTH * 0.15f;
	public static final float RIGHT_AXIS = WIDTH * 0.9f;
	
	// Y coordinates of the bottom axis and top "axis"
	public static final float BOTTOM_AXIS = HEIGHT * 0.1f;
	public static final float TOP_AXIS = HEIGHT * 0.9f;
	
	// length of the axises
	double SIDE_AXIS_LENGTH = (TOP_AXIS - BOTTOM_AXIS);
	double BOTTOM_AXIS_LENGTH = (RIGHT_AXIS - LEFT_AXIS);
	
	// 2^24
	public static final long NUM_SUBNET = 16777216L;
	
	// 2^16
	public static int NUM_PORTS = 65535;
	
	public static final String[] RULE_SET_TICKS = 
		{ "attack-responses", "backdoor", "bad-traffic", "chat", "ddos", 
		  "deleted", "dns", "dos", "experimental", "exploit", "finger",
		  "ftp", "icmp-info", "icmp", "imap", "info", "local", "misc", 
		  "multimedia", "mysql", "netbios", "nntp", "oracle", "other-ids", 
		  "p2p", "policy", "pop2", "pop3", "porn", "rpc", "rservices", "scan", 
		  "shellcode", "smtp", "snmp", "sql", "telnet", "tftp", "virus", "web-attacks", 
		  "web-cgi", "web-client", "web-coldfusion", "web-frontpage", "web-iis", 
		  "web-misc", "web-php", "x11"};
	
	public static final float[] BG_RGB = VisualFirewall.BG_COLOR.getColorComponents(null);
	public static final float[] AXIS_RGB = VisualFirewall.FG_COLOR.getColorComponents(null);
	
	public static int DRAW_AXIS = 1;
	
	HashMap ruleTypeToTick = new HashMap();
	
	SnortAlarmDatabase snortAlarmDatabase = new SnortAlarmDatabase();
	
	Color[] priority = {Color.green, Color.yellow, Color.ORANGE, Color.red};
	
	public IDSAlarmView(Rectangle worldWindowRect)
	{
		super(worldWindowRect);

		
        
		
		GLCapabilities capabilities = new GLCapabilities();
		setCanvas(new GLCanvas(capabilities));
		 

        // add a GLEventListener, which will get called when the
		// canvas is resized or needs a repaint
		getGLCanvas().addGLEventListener(this);
		
		for(int i = 0; i < RULE_SET_TICKS.length; ++i)
		{
			ruleTypeToTick.put(RULE_SET_TICKS[i], new Integer(i));
		}
		setName("IDSAlarm");
		
		// this is used to keep the lines sorted by age.
		linesSet = new TreeSet( new LineComparator() );
		// this is used to keep the dot sorted by age.
		dotsSet = new TreeSet( new DotComparator() );
		
		//javax.swing.Timer timer = new javax.swing.Timer (10000, this); 
		//timer.start();
		
	}
	
	public void actionPerformed(ActionEvent event) 
	{
		
	}
	
    /**
     * Remember that the GLDrawable is actually the 
     * GLCanvas that we dealt with earlier.
     */
    public void init(GLAutoDrawable gld) 
    {	
    	//if(DEBUG)System.out.println("VisualSignatureView: init() called");
        //Remember not to save the
        //GL and GLU objects for 
        //use outside of this method.
        //New ones will be provided 
        //later.
    	GL gl = getGLCanvas().getGL();
		GLU glu = new GLU();
                
        gl.glClearColor(BG_RGB[0], BG_RGB[1], BG_RGB[2], 1.0f );
        		
        //Let's make the point 5 pixels wide
        gl.glPointSize(5.0f);
                
        //glViewport's arguments represent
        //left, bottom, width, height
        gl.glViewport(0, 0, (int)WIDTH, (int)HEIGHT);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        //gluOrtho2D's arguments represent
        //left, right, bottom, top
        glu.gluOrtho2D(0, WIDTH, 0, HEIGHT);
        
        // This is supposed to optimize repeatedly drawn graphics. See 
        // http://fly.cc.fer.hr/~unreal/theredbook/chapter04.html for more info.
        gl.glNewList(DRAW_AXIS, GL.GL_COMPILE);
        	drawAxis(gl);
        gl.glEndList();
    }
    
    public void display(GLAutoDrawable gld) 
	{
    	//System.out.println("IDSAlarmView: display() called");
        // Remember to get a new copy
        // of GL object instead of
        // saving a previous one
    	GL gl = getGLCanvas().getGL();
		GLU glu = new GLU();
		
		// is there a pending world window change?
        if ( getWorldWindowChanged() )
            resetWorldWindow(gl, glu);
		
		// load identity matrix
        gl.glMatrixMode (GL.GL_MODELVIEW);
        gl.glLoadIdentity(); 
   
		//erase GLCanvas using the clear color		
       	//gl.glClearColor(red, green, blue, alpha); // background
       	gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        //Choose our color for drawing
       	//drawAxis(gl);
       	gl.glCallList(DRAW_AXIS);
		
		//if(DEBUG)System.out.println("linesSet.size(): "+linesSet.size());
		
		Set removeThese = new HashSet();
		
		synchronized(linesSet)
		{
			Iterator iter = (Iterator)linesSet.iterator();
			while(iter.hasNext() )
			{
				long time = System.currentTimeMillis();
				Line line = (Line) iter.next();
				
				double elapsed = time - line.age;
				float colorChange = (float)(elapsed/FIVE_MIN);
				
				line.age = time;
				
				Color c = line.getColor();
				
				float[] rgb = c.getColorComponents(null);
				
				c = new Color( rgb[0] - colorChange >= (BG_RGB[0] + 0.1f)?(rgb[0] - colorChange):(BG_RGB[0] + 0.1f), 
						       rgb[1] - colorChange >= (BG_RGB[1] + 0.1f)?(rgb[1] - colorChange):(BG_RGB[1] + 0.1f),  
							   rgb[2] - colorChange >= (BG_RGB[2] + 0.1f)?(rgb[2] - colorChange):(BG_RGB[2] + 0.1f) );
				
				/*rgb = c.getColorComponents(null);
				
				if(rgb[0] <= BG_RGB[0] && rgb[1] <= BG_RGB[1] && rgb[2] <= BG_RGB[2])
				{
					removeThese.add(line);
					continue;
				}*/
				
				line.setColor(c);
				drawLine(gl, line);
			}
			
			linesSet.removeAll(removeThese);
		}
		
		synchronized(dotsSet)
		{
			Iterator iter = (Iterator)dotsSet.iterator();
			while(iter.hasNext() )
			{
				Dot dot = (Dot)iter.next();
				drawDot(gl, dot);
			}
		}
		//drawAxis(gl);
		
		drawVerticalTimeBar(gl);
    }
    
    // precompute for optmizarion
    double SIDE_AXIS_LENGTH_DIV_RULE_SET_TICKS_length = SIDE_AXIS_LENGTH/RULE_SET_TICKS.length;
    double LEFT_AXIS_MINIS_10 = LEFT_AXIS - 10.0f;
    double LEFT_AXIS_MINIS_35 = LEFT_AXIS - 35.0f;
    double LEFT_AXIS_PLUS_10 = LEFT_AXIS + 10.0f;
    double TOP_AXIS_PLUS_35 = TOP_AXIS + 35.0f;
    double LOW_ADDR_X = RIGHT_AXIS - "0.0.0.0".length()*11;
    double HIGH_ADDR_X = RIGHT_AXIS - "255.255.255.0".length()*14;
	double BOTTOM_AXIS_MINUS_100 = BOTTOM_AXIS - 100.0f;
	double TIME_LABEL_X = (LEFT_AXIS + BOTTOM_AXIS_LENGTH/2.0f - "Time".length()*18);
	double BOTTOM_AXIS_MINUS_150 = BOTTOM_AXIS - 150.0f;
	double BOTTOM_AXIS_LENGTH_DIV_25 = BOTTOM_AXIS_LENGTH/25;
	double BOTTOM_AXIS_LENGTH_DIV_24 = BOTTOM_AXIS_LENGTH/24;
	double BOTTOM_AXIS_PLUS_10 = BOTTOM_AXIS + 10.0f;
	double BOTTOM_AXIS_MINUS_10 = BOTTOM_AXIS - 10.0f;
	double BOTTOM_AXIS_MINUS_50 = BOTTOM_AXIS - 50;
	
	String[] time = { "00:00", "01:00", "02:00", "03:00", "04:00", 
			          "05:00", "06:00", "07:00", "08:00", "09:00",
					  "10:00", "11:00", "12:00", "13:00", "14:00",
					  "15:00", "16:00", "17:00", "18:00", "19:00",
					  "20:00", "21:00", "22:00", "23:00"};
	
	void drawAxis(GL gl)
	{
		gl.glColor3f(AXIS_RGB[0], AXIS_RGB[1], AXIS_RGB[2]);
		
/////////////// Draw top and bottom boundaries //////////////////
		gl.glColor3f(BG_RGB[0] + 0.1f, BG_RGB[1] + 0.1f, BG_RGB[2] + 0.1f);
		gl.glPointSize(1.0f);
		
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(0.0f, HEIGHT - 1);
		gl.glVertex2d(WIDTH, HEIGHT - 1);
		gl.glEnd();
		
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(0.0f, 0.0f);
		gl.glVertex2d(WIDTH, 0.0f);
		gl.glEnd();
		/////////////////////////////////////////////////////////////////
		
		
		GLUT glut = new GLUT();
		
		gl.glColor3fv(AXIS_RGB, 0);
		gl.glPointSize(5.0f);
		
		///////////////////////// Left Axis //////////////////////////////////////////
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(LEFT_AXIS, BOTTOM_AXIS);
		gl.glVertex2d(LEFT_AXIS, TOP_AXIS);
		gl.glEnd();
		
		for(int i = 0; i < RULE_SET_TICKS.length; ++i)
		{
			double y = (i * SIDE_AXIS_LENGTH_DIV_RULE_SET_TICKS_length) + BOTTOM_AXIS;
			
			gl.glBegin(GL.GL_LINES);
	        gl.glVertex2d(LEFT_AXIS_MINIS_10, (float)y);
			gl.glVertex2d(LEFT_AXIS_PLUS_10, (float)y);
			gl.glEnd();
			
			if(isMaximized)
			{
				/////////////////// make text labels for snort rules classes ///////////
				gl.glColor3fv(AXIS_RGB, 0);
				int width = glut.glutBitmapLength( GLUT.BITMAP_HELVETICA_10, RULE_SET_TICKS[i] );
				
				double tmpX = LEFT_AXIS_MINIS_35 - (width*5);
				gl.glRasterPos2f((float)tmpX + 20, (float)y - 10.0f);
				//Take a string and make it a bitmap, put it in the 'gl' passed over and pick
				//the GLUT font, then provide the string to show
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, RULE_SET_TICKS[i]);
				
				// Draw a horizontal grid
				gl.glEnable(GL.GL_LINE_STIPPLE);
				gl.glLineStipple(3, (short)0xAAAA);
				
				gl.glColor3f(0.15f, 0.15f, 0.15f);
				gl.glBegin(GL.GL_LINES);
		        gl.glVertex2d(LEFT_AXIS_PLUS_10, (float)y);
				gl.glVertex2d(RIGHT_AXIS, (float)y);
				gl.glEnd();
				
				gl.glColor3fv(AXIS_RGB, 0);
				
				gl.glDisable(GL.GL_LINE_STIPPLE);
			}
		}
		
		///////////////////////// Right Axis //////////////////////////////////////////
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(RIGHT_AXIS, BOTTOM_AXIS);
		gl.glVertex2d(RIGHT_AXIS, TOP_AXIS);
		gl.glEnd();
		
		if(isMaximized)
		{
			gl.glRasterPos2f((float)LOW_ADDR_X, (float)TOP_AXIS_PLUS_35);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "0.0.0.0");
			
			gl.glRasterPos2f((float)HIGH_ADDR_X, (float)BOTTOM_AXIS_MINUS_100);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "255.255.255.0");
			
			gl.glRasterPos2f((float)TIME_LABEL_X, (float)BOTTOM_AXIS_MINUS_150);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Time");
			
			if(EXPERIMENTAL)
			{
				float x = (float)(LEFT_AXIS + BOTTOM_AXIS_LENGTH/2.0f - "Monitored Subnet".length()*14);
				gl.glRasterPos2f((float)x, (float)TOP_AXIS + 30.0f);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Monitored Subnet");
			}
		}
		
		if(EXPERIMENTAL)
		{
			///////////////////////// Top Axis //////////////////////////////////////////
			gl.glBegin(GL.GL_LINES);
	        gl.glVertex2d(LEFT_AXIS, TOP_AXIS);
			gl.glVertex2d(RIGHT_AXIS, TOP_AXIS);
			gl.glEnd();	
		}
		
		///////////////////////// Bottom Axis //////////////////////////////////////////
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(LEFT_AXIS, BOTTOM_AXIS);
		gl.glVertex2d(RIGHT_AXIS, BOTTOM_AXIS);
		gl.glEnd();
		
		// time ticks
		for(int i = 1; i <= 24; ++i)
		{
			double x = (i * BOTTOM_AXIS_LENGTH_DIV_25) + LEFT_AXIS;
			
			gl.glBegin(GL.GL_LINES);
	        gl.glVertex2d((float)x, BOTTOM_AXIS_PLUS_10);
			gl.glVertex2d((float)x, BOTTOM_AXIS_MINUS_10);
			gl.glEnd();
			int t = (i-1)%24;
			
			if(isMaximized && ( (t % 3) == 0) )
			{
				
				///////////////////////////////////////////////////////////		
				gl.glRasterPos2f((float)x - 60, (float)BOTTOM_AXIS_MINUS_50);
				//Take a string and make it a bitmap, put it in the 'gl' passed over and pick
				//the GLUT font, then provide the string to show
				
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, time[t]);
			}
		}
	}
	
	void drawVerticalTimeBar(GL gl)
	{
		//////////////////Draw "Current Time Vertical Line" ////////////////////////////
		Date now = new Date();
		float hour = now.getHours();
		float minutes = now.getMinutes();
				
		double time = hour + ((double)minutes)/60.0;
		//double x = (time + 1) * (BOTTOM_AXIS_LENGTH_DIV_25) + LEFT_AXIS;
		double x = (time * BOTTOM_AXIS_LENGTH_DIV_24) + LEFT_AXIS;
		
		gl.glEnable(GL.GL_LINE_STIPPLE);
		// Set the stippling pattern
		gl.glLineStipple(3, (short)0xAAAA);
		
		// draw current time line
		gl.glColor3f(0.1f, 0.1f, 0.5f);
		gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex2d((float)x, BOTTOM_AXIS);
		gl.glVertex2d((float)x, TOP_AXIS);
		gl.glEnd();
		
		gl.glDisable(GL.GL_LINE_STIPPLE);
	}
	
	void drawLine(GL gl, Line line)
	{		
		gl.glPointSize(5.0f);
		
		Color color = line.getColor();
		float[] rgb = color.getColorComponents(null);
		
		gl.glEnable(GL.GL_SMOOTH);
		gl.glColor3fv(rgb, 0);
		
		gl.glBegin(GL.GL_LINES); 
        gl.glVertex2d(line.x1, line.y1);
		gl.glVertex2d(line.x2, line.y2);
		gl.glEnd();
		gl.glDisable(GL.GL_SMOOTH);
	}
	
	void drawDot(GL gl, Dot dot)
	{	
		Color color = dot.color;
		float[] rgb = color.getColorComponents(null);
		double theta;
		
		float x,y;
		
		gl.glColor3fv(rgb, 0);
		
		gl.glBegin (GL.GL_POLYGON);
        gl.glVertex2f(dot.x, dot.y);
        
        for (theta = 0; theta <= TWO_PI; theta += ARC_SEGMENT) 
        {
            x = (float) (dot.x + (Math.sin(theta) * dot.radius));
            y = (float) (dot.y + (Math.cos(theta) * dot.radius));
            
            gl.glVertex2f(x,y);            
        }
        gl.glEnd();
	}

    //we won't need these two methods
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
	{
    	GL gl = getGLCanvas().getGL();
		GLU glu = new GLU(); 

        // save size for viewport reset
        setViewportWidth(width);
        setViewportHeight(height);

        resetWorldWindow(gl, glu);
		
		display(drawable); 
	}
    
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
    {
	}
	
   	public void addLine(Line line)
	{
		synchronized(linesSet)
		{
			//if(!linesSet.contains(line))
			linesSet.add(line);
		}
	}
   	
   	public void renderVerticalBitmapString(GL gl, float x, float y, int bitmapHeight, int font, String string) 
   	{
   		GLUT glut = new GLUT();
   		char c;	
   		
   		for (int i = 0; i < string.length(); ++i) 
   		{
   			c = string.charAt(i);
   			
   			gl.glRasterPos2f(x, y+bitmapHeight*i);
   			glut.glutBitmapCharacter(font, c);
   		}

   	}
   	
   	
	
   	double SIDE_AXIS_LENGTH_DIV_NUM_SUBNET = SIDE_AXIS_LENGTH/((double)NUM_SUBNET);
   	
	public void dispatchAlarm(SnortAlarm snortAlarm)
	{
		// create 2 'Dots' and a line connecting them

		short sId = snortAlarm.type[1];
		
		String ruleType = snortAlarmDatabase.getAlarmType(sId);		
		Integer ruleTypeY = (Integer)ruleTypeToTick.get(ruleType);
		
		if(DEBUG)System.out.println(ruleType +" : "+snortAlarm);
		
		
		if(ruleTypeY == null)
		{
			//System.out.println("ruleType == null for sId = "+sId);
			return;
		}
		
		double y = (ruleTypeY.intValue() * SIDE_AXIS_LENGTH_DIV_RULE_SET_TICKS_length) + BOTTOM_AXIS;
		
		int hour = snortAlarm.timestamp.getHours();
		int min = snortAlarm.timestamp.getMinutes();
		
		double time = hour + ((double)min)/60.0;
		double x = (time * BOTTOM_AXIS_LENGTH_DIV_24) + LEFT_AXIS;
		
		Dot d1 = new Dot((float)x, (float)y, priority[snortAlarm.priority]);
		
		byte[] attacker = snortAlarm.srcip.getAddress();
		
		long subnet = (0x0FF & attacker[0]);
		subnet <<= 8;
		subnet |= (0x0FF & attacker[1]);
		subnet <<= 8;
		subnet |= (0x0FF & attacker[2]);
		subnet <<= 8;
		subnet |= (0x0FF & attacker[3]);
		 
		//24 bit netmask
		subnet &= 0x0000000000FFFFFFL;
		
		double attackerY = SIDE_AXIS_LENGTH_DIV_NUM_SUBNET*((double)subnet) + BOTTOM_AXIS;
		
		Dot d2 = new Dot((float)RIGHT_AXIS, (float)attackerY, priority[snortAlarm.priority]);
		
		addLine(new Line((float)x, (float)RIGHT_AXIS, (float)y, (float)attackerY, VisualFirewall.FG_COLOR));
		
		Dot d3 = null;
		//////////////experimental: draw lines to victim too //////////////////////////////////
		if(EXPERIMENTAL)
		{
			byte[] victim = snortAlarm.dstip.getAddress();
			
			subnet = (0x0FF & victim[0]);
			subnet <<= 8;
			subnet |= (0x0FF & victim[1]);
			subnet <<= 8;
			subnet |= (0x0FF & victim[2]);
			subnet <<= 8;
			subnet |= (0x0FF & victim[3]);
			 
			//subnet netmask
			subnet &= 0x00000000000000FFL;
			
			double victimX = LEFT_AXIS + BOTTOM_AXIS_LENGTH*((double)subnet)/ 255.0;
			
			d3 = new Dot((float)victimX, (float)TOP_AXIS, priority[snortAlarm.priority]);
			addLine(new Line((float)x, (float)victimX, (float)y, (float)TOP_AXIS, Color.red));
		}
		////////////////////////////////////////////////////////////////////////////////////
		
		synchronized(dotsSet)
		{
			dotsSet.add(d1);
			dotsSet.add(d2);
			
			if(EXPERIMENTAL && d3 != null)
				dotsSet.add(d3);
		}
		
	}

	
}
