/*
 * Created on Mar 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.gatech.csc.visualfirewall.datasource;

import com.voytechs.jnetstream.io.StreamFormatException;
import com.voytechs.jnetstream.io.EOPacketStream;
import com.voytechs.jnetstream.io.EOPacket;
import com.voytechs.jnetstream.io.RawformatInputStream;
import com.voytechs.jnetstream.io.PacketInputStream;
import com.voytechs.jnetstream.npl.SyntaxError;
import com.voytechs.jnetstream.primitive.MacAddressPrimitive;
import com.voytechs.jnetstream.primitive.IpAddressPrimitive;
import com.voytechs.jnetstream.primitive.PrimitiveException;
import edu.gatech.csc.visualfirewall.data.*;
import edu.gatech.csc.visualfirewall.data.listener.AbstractPacketListener;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.event.EventListenerList;


/**
 * @author chris
 * Mar 30, 2005
 * PcapFile
 */
public class PcapFile extends AbstractDataSource {
	EventListenerList abstractPacketListeners = new EventListenerList();
	
	public PcapFile( File input ) {
		this.input = input;
		this.produces = AbstractPacket.class;
	}
	
	public void run() {
        try {
            /* Opens up the capture file as an input stream. */
            PacketInputStream in =
                new RawformatInputStream(input.getAbsolutePath());


            /* Loop exists when EOPacketStream exception is thrown */
            while (true) {

                /* Aligns the position of the stream at beginning of packet */
                in.nextPacket();

                /* Returns the name of the first header */
                String linkType = in.getLinkType();

                if (linkType.equals("Ethernet") == true) {

                    /*
                     * Read 6 bytes (48 bits).
                     *
                     * Alternative is to read all the data yourself, but then
                     * you have to take care of those pescky details such as
                     * how to take care of unsigned values using signed data
                     * types, etc... All of this is has already been done
                     * for you with various library classes.
                     *
                     * I.e.
                     * byte[] dst = new byte[6];
                     * for (int i = 0; i < 6; i ++) {
                     *  dst[i] = in.readByte();
                     * }
                     *
                     */
                    MacAddressPrimitive dst = new MacAddressPrimitive();
                    dst.setValue(in);

                    MacAddressPrimitive src = new MacAddressPrimitive();
                    src.setValue(in);

                    int etherProtocol = in.readUnsignedShort();

                    // Now check if its IP protocol
                    if (etherProtocol == 0x800) {
                        int version =       in.readBits(4);
                        int hlen =          in.readBits(4);
                        int precedence =    in.readBits(3);
                        int delay =         in.readBits(1);
                        int throughtput =   in.readBits(1);
                        int reliability =   in.readBits(1);
                        in.readBits(2);     // Reserved 2 bits

                        int length =        in.readUnsignedShort();
                        int id =            in.readUnsignedShort();

                        in.readBits(1);     // Reserved 1 flag bit

                        int doNotFragment = in.readBits(1);
                        int moreFragments = in.readBits(1);

                        int offset =        in.readBits(13);
                        int timeToLive =    in.readUnsignedByte();
                        int ipProtocol =    in.readUnsignedByte();
                        int checksum =      in.readUnsignedShort();

                        IpAddressPrimitive source = new IpAddressPrimitive();
                        source.setValue(in);

                        IpAddressPrimitive destination =
                            new IpAddressPrimitive();
                        destination.setValue(in);

                        // Skipping all the options, etc...

                        //System.out.print("IP");
                        //System.out.print(" " + source);
                        //System.out.print(" -> " + destination);

                        // Now check for TCP protocol
                        if (ipProtocol == 6) {
                      	  int spt = in.readUnsignedShort();
                    	      int dpt = in.readUnsignedShort();
                           //System.out.println(" protocol=TCP spt="+spt+" dpt="+dpt);
                    	      fireAbstractPacket( new IPPacket( InetAddress.getByName(source.toString()), InetAddress.getByName(destination.toString()), new TCPPacket( spt, dpt, 0, length ), length ) );
                        } else if (ipProtocol == 17) {
                        	  int spt = in.readUnsignedShort();
                        	  int dpt = in.readUnsignedShort();
                        	  int len = in.readUnsignedShort();
                            //System.out.println(" protocol=UDP spt="+spt+" dpt="+dpt+" len="+len);
                        	  fireAbstractPacket( new IPPacket( InetAddress.getByName(source.toString()), InetAddress.getByName(destination.toString()), new UDPPacket( spt, dpt, len ), length ) );
                     	                                 
                        } else if (ipProtocol == 1) {
                        	   int type = in.readUnsignedByte();
                        	   int code = in.readUnsignedByte();
                        	   //System.out.println(" protocol=ICMP type="+type+" code="+code);
                        	   fireAbstractPacket( new IPPacket( InetAddress.getByName(source.toString()), InetAddress.getByName(destination.toString()), new ICMPPacket( (short)type, (short)code, length ), length ) );

                        } else { // For all other protocols display number
                            //System.out.println(
                            //    " protocol=0x"
                            //    + Integer.toHexString(ipProtocol) );
                        }

                    } else {
                        /*System.out.print("Ethernet");
                        System.out.print(" " + src);
                        System.out.print(" -> " + dst);
                        System.out.println(
                            " protocol=0x"
                            + Integer.toHexString(etherProtocol) );*/
                    }
                } else {
                    //System.out.println("Unsupported packet type " + linkType);
                }
                try {
                		Thread.sleep( (int)(Math.random()*100 ) );
                } catch (Exception e) {
                }
            }
        } catch (StreamFormatException t) {
            t.printStackTrace();
        } catch (EOPacket eo) {
            eo.printStackTrace();
        } catch (EOPacketStream eos) {
            // This is normal condition
        } catch(IOException ie) {
            ie.printStackTrace();
        } catch(SyntaxError se) {
            se.printStackTrace();
        } catch(PrimitiveException pe) {
	    pe.printStackTrace();
	}
	}

	public void addAbstractPacketListener( AbstractPacketListener listener ) {
		abstractPacketListeners.add( AbstractPacketListener.class, listener );
	}
	
	public void removeAbstractPacketListener( AbstractPacketListener listener ) {
		abstractPacketListeners.remove( AbstractPacketListener.class, listener );
	}
	
	protected void fireAbstractPacket( AbstractPacket iptr ) {
		Object[] listeners = abstractPacketListeners.getListenerList();
		int numListeners = listeners.length;
		// TODO: the example was broken, check if 'i' should be incremented by 1 or 2.
		for ( int i = 0; i < numListeners; i += 2 ) {
			if ( listeners[i] == AbstractPacketListener.class )
				((AbstractPacketListener)listeners[i+1]).dispatchPacket( iptr );
		}
	}
	
	public static void main(String[] args) {
    		new PcapFile( new File( "logs/50228-http.pcap" ) ).run();
    }
}
