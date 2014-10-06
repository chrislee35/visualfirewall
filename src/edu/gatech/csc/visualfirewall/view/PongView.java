/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */ 
package edu.gatech.csc.visualfirewall.view;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.GLUT;

import edu.gatech.csc.visualfirewall.VisualFirewall;
import edu.gatech.csc.visualfirewall.data.ICMPPacket;
import edu.gatech.csc.visualfirewall.data.IPTableResult;
import edu.gatech.csc.visualfirewall.data.TCPPacket;
import edu.gatech.csc.visualfirewall.data.UDPPacket;
 


/**
 * @author chris, nic(k), jason
 * Mar 31, 2005
 * PongView
 */
public class PongView extends AbstractView implements GLEventListener, KeyListener {

	boolean DEBUG = false;
	boolean DEBUG2 = false;

	final int MAX_BALLS = 5000;
	
	
	/* color swatch */
	Random rand = new Random();
	
	int colorArrayCount = 0;
	double[][] colorArray = 
				{{30.0/255.0,144/255.0,1}, {143/255.0,188/255.0,143/255.0}, { 255/255.0,255.0/255.0,0}, { 188/255.0,143/255.0,143/255.0 }, { 255/255.0,127/255.0,80/255.0 },{ 219/255.0,112/255.0,147/255.0}, 
				 {0,191/255.0,1}, {46/255.0,139/255.0,87/255.0}, { 255.0/255.0,215/255.0,0},  { 205/255.0,92/255.0,92/255.0 }, { 240/255.0,128/255.0,128/255.0 }, { 199/255.0,21/255.0,133/255.0},
				 {135/255.0,206/255.0,250/255.0}, {60/255.0,179/255.0,113/255.0}, { 238/255.0,221/255.0,130/255.0}, { 139/255.0,69/255.0,19/255.0 }, { 255/255.0,99/255.0,71/255.0 }, { 208/255.0,32/255.0,144/255.0 },  
				 {135/255.0,206/255.0,250/255.0}, {32/255.0,178/255.0,170/255.0}, { 218/255.0,165/255.0,32/255.0}, { 160/255.0,82/255.0,45/255.0 }, { 255/255.0,69/255.0,0 }, { 238/255.0,130/255.0,238/255.0 },    
				 {70/255.0,130/255.0,180/255.0}, {152/255.0,251/255.0,152/255.0}, { 184/255.0,134/255.0,11/255.0}, { 205/255.0,133/255.0,63/255.0 },{ 154/255.0, 1.0, 154/255.0}, { 176/255.0,48/255.0,96/255.0}};
	
	//ICMP traffic count values: echo/reply, dest unreach(net, host, proto, port), 11=timeout, other
	protected int ICMP_IN[] = {0,0,0,0,0,0,0}; 
	protected int ICMP_OUT[] = {0,0,0,0,0,0,0};
	protected int ICMP_ARRAY_SIZE = ICMP_IN.length;
	protected int ICMP_IN_TOTAL = 0;
    protected int ICMP_OUT_TOTAL = 0;
	//pie chart locale
    protected final int ICMP_RADIUS = 80;
    protected float ICMP_X = worldWindowRect.width*0.57f; 	
    protected float ICMP_Y = worldWindowRect.height*0.02f;

    protected final float UDP_INNER_WIDTH = 0.9f;
    
    final int OFFSET = 0;
	protected final int LEFT_WALL_X = 360;
	protected final int R_OFFSET = worldWindowRect.width - 550;
	protected final int WALL_HEIGHT = worldWindowRect.height - OFFSET; 
	protected final int WALL_BOTTOM = OFFSET;
	
	protected static final double TWO_PI = 2 * Math.PI;
	protected static final double ARC_SEGMENT = TWO_PI / 9; // how many circle outline points
	protected static double ICMP_ARC_SEG = TWO_PI / 360; // how many circle outline points
	
	protected static final long NUM_ADDR = 4294967295L;
	protected static final double CUBE_ROOT_65535 = Math.pow(65535, 0.3333333);
	
	//ftp = 21, ssh = 22, http = 80, https = 443
	protected static final String[] OPEN_PORTS = {"21", "ftp", "22", "ssh", "80", "http", "443", "https"};
	protected static final int OPEN_PORT_SPACING = 35;
	protected static final int OPEN_PORT_OFFSET = 200;
	protected static int[] OPEN_PORTS_X_OFFSETS = new int[OPEN_PORTS.length];
	protected static int[] OPEN_PORTS_Y_OFFSETS = new int[OPEN_PORTS.length];
	
	protected static final int[] MARKED_PORTS = {10, 80, 150, 500, 1000, 5000, 10000, 50000};
	protected static int[] MARKED_PORTS_X_OFFSETS = new int[MARKED_PORTS.length];
	protected static int[] MARKED_PORTS_Y_OFFSETS = new int[MARKED_PORTS.length];
	
	protected static final double[] TICK_MARKS = new double[8];
	
	public static final float[] BG_RGB = VisualFirewall.BG_COLOR.getColorComponents(null);
	public static final float[] AXIS_RGB = VisualFirewall.FG_COLOR.getColorComponents(null);
	
	//protected final double FPS = 30.0;
	//protected final int MS_PER_FRAME = (int) Math.round (1000.0 / FPS);
	
	protected long lastUpdateTime;
	
	HashMap addrColorMap = new HashMap(MAX_BALLS);
	
	LinkedList activeBalls = new LinkedList(); 
	LinkedList inActiveBalls = new LinkedList();
	
	
	public PongView( Rectangle worldRect ) {
		super(worldRect);
	
		lastUpdateTime = System.currentTimeMillis();
		
		if(DEBUG)System.out.println("PongView: PongView() called");
		//setWorldWindowRect(worldWindowRect);
		//setWorldWindowChanged(false); 
		
		// get a GLCanvas
		GLCapabilities capabilities = new GLCapabilities();
		setCanvas(new GLCanvas());
		// add a GLEventListener, which will get called when the
		// canvas is resized or needs a repaint
		getGLCanvas().addGLEventListener(this);
		
		// instantiate inActive list
			
		for(int i = 0; i < MAX_BALLS; i++)
			inActiveBalls.add(new PongBall(0,0));
		
		//add key listener
		getGLCanvas().addKeyListener(this);
			
	}
	
	public void init(GLAutoDrawable drawable) {
		
		if(DEBUG)System.out.println("PongView: init() called");
		
		//System.out.println ("init()");
		
		GL gl = getGLCanvas().getGL();
		GLUT glut = new GLUT();

		//Y axis tick marks
		for(int i = 0; i < MARKED_PORTS.length; i++)
		TICK_MARKS[i] = worldWindowRect.height - OFFSET - OPEN_PORT_OFFSET - 
						( worldWindowRect.height - OFFSET*2 - OPEN_PORT_OFFSET)
						*(Math.pow(MARKED_PORTS[i], 0.333) / CUBE_ROOT_65535);
				
		// set erase color 20% GREY
		gl.glClearColor(BG_RGB[0], BG_RGB[1], BG_RGB[2],1);

		for(int i = 1; i < OPEN_PORTS.length; i=i+2){
			int width = glut.glutBitmapLength( GLUT.BITMAP_HELVETICA_10, OPEN_PORTS[i] );
			OPEN_PORTS_X_OFFSETS[i] = LEFT_WALL_X - 35 - (width*5);
			OPEN_PORTS_Y_OFFSETS[i] = worldWindowRect.height - OFFSET - i*OPEN_PORT_SPACING;
		}

		//draw numbers
		for(int i = 0; i < MARKED_PORTS.length; i++){
			String port = (new Integer(MARKED_PORTS[i]) ).toString();
			int width = glut.glutBitmapLength( GLUT.BITMAP_HELVETICA_10, port );
			MARKED_PORTS_X_OFFSETS[i] = LEFT_WALL_X - 35 - (width*5);
			MARKED_PORTS_Y_OFFSETS[i] = (int)(worldWindowRect.height - OFFSET - OPEN_PORT_OFFSET - 
					( worldWindowRect.height - OFFSET*2 - OPEN_PORT_OFFSET)
					*(Math.pow(MARKED_PORTS[i], 0.333) / CUBE_ROOT_65535));
		}
		
		// This is supposed to optimize repeatedly drawn graphics. See 
        // http://fly.cc.fer.hr/~unreal/theredbook/chapter04.html for more info.
        gl.glNewList(1, GL.GL_COMPILE);
        	drawStaticGraphics(gl, glut);
        gl.glEndList();
        
	}
	
	public void display(GLAutoDrawable drawable) 
	{
		if(DEBUG)System.out.println("PongView: display() called");
	
		long inTime = System.currentTimeMillis();
		
		// System.out.println ("display()");
		
		GL gl = getGLCanvas().getGL();
		GLU glu = new GLU();

		// is there a pending world window change?
		if (worldWindowChanged)
			resetWorldWindow(gl, glu);

		//calls gl compiled drawStaticGraphics
		gl.glCallList(1);		
			
		drawICMPChart(gl);	
		
		GLUT glut = new GLUT();
		
		//update and draw every ball
		updateDisplaySimul(gl, glut);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.games.jogl.GLEventListener#reshape(net.java.games.jogl.GLDrawable,
	 *      int, int, int, int)
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		
		if(DEBUG)System.out.println("PongView: reshape() called");
		
		GL gl = getGLCanvas().getGL();
		GLU glu = new GLU();

		// save size for viewport reset
		viewportWidth = width;
		viewportHeight = height;

		resetWorldWindow(gl, glu);
	}

	/*
	 * in display list for optimization:
	 * statically (repeatedly) drawn axises, text, hash marks
	 */
	public void drawStaticGraphics(GL gl, GLUT glut){
//		 load identity matrix
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		// draw the barriers
		gl.glColor3f(AXIS_RGB[0], AXIS_RGB[1], AXIS_RGB[2]);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2f(LEFT_WALL_X, OFFSET);
		gl.glVertex2f(LEFT_WALL_X, worldWindowRect.height - OFFSET);
		gl.glVertex2f(R_OFFSET, OFFSET);
		gl.glVertex2f(R_OFFSET, worldWindowRect.height - OFFSET);
		gl.glEnd();
		
		//draw hashes on barriers
		double height;
		gl.glColor3f(AXIS_RGB[0], AXIS_RGB[1], AXIS_RGB[2]);
		gl.glBegin(GL.GL_LINES);
		for(int i = 0; i < TICK_MARKS.length; i++){
			//System.out.println("["+i+"] "+"tick mark y pixel: "+TICK_MARKS[i]);
			gl.glVertex2f(LEFT_WALL_X-10, (float)TICK_MARKS[i]);
			gl.glVertex2f(LEFT_WALL_X+10, (float)TICK_MARKS[i]);
		}
		gl.glEnd();
		
		
		if(isMaximized){
//			draw open ports
			for(int i = 1; i < OPEN_PORTS.length; i=i+2){
				int width = glut.glutBitmapLength( GLUT.BITMAP_HELVETICA_10, OPEN_PORTS[i] );
				gl.glRasterPos2f(OPEN_PORTS_X_OFFSETS[i], OPEN_PORTS_Y_OFFSETS[i]);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, OPEN_PORTS[i]);
				
				//hashes
				gl.glBegin(GL.GL_LINES);
				gl.glVertex2f(LEFT_WALL_X-10, worldWindowRect.height - OFFSET - i*OPEN_PORT_SPACING);
				gl.glVertex2f(LEFT_WALL_X+10, worldWindowRect.height - OFFSET - i*OPEN_PORT_SPACING);
				gl.glEnd();
				
				//System.out.println("i-1: "+ i);
			}
					
			//draw numbers
			for(int i = 0; i < MARKED_PORTS.length; i++){
				String port = (new Integer(MARKED_PORTS[i]) ).toString();
				gl.glRasterPos2f(MARKED_PORTS_X_OFFSETS[i], MARKED_PORTS_Y_OFFSETS[i]);
				glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, port);
			}
		
									
			gl.glRasterPos2f(worldWindowRect.width*0.51f, worldWindowRect.height*0.05f);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "ICMP");
			gl.glRasterPos2f(worldWindowRect.width*0.52f, worldWindowRect.height*0.03f);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "IN");
			
			gl.glRasterPos2f(worldWindowRect.width*0.65f, worldWindowRect.height*0.05f);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "ICMP");
			gl.glRasterPos2f(worldWindowRect.width*0.655f, worldWindowRect.height*0.03f);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "OUT");
			
			/*
			gl.glRasterPos2f(worldWindowRect.width*0.095f, worldWindowRect.height-10);
			glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_10, "0");
			
			gl.glRasterPos2f(worldWindowRect.width*0.084f, worldWindowRect.height*0.08f);
			glut.glutBitmapString(gl, GLUT.BITMAP_HELVETICA_10, "65535");
			*/
			
			gl.glRasterPos2f(R_OFFSET+30, worldWindowRect.height-40);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "0.0.0.0");
			
			gl.glRasterPos2f(R_OFFSET+30, 40);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "255.255.255.255");
		}
	}
	
	/*
	 * draws the ICMP chart
	 */
	public void drawICMPChart(GL gl){
		
		//center of circle
		float cx = ICMP_X + ICMP_RADIUS;
        float cy = ICMP_Y + ICMP_RADIUS;
        float x,y;
        double curAngle, stopAngle;

        //incoming ICMP
        curAngle = 0;
        gl.glBegin (GL.GL_POLYGON);
        gl.glVertex2f(cx,cy);
        for(int i = 0; i < ICMP_ARRAY_SIZE; i++){
        	
        	if(ICMP_IN_TOTAL!=0)
        		stopAngle = curAngle + ((float) ICMP_IN[i] / (float)ICMP_IN_TOTAL)*TWO_PI;
        	else{
        		stopAngle = curAngle + TWO_PI;
        		i = 6;
    		}
        	
        	while(curAngle <= stopAngle){ 
        		x = (float) (cx + (Math.sin(curAngle) * ICMP_RADIUS));
            	y = (float) (cy + (Math.cos(curAngle) * ICMP_RADIUS));	 
        	
            	switch(i){
            		case 0:
            			gl.glColor3f(1, 0, 0);
            			break;
            		case 1:
            			gl.glColor3f(0, 1, 0);
            			break;
            		case 2:
            			gl.glColor3f(0, 0, 1);
            			break;
            		case 3:
            			gl.glColor3f(1, 1, 0);
            			break;
            		case 4:
            			gl.glColor3f(0, 1, 1);
            			break;
            		case 5:
        				gl.glColor3f(1, 0, 1);
            			break;
        			case 6:
        				gl.glColor3f(1, 1, 1);
            			break;            				
            		default:
            			gl.glColor3f(0.5f, 0.5f, 0.5f);
            			break;
            	}
            
              	gl.glVertex2f(x,y); 
              	//System.out.println("ICMP IN ("+x+","+y+")");
              	
            	curAngle = curAngle + ICMP_ARC_SEG;
        	}
        }
        gl.glEnd();
        
        //outgoing ICMP
        curAngle = 0;        
        cx += 400;
        gl.glBegin (GL.GL_POLYGON);
        gl.glVertex2f(cx,cy);
        for(int i = 0; i < ICMP_ARRAY_SIZE; i++){
        	
        	if(ICMP_OUT_TOTAL != 0)
        		stopAngle = curAngle + ((float) ICMP_OUT[i] / (float)ICMP_OUT_TOTAL)*TWO_PI;
        	else{
        		stopAngle = curAngle + TWO_PI;
        		i = 6;
        	}
        	
        	//System.out.println("cur: "+curAngle+ " stop: "+stopAngle);
        	while(curAngle <= stopAngle){ 
        		x = (float) (cx + (Math.sin(curAngle) * ICMP_RADIUS));
            	y = (float) (cy + (Math.cos(curAngle) * ICMP_RADIUS));	 
        	
            	switch(i){
            		case 0:
            			gl.glColor3f(1, 0, 0);
            			break;
            		case 1:
            			gl.glColor3f(0, 1, 0);
            			break;
            		case 2:
            			gl.glColor3f(0, 0, 1);
            			break;
            		case 3:
            			gl.glColor3f(1, 1, 0);
            			break;
            		case 4:
            			gl.glColor3f(0, 1, 1);
            			break;
            		case 5:
        				gl.glColor3f(1, 0, 1);
            			break;
            		case 6:
        				gl.glColor3f(1, 1, 1);
            			break;
            		default:
            			gl.glColor3f(0.5f, 0.5f, 0.5f);
            			break;
            	}
              	gl.glVertex2f(x,y);            
            	curAngle = curAngle + ICMP_ARC_SEG;
        	}
        }
        gl.glEnd();
        
		  	
	}
	
	
	/* 
	 * update and display each packet
	 */
	public void updateDisplaySimul(GL gl, GLUT glut){

		// calculate elapsed time since last update
        long elapsed = System.currentTimeMillis() - lastUpdateTime;
        // System.out.println ("elapsed ms = " + elapsed);
        double elapsedSec = elapsed / 1000d;
                
		PongBall ball;
		float newX, newY;
	
		//color from IP mapping
		double color[];

		
		synchronized(activeBalls){
			for(ListIterator li = activeBalls.listIterator(); li.hasNext();){
				ball = (PongBall) li.next();
				
											
				// update ball location
				if(ball.movingLeft)
					newX = ball.getXcoord() - (float) (ball.getVelocity() * elapsedSec);
				else //moving right
					newX = ball.getXcoord() + (float) (ball.getVelocity() * elapsedSec);
				newY = ball.slope * newX + ball.lineOffset; 
				ball.setXYcoord(newX, newY);
												
				//collision detection on top / bottom for bouncing balls
				if( ball.reflecting &&					
					( (ball.bounceCounter == 0)	
					   || (ball.getYcoord() >= worldWindowRect.height)
					   || (ball.getYcoord() <= ball.getRadius()) 
					   || (ball.getYcoord() <= ball.getRadius())
					   || (ball.movingLeft && ball.getXcoord() <= (R_OFFSET + LEFT_WALL_X)/2) 
					   || (!ball.movingLeft && ball.getXcoord() >= (R_OFFSET + LEFT_WALL_X)/2) ) )
				{				
						//tired of bouncing
						if(DEBUG2) System.out.println("Deactivating ball: tired of bouncing");				
						inActiveBalls.add(ball);
						li.remove();
				}
				//collision on left / right axises
				else if(ball.isRejected() &&
						((ball.movingLeft && ball.getXcoord() <= ball.xEnd) 
						|| (!ball.movingLeft && ball.getXcoord() >= ball.xEnd)) ){
					
					//firewall rules
					//bounce the ball
					ball.setColor(0.5f, 0.5f, 0.5f);
					ball.reflectVectorSlope();
				}
				// pass through
				else if( (ball.movingLeft && ball.getXcoord() <= LEFT_WALL_X - 200) 
						|| (!ball.movingLeft && ball.getXcoord() >= R_OFFSET + 200) )
					{
					if(DEBUG2) System.out.println("Deactivating ball: out of bounds");				
					inActiveBalls.add(ball);
					li.remove();
				}
				//display the ball
				else{	
					if(DEBUG2) System.out.println("Drawing my ball");				
					//display ball
					if(ball.drawCircle){
						//draw circle
						double radius = ball.getRadius();
				        double cx = ball.getXcoord() + radius;
				        double cy = ball.getYcoord() + radius;
				        
				        //System.out.println ("drawCircle at " + cx + "," + cy);
				        float x, y;
				        
				        gl.glBegin (GL.GL_POLYGON);
				        if(ball.reflecting || ball.isUDP)
				        	gl.glColor4f(ball.getRed(), ball.getGreen(), ball.getBlue(),1);
				        else				        	
				        	gl.glColor4d(ball.textRed, ball.textGreen, ball.textBlue, 1);
				        for (double theta = 0; theta < TWO_PI; theta += ARC_SEGMENT) {
				            x = (float)(cx + (Math.sin(theta) * radius));
				            y = (float)(cy + (Math.cos(theta) * radius));				            
				            gl.glVertex2f(x,y);
			        	}//end for loop
				        gl.glEnd();

				        if(ball.isUDP && !ball.reflecting){
				        	double borderRadius = radius * UDP_INNER_WIDTH;
				        	gl.glBegin (GL.GL_POLYGON);
					        for (double theta = 0; theta < TWO_PI; theta += ARC_SEGMENT) {
						        //inner ball
					            x = (float)(cx + (Math.sin(theta) * borderRadius));
					            y = (float)(cy + (Math.cos(theta) * borderRadius));
					            gl.glColor4d(ball.textRed, ball.textGreen, ball.textBlue, 1);
					            gl.glVertex2f(x,y);
					        }//end for loop
				            gl.glEnd();
				        }
					}
					else{ 
						//draw rectangle
						double radius = ball.getRadius();
						double ballX = ball.getXcoord();
						double ballY = ball.getYcoord();
						
						gl.glBegin (GL.GL_QUADS);       
				        if(ball.reflecting || ball.isUDP)
				        	gl.glColor4f(ball.getRed(), ball.getGreen(), ball.getBlue(),1);
				        else
				        	gl.glColor4d(ball.textRed, ball.textGreen, ball.textBlue, 1);
				        gl.glVertex2d(ballX + radius, ballY + radius);
				        gl.glVertex2d(ballX + radius, ballY - radius);
				        gl.glVertex2d(ballX - radius, ballY - radius);
				        gl.glVertex2d(ballX - radius, ballY + radius);
				        gl.glEnd();
				        
				        if(ball.isUDP && !ball.reflecting){
				        	double borderRadius = radius * UDP_INNER_WIDTH;
				           	//inner ball
					        gl.glBegin(GL.GL_QUADS);
					        gl.glColor4d(ball.textRed, ball.textGreen, ball.textBlue, 1);
					        gl.glVertex2d(ballX + borderRadius, ballY + borderRadius);
					        gl.glVertex2d(ballX + borderRadius, ballY - borderRadius);
					        gl.glVertex2d(ballX - borderRadius, ballY - borderRadius);
					        gl.glVertex2d(ballX - borderRadius, ballY + borderRadius);
					        gl.glEnd();					        
				        }
					}
					
					// display end string (port or IP:PORT) on top of balls
					if(!ball.reflecting){
											
						//colorize text yo
						//gl.glEnable(GL.GL_BLEND);
						gl.glColor4d(ball.textRed, ball.textGreen, ball.textBlue, 1);							
						if(isMaximized){
							if(ball.isIncoming()){					
								gl.glRasterPos2f(R_OFFSET, (float) ball.yOrigin);
								glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, ball.getIPAddr() + ":" + ball.getSrcPort());
								gl.glRasterPos2f(LEFT_WALL_X + 15, (float)ball.yEnd);
								glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(ball.getDstPort()));
							}
							else{
								gl.glRasterPos2f(R_OFFSET, (float) ball.yEnd);
								glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, ball.getIPAddr() + ":" + ball.getDstPort());
								gl.glRasterPos2f(LEFT_WALL_X + 15, (float) ball.yOrigin);
								glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, String.valueOf(ball.getSrcPort()));						
							}
						}
					}
				}
			}
		}//synchronized
		
		if(isMaximized){
			gl.glColor3d(0.118, 0.565, 1);		
			gl.glRasterPos2f(worldWindowRect.width*0.21f, worldWindowRect.height*0.03f);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "SPEED: "+ PongBall.velocity / 1000);
		}
		lastUpdateTime = System.currentTimeMillis();
	}
	
	
	
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		
		if(DEBUG)System.out.println("PongView: displayChanged() called");
		// TODO Auto-generated method stub

	}
	
	/*
	 * maps port # to pixel on left barrier
	 */
	protected double mapPort(int port){		
		for(int i = 0; i < OPEN_PORTS.length; i = i+2){
			if (OPEN_PORTS[i].equals(Integer.toString(port))){
				return worldWindowRect.height - OFFSET - (i+1)*OPEN_PORT_SPACING;
			}				
		}
			
		return  worldWindowRect.height - OFFSET - OPEN_PORT_OFFSET - 
				( worldWindowRect.height - OFFSET*2 - OPEN_PORT_OFFSET)*(Math.pow(port, 0.333) / CUBE_ROOT_65535);
	}
	
	/*
	 * maps ip addr to pixel on right barrier
	 */
	protected double mapIP(InetAddress ip){
			
		byte[] ipByte = ((Inet4Address) ip).getAddress();
		
		long addr = ((long)(((char)ipByte[0])&0xff)<<24) |
			((long)(((char)ipByte[1])&0xff)<<16) | 
			((long)(((char)ipByte[2])&0xff)<<8) | 
			(long)((char)ipByte[3])&0xff;
		addr &= 0x00000000ffffffff;
		//System.out.println( ip +" "+addr );
		//addr = NUM_ADDR/2;
		double tmp = worldWindowRect.height - OFFSET - ( worldWindowRect.height - OFFSET*2)*((double)addr / (double)NUM_ADDR);		
		return   tmp;
		 		
	}
			
	public void dispatchResult(IPTableResult ipTR)
	{
		//System.out.println("PongView: " + ipTR.toString() );
		
		PongBall ball = (PongBall) inActiveBalls.getFirst();
			
		if( ((Inet4Address) ipTR.packet.srcip).getHostAddress().equals(VisualFirewall.localIPAddr)){
			ball.setIncoming(false);
		}
		else if( ((Inet4Address) ipTR.packet.dstip).getHostAddress().equals(VisualFirewall.localIPAddr)){
			ball.setIncoming(true);
			
		}
		else{
			//System.out.println("Disregard packet...");
			return;
		}			
		
		
		
		//check if accepted (enter) or rejected (explode)
		if(ipTR.accepted)
			ball.setRejected(false);
		else
			ball.setRejected(true);
		
		
		if(ipTR.packet.pdu != null)
		{
			if(ipTR.packet.length < 500)
				ball.setRadius(15);
			else
				ball.setRadius(Math.sqrt(ipTR.packet.length*0.645));
						
			//System.out.println("ip len: " +ipTR.packet.length);
			//System.out.println("radius: "+ball.getRadius());
						
			if(ipTR.packet.pdu.getClass() == TCPPacket.class){
				
				if (DEBUG) System.out.println("TCP PACKET!");
		
				TCPPacket tcp = (TCPPacket) ipTR.packet.pdu;
						
				//set color black
				ball.setColor(0,1,0);
				
				ball.isTCP = true;
				ball.isUDP = false;
				ball.isICMP = false;
			
				//set the vectors (end, begin) points for balls
				if(ball.isIncoming()){
					ball.setVector(R_OFFSET - ball.getRadius(), mapIP(ipTR.packet.srcip), LEFT_WALL_X + ball.getRadius(), mapPort(tcp.dstport));
					ball.setIPPorts(((Inet4Address) ipTR.packet.srcip).getHostAddress(), tcp.srcport, tcp.dstport);					
				}
				else{
					ball.setVector(LEFT_WALL_X + ball.getRadius(), mapPort(tcp.srcport), R_OFFSET - ball.getRadius(), mapIP(ipTR.packet.dstip));
					ball.setIPPorts(((Inet4Address) ipTR.packet.dstip).getHostAddress(), tcp.srcport, tcp.dstport);
				}
				
				//ball.textColor = mapIPColor(ball.getIPAddr());				
				
			}			
			else if(ipTR.packet.pdu.getClass() == UDPPacket.class){
				if (DEBUG) System.out.println("UDP PACKET!");
			
				UDPPacket udp = (UDPPacket) ipTR.packet.pdu;
				
				//set color white
				ball.setColor(0.9f,0.9f,0.9f);
				
				ball.isUDP = true;
				ball.isTCP = false;
				ball.isICMP = false;
				
				if(ball.isIncoming()){
					ball.setVector(R_OFFSET - ball.getRadius(), mapIP(ipTR.packet.srcip), LEFT_WALL_X + ball.getRadius(), mapPort(udp.dstport));
					ball.setIPPorts(((Inet4Address) ipTR.packet.srcip).getHostAddress(), udp.srcport, udp.dstport);
				}
				else{
					ball.setVector(LEFT_WALL_X + ball.getRadius(), mapPort(udp.srcport), R_OFFSET - ball.getRadius(), mapIP(ipTR.packet.dstip));
					ball.setIPPorts(((Inet4Address) ipTR.packet.dstip).getHostAddress(), udp.dstport, udp.srcport);
				}			
			
			}			
			else if(ipTR.packet.pdu.getClass() == ICMPPacket.class){
				if (DEBUG) System.out.println("ICMP PACKET!");
				
				if(ball.isIncoming())
					ICMP_IN_TOTAL++;
				else
					ICMP_OUT_TOTAL++;
				
				ICMPPacket icmp = (ICMPPacket) ipTR.packet.pdu;
				
				ball.isTCP = false;
				ball.isUDP = false;
				ball.isICMP = true;

				if(icmp.type == 0 || icmp.type == 8){
					if(ball.isIncoming())
						ICMP_IN[0]++;
					else
						ICMP_OUT[0]++;					
				}
				else if(icmp.type == 3){
					//codes!
					if(icmp.code == 0){
						//net unr
						if(ball.isIncoming())
							ICMP_IN[1]++;
						else
							ICMP_OUT[1]++;
					}
					else if(icmp.code == 1){
						//host unr
						if(ball.isIncoming())
							ICMP_IN[2]++;
						else
							ICMP_OUT[2]++;
					}
					else if(icmp.code == 2){
						//proto unr
						if(ball.isIncoming())
							ICMP_IN[3]++;
						else
							ICMP_OUT[3]++;
					}
					else if(icmp.code == 3){
						//port unr
						if(ball.isIncoming())
							ICMP_IN[4]++;
						else
							ICMP_OUT[4]++;
					}
					else{
						//don't care - consider other?
						if(ball.isIncoming())
							ICMP_IN[6]++;
						else
							ICMP_OUT[6]++;
					}
				}
				else if(icmp.type == 11){
					//time exceeded
					if(ball.isIncoming())
						ICMP_IN[5]++;
					else
						ICMP_OUT[5]++;
				}
				else{
					//other type, don't draw anything
					if(ball.isIncoming())
						ICMP_IN[6]++;
					else
						ICMP_OUT[6]++;
				}				
				
				//System.out.println("type: "+icmp.type+" code: "+icmp.code);
			}		
			else{
				System.out.println("JUST AN IP PACKET.");
				return;
			}
		}//END IP_TR IF
		
		
		//System.out.println("ICMP IN ("+ICMP_IN_TOTAL+") e:"+ICMP_IN[0]+" n: "+ICMP_IN[1]+" h: "+ICMP_IN[2]+" pr: "+ICMP_IN[3]+" pt: "+ICMP_IN[4]+" ex: "+ICMP_IN[5]+" ot: "+ICMP_IN[6]);
		//System.out.println("ICMP OUT ("+ICMP_OUT_TOTAL+") e:"+ICMP_OUT[0]+" n: "+ICMP_OUT[1]+" h: "+ICMP_OUT[2]+" pr: "+ICMP_OUT[3]+" pt: "+ICMP_OUT[4]+" ex: "+ICMP_OUT[5]+" ot: "+ICMP_IN[6]);
		
		//randomly choose IP color
		if(!addrColorMap.containsKey((String) ball.getIPAddr())){
			
			//Color c = new Color (rand.nextInt (255), rand.nextInt (255), rand.nextInt (255));
			Color c = new Color((float)colorArray[colorArrayCount][0], (float)colorArray[colorArrayCount][1], (float)colorArray[colorArrayCount][2]);
			ball.setTextColor(c);
			addrColorMap.put((String) ball.ipaddr, (Color) c);
			
			colorArrayCount++;
			colorArrayCount %= 30;
		}
		else
			ball.setTextColor((Color) addrColorMap.get((String) ball.getIPAddr()));
		
		//put it in a collection to be checked by display()
		
		synchronized(activeBalls){
			if(DEBUG) System.out.println("Adding new ball to queue");
			if(!ball.isICMP)
				activeBalls.add(inActiveBalls.removeFirst());
			//else
				//inActiveBalls.add(ball);
		}
		
	}//end dispatchResult()
	
	
	/*
	 * Allows user to change the velocity of all balls
	 */
	public void keyTyped(KeyEvent ke){
		
		// > 0 inc, stop at 5000
		// < 
		
		if(ke.getKeyChar()=='a'){
			if(PongBall.velocity >= 0 && PongBall.velocity < 5000)
				PongBall.velocity += 500;
		}			
		else if(ke.getKeyChar()=='s'){
			if(PongBall.velocity > 0 && PongBall.velocity <= 5000)
				PongBall.velocity -= 500;
		}
		
		
		
	}
	
	public void keyPressed(KeyEvent ke){
		
	}
	
	public void keyReleased(KeyEvent ke){
		
	}

	
}//end class
