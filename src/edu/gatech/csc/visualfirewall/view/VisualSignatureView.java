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
import edu.gatech.csc.visualfirewall.data.IPPacket;
import edu.gatech.csc.visualfirewall.data.IPTableResult;
import edu.gatech.csc.visualfirewall.data.TCPPacket;
import edu.gatech.csc.visualfirewall.data.UDPPacket;


/**
 * @author Jason Trost
 * Mar 31, 2005
 * VisualSignatureView
 */
public class VisualSignatureView extends AbstractView implements GLEventListener {

	boolean DEBUG = false;
	
	GLCanvas canvas;
	
	protected Rectangle worldWindowRect;
    boolean worldWindowChanged;

    protected Rectangle wallInterior;
	
	int viewportWidth;
    int viewportHeight;
	
	//java.util.List linesList = new LinkedList();
	java.util.SortedSet linesSet;
	
	// These are used for aging the lines.
	public static final double ONE_MIN = 60000;
	public static final double TWO_MIN = 2*ONE_MIN;
	public static final double THREE_MIN = 3*ONE_MIN;
	public static final double FOUR_MIN = 4*ONE_MIN;
	public static final double FIVE_MIN = 5*ONE_MIN;
	
	public static final float HEIGHT = 3000.0f;
	public static final float WIDTH  = 3000.0f;
	
	public static final float PORT_AXIS_X = WIDTH * 0.12f;
	public static final float ADDR_AXIS_X = WIDTH * 0.82f;
	
	public static final long NUM_ADDR = 4294967295L;
	public static int NUM_PORTS = 65535;
	public static final double CUBE_ROOT_65535 = Math.pow(65535, 0.3333333);
	
	public static final int[] MARKED_PORTS = {10, 80, 150, 500, 1000, 5000, 10000, 50000};
	public static double[] MARKED_PORTS_HEIGHT = new double[MARKED_PORTS.length];
	public static double[] MARKED_PORTS_HEIGHT_MINUS_12 = new double[MARKED_PORTS.length];
	public static String[] MARKED_PORTS_AS_STRINGS = new String[MARKED_PORTS.length];
	public static int[] MARKED_PORTS_AS_STRINGS_WIDTH = new int[MARKED_PORTS.length];
	
	public static double[] MARKED_PORTS_X = new double[MARKED_PORTS.length];
	
	public static final float[] BG_RGB = VisualFirewall.BG_COLOR.getColorComponents(null);
	public static final float[] AXIS_RGB = VisualFirewall.FG_COLOR.getColorComponents(null);
	
	public static final Color TCP_LINE_COLOR = Color.GREEN;
	public static final Color UDP_LINE_COLOR = Color.ORANGE;
	
	public static int DRAW_AXIS = 1;
	
	public VisualSignatureView(Rectangle worldWindowRect) 
	{
		super(worldWindowRect);

		GLCapabilities capabilities = new GLCapabilities();
		setCanvas(new GLCanvas(capabilities));

        // add a GLEventListener, which will get called when the
		// canvas is resized or needs a repaint
		getGLCanvas().addGLEventListener(this);
		
		linesSet = new TreeSet( new LineComparator() );
        
        //javax.swing.Timer timer = new javax.swing.Timer (10000, this); 
		//timer.start();
        
        GLUT glut = new GLUT();
		
		// precompute here for extra performance.
		for(int i = 0; i < MARKED_PORTS.length; ++i)
		{
			MARKED_PORTS_HEIGHT[i] = HEIGHT * ( 1.0 - Math.pow(MARKED_PORTS[i], 0.333333) / CUBE_ROOT_65535);
			MARKED_PORTS_HEIGHT_MINUS_12[i] = MARKED_PORTS_HEIGHT[i] - 12.0f; 
			MARKED_PORTS_AS_STRINGS[i] = Integer.toString(MARKED_PORTS[i]);
			MARKED_PORTS_AS_STRINGS_WIDTH[i] = 5*glut.glutBitmapLength( GLUT.BITMAP_HELVETICA_10, MARKED_PORTS_AS_STRINGS[i] );
			MARKED_PORTS_X[i] = PORT_AXIS_X_MINUS_20 - MARKED_PORTS_AS_STRINGS_WIDTH[i]; 
		}
		
		setName("VisualSignature");
	}
	
	public void actionPerformed(ActionEvent event) 
	{
		//System.out.println(linesSet.size()); 
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
    
    int size = 0;
    boolean clear = true;
    
    public void display(GLAutoDrawable gld) 
	{
    	//System.out.println("VisualSignatureView: display() called, linesSet.size(): "+linesSet.size());

    	//if(DEBUG)System.out.println("VisualSignatureView: display() called");
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
			//System.out.println("linesSet.size() = "+linesSet.size());
			
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
				
				c = new Color( rgb[0] - colorChange >= BG_RGB[0]?(rgb[0] - colorChange):BG_RGB[0], 
						       rgb[1] - colorChange >= BG_RGB[1]?(rgb[1] - colorChange):BG_RGB[1],  
							   rgb[2] - colorChange >= BG_RGB[2]?(rgb[2] - colorChange):BG_RGB[2] );
				
				rgb = c.getColorComponents(null);
				
				if(rgb[0] <= BG_RGB[0] && rgb[1] <= BG_RGB[1] && rgb[2] <= BG_RGB[2])
				{
					//System.out.println("line removed: "+line);
					removeThese.add(line);
					continue;
				}
				
				line.setColor(c);
				drawLine(gl, line);
			}
			
			linesSet.removeAll(removeThese);
		}
		//drawAxis(gl);
    }
    
    public static final double PORT_AXIS_X_MINUS_10 = PORT_AXIS_X - 10.0f;
    public static final double PORT_AXIS_X_PLUS_10 = PORT_AXIS_X + 10.0f;
    public static final double PORT_AXIS_X_MINUS_20 = PORT_AXIS_X - 20.0f;
    
    public static final float HIGH_ADDR_HEIGHT = HEIGHT*0.01f;
	public static final float LOW_ADDR_HEIGHT = HEIGHT*0.97f;
	public static final float ADDR_AXIS_X_PLUS_20 = ADDR_AXIS_X + 20; 
    
	void drawAxis(GL gl)
	{
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
		
		
		
		//if(DEBUG)System.out.println("VisualSignatureView: drawAxis() called");
		GLUT glut = new GLUT();
		
		gl.glColor3f(AXIS_RGB[0], AXIS_RGB[1], AXIS_RGB[2]);
		gl.glPointSize(5.0f);
		
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(PORT_AXIS_X, 0.0f);
		gl.glVertex2d(PORT_AXIS_X, HEIGHT);
		gl.glEnd();
		
		
		for(int i = 0; i < MARKED_PORTS.length; ++i)
		{
			//double tmpHeight = 1.0 - Math.pow(MARKED_PORTS[i], .3333) / CUBE_ROOT_65535;
			//tmpHeight *= HEIGHT;
			
			gl.glBegin(GL.GL_LINES);
	        gl.glVertex2d(PORT_AXIS_X_MINUS_10, (float)MARKED_PORTS_HEIGHT[i]);
			gl.glVertex2d(PORT_AXIS_X_PLUS_10, (float)MARKED_PORTS_HEIGHT[i]);
			gl.glEnd();
			
			///////////////////////////////////////////////////////////
			
			if(isMaximized)
			{
				//int width = glut.glutBitmapLength( GLUT.BITMAP_HELVETICA_10, MARKED_PORTS_AS_STRINGS[i] );
				
				gl.glRasterPos2f((float)MARKED_PORTS_X[i], (float)MARKED_PORTS_HEIGHT_MINUS_12[i]);
				//Take a string and make it a bitmap, put it in the 'gl' passed over and pick
				//the GLUT font, then provide the string to show
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, MARKED_PORTS_AS_STRINGS[i]);
			}
		}
		
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(ADDR_AXIS_X, 0.0f);
		gl.glVertex2d(ADDR_AXIS_X, HEIGHT);
		gl.glEnd();
		
		if(isMaximized)
		{
			gl.glRasterPos2f(ADDR_AXIS_X_PLUS_20, HIGH_ADDR_HEIGHT);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "255.255.255.255");
			
			gl.glRasterPos2f(ADDR_AXIS_X_PLUS_20, LOW_ADDR_HEIGHT);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "0.0.0.0" );
		}
	}
	
	void drawLine(GL gl, Line line)
	{
		//System.out.println(line);
		
		gl.glPointSize(5.0f);
		
		Color color = line.getColor();
		float[] rgb = color.getColorComponents(null);
		
		gl.glColor3fv(rgb, 0);
		
		gl.glBegin(GL.GL_LINES);
        gl.glVertex2d(line.x1, line.y1);
		gl.glVertex2d(line.x2, line.y2);
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
			if(!linesSet.contains(line))
				linesSet.add(line);
			else
			{
				linesSet.remove(line);
				// this makes it so the age of the line in the Set is updated
				linesSet.add(line);
			}
		}
	}
	
	public void addLine(float x1, float x2, float y1, float y2)
	{
		addLine( new Line(x1, x2, y1, y2) );
	}
		
	public void addLine(long srcip, int dstport, Color color)
	{
		double percent = 1.0 - (double)srcip/ (double) NUM_ADDR;
		float y1 = HEIGHT * (float)percent;
		
		percent = 1.0 - Math.pow(dstport, 0.3333333) / CUBE_ROOT_65535;
		float y2 = HEIGHT * (float)percent;
		
		addLine( new Line(ADDR_AXIS_X, PORT_AXIS_X, y1, y2, color) );
	}

	public void dispatchResult(IPTableResult ipTableResult)
	{	
		if( ipTableResult != null && ipTableResult.packet !=null &&
		    ipTableResult.packet.srcip !=null && ipTableResult.packet.pdu !=null &&
			ipTableResult.packet.dstip !=null                                       )
		{	
			IPPacket ip = ipTableResult.packet;
			
			String srcIpStr = ip.srcip.toString().substring(1);
			String dstIpStr = ip.dstip.toString().substring(1);
						
			byte[] ipByte = ip.srcip.getAddress();
			
			long srcip = (0x0FF & ipByte[0]);
			srcip <<= 8;
			srcip |= (0x0FF & ipByte[1]);
			srcip <<= 8;
			srcip |= (0x0FF & ipByte[2]);
			srcip <<= 8;
			srcip |= (0x0FF & ipByte[3]);
			srcip &= 0x00000000FFFFFFFFL;
			
			ipByte = ip.dstip.getAddress();
			
			long dstip = (0x0FF & ipByte[0]);
			dstip <<= 8;
			dstip |= (0x0FF & ipByte[1]);
			dstip <<= 8;
			dstip |= (0x0FF & ipByte[2]);
			dstip <<= 8;
			dstip |= (0x0FF & ipByte[3]);
			dstip &= 0x00000000FFFFFFFFL;
			
			if(ip.pdu.getClass().getName().equals("edu.gatech.csc.visualfirewall.data.TCPPacket"))
			{
				if(DEBUG)System.out.println("ip.pdu.getClass().getName(): "+ip.pdu.getClass().getName());
				
				if(VisualFirewall.localIPAddr.equals(dstIpStr))
				{
					// incoming packet
					TCPPacket tcp = (TCPPacket)ip.pdu;
					addLine(srcip, tcp.dstport, TCP_LINE_COLOR);
				}
				else if(VisualFirewall.localIPAddr.equals(srcIpStr))
				{
					// outgoing packet
					TCPPacket tcp = (TCPPacket)ip.pdu;
					addLine(dstip, tcp.srcport, TCP_LINE_COLOR);
				}
			}
			else if(ip.pdu.getClass().getName().equals("edu.gatech.csc.visualfirewall.data.UDPPacket"))
			{	
				if(VisualFirewall.localIPAddr.equals(dstIpStr))
				{
					// incoming packet
					UDPPacket udp = (UDPPacket)ip.pdu;
					addLine(srcip, udp.dstport, UDP_LINE_COLOR);
				}
				else if(VisualFirewall.localIPAddr.equals(srcIpStr))
				{
					// outgoing packet
					UDPPacket udp = (UDPPacket)ip.pdu;
					addLine(dstip, udp.srcport, UDP_LINE_COLOR);
				}	
			}
		}
	}

	
}
