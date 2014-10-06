/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall;

import edu.gatech.csc.visualfirewall.view.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.sun.opengl.util.Animator;
import javax.media.opengl.GLAutoDrawable;
import edu.gatech.csc.visualfirewall.datasource.*;

/**
 * @author chris Mar 31, 2005 VisualFirewall
 */
public class VisualFirewall extends JFrame implements ActionListener {
	public JSplitPane jSplitPane;

	public JPanel mainJPanel;

	public JPanel sideJPanel;

	public static final Rectangle INITIAL_VIEW_RECTANGLE = new Rectangle(0, 0,
			3000, 3000);

	public static boolean needBounds = true;

	public static final int NUMVIEWS = 4;
	
	public static String localIPAddr;
    public static InetAddress localInetAddress = null;
    
	public AbstractView[] views = new AbstractView[NUMVIEWS];

	public int[] perm = new int[NUMVIEWS];

	public Component[] canvases = new Component[NUMVIEWS];

	public Rectangle[] bounds = new Rectangle[NUMVIEWS];

	public int mainviewindex = 0;
	
	public FirewallLog ipTablesLog;
	public SnortLog snortLog;
	
	private static final int PONG = 2;
	private static final int VISUAL_SIGNATURE = 1;
	private static final int STATISTICS = 3;
	private static final int IDS = 0;

	public Component visualSignature; 
	public Component pong;
	public Component ids;
	public Component statistics;
	
	public static Color BG_COLOR = new Color(0.1f, 0.1f, 0.1f);
	public static Color FG_COLOR = new Color(0.9f, 0.9f, 0.9f);

	public static Properties props;
	
	public VisualFirewall() {
		super("Visual Firewall Alpha");
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		
		try
		{
			localIPAddr = getHostIP();
						
			if( props.getProperty("VisualFirewall.fakeip").equals("true"))
            	localIPAddr = props.getProperty("VisualFirewall.fakeipaddr");
            localInetAddress = InetAddress.getByName(localIPAddr);
		}
		catch(Exception e)
		{
			if(localIPAddr == null)
			{
				e.printStackTrace();
				System.out.println("Error: Could not determine the host's IP.  Exiting...");
				System.exit(1);
			}
		}
		
		System.out.println("Local IP = "+localIPAddr);
		
		mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		sideJPanel = new JPanel();
		sideJPanel.setLayout(new GridLayout(NUMVIEWS - 1, 1));

		jSplitPane = new JSplitPane();
		jSplitPane.setDividerLocation(607);
		jSplitPane.setLeftComponent(mainJPanel);
		jSplitPane.setRightComponent(sideJPanel);
		getContentPane().add(jSplitPane, BorderLayout.CENTER);

		int i = 0;
		views[PONG] = new PongView(INITIAL_VIEW_RECTANGLE);
		pong = views[PONG].getCanvas();
		
		views[VISUAL_SIGNATURE] = new VisualSignatureView(INITIAL_VIEW_RECTANGLE);
		visualSignature = views[VISUAL_SIGNATURE].getCanvas();
		
		views[STATISTICS] = new StatisticsView(INITIAL_VIEW_RECTANGLE);
		statistics = views[STATISTICS].getCanvas();
		
		views[IDS]        = new IDSAlarmView(INITIAL_VIEW_RECTANGLE);
		ids = views[IDS].getCanvas();
		
		views[0].isMaximized = true;
		
		for (i = 0; i < NUMVIEWS; ++i) {
			perm[i] = i;
			canvases[i] = views[i].getCanvas();
			canvases[i].addMouseListener(new VFW_MouseListener(this));

			if (i == 0)
				mainJPanel.add(canvases[i], BorderLayout.CENTER);
			else
				sideJPanel.add(canvases[i]);
		}
		
		
		((StatisticsView)views[STATISTICS]).addMouseListener(new VFW_MouseListener(this));
		//((IDSAlarmView)views[IDS]).addMouseListener(new VFW_MouseListener(this));
				
		pack();
		
		// set up the Data Gathering/Parsing agents
		//ipTablesLog = new IPTablesLog();
		if ( props.getProperty("VisualFirewall.ipfw").equals("true") )
			ipTablesLog = new IPFWLog(new File( props.getProperty("VisualFirewall.iptableslog" ) ) );
		else
			ipTablesLog = new IPTablesLog(new File( props.getProperty("VisualFirewall.iptableslog" ) ));

		ipTablesLog.addIPTableResultListener( views[STATISTICS]); //stats view
		ipTablesLog.addIPTableResultListener( views[VISUAL_SIGNATURE]); // VisSig view
		ipTablesLog.addIPTableResultListener( views[PONG]); // Pong view
		
		snortLog = new SnortLog(new File(props.getProperty("VisualFirewall.snortlog" )));
		//snortLog.addSnortAlarmListener( views[STATISTICS]); //stats view
		snortLog.addSnortAlarmListener( views[IDS]); // IDS Alarm view
		
		this.addWindowListener(new VFW_WindowListener(this));
		
		javax.swing.Timer timer = new javax.swing.Timer(1000, this);
		timer.start();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		jSplitPane.setDividerLocation(jSplitPane.getDividerLocation());
		jSplitPane.updateUI();
	}
	
	public static String getHostIP() throws Exception
	{
		String ip = props.getProperty("VisualFirewall.ipaddress");
		if(ip != null)
			return ip;

		String netwInterface = props.getProperty("VisualFirewall.networkinterface"); 
				
		// This "try" block determines the IP address of localhost (not 127.0.0.1).
		try{
			//boolean found = false;
			Enumeration netInterfaces;
			
			if(netwInterface != null)
			{
				Vector v = new Vector();
				v.add(NetworkInterface.getByName(netwInterface));
				netInterfaces = v.elements();
			}
			else
			{
				netInterfaces = NetworkInterface.getNetworkInterfaces();
			}
			
			while(netInterfaces.hasMoreElements())
			{
				NetworkInterface ni = (NetworkInterface)netInterfaces.nextElement();
				Enumeration ipAddrs = ni.getInetAddresses();
				
				while(ipAddrs.hasMoreElements())
				{
					InetAddress i = (InetAddress) ipAddrs.nextElement();
					ip = i.getHostAddress();
					
					if(ip.matches("\\d+\\.\\d+\\.\\d+\\.\\d+") && !ip.equals("127.0.0.1"))
					{
							return ip;
					}
				}
			}

		}catch(Exception e)
		{
			throw e;
		}
		
		return ip;
	}

	public void centerWindow(Component frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();

		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;

		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;

		//frame.setLocation((screenSize.width - frameSize.width) >> 1,
		//		(screenSize.height - frameSize.height) >> 1);
		
		// This is just for Jason's Machine
		frame.setLocation((screenSize.width - frameSize.width) >> 1,
				((screenSize.height - frameSize.height) >> 1) - 100);
	}

	public static void main(String[] args) {

		
		props = new java.util.Properties();
        try {
            java.io.File propfile = new java.io.File( "VisualFirewall.properties" );
            if ( propfile.exists() )
                props.load( new java.io.FileInputStream( propfile ) );
            else {
            		System.err.println("Properties file not found.");
            		System.exit(-1);
            }
        	
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( -1 );
        }

       
		VisualFirewall visFW =  new VisualFirewall();
		visFW.setSize(850, 728);
		visFW.centerWindow(visFW);
		visFW.setResizable(false);
		visFW.setVisible(true);
		
		try
		{
            Thread.sleep(2000); 
        }
		catch (InterruptedException ie) {}
		
		
		Animator animator1 = new Animator((GLAutoDrawable)visFW.views[VISUAL_SIGNATURE].getGLCanvas() );
		animator1.start();
		
		Animator animator2 = new Animator((GLAutoDrawable)visFW.views[PONG].getGLCanvas() );
		animator2.start();
		
		Animator animator3 = new Animator((GLAutoDrawable)visFW.views[IDS].getGLCanvas() );
		animator3.start();
		
		new Thread(visFW.snortLog).start();
		new Thread(visFW.ipTablesLog).start();		 
	}
}

