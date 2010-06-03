/*
 * JBoss, Home of Professional Open Source
 * Copyright XXXX, Red Hat Middleware LLC, and individual contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.mobicents.media.server.impl.rtp.sdp;

import org.mobicents.media.Format;
import org.mobicents.media.server.spi.MediaType;

/**
 *
 * @author kulikov
 * @author amit bhayani
 */
public class MediaDescriptor {
    
    protected MediaType mediaType;
    private int port;
    private String profile;
    
    private FormatParser fmtParser;
    
    private int[] fmt = new int[15];
    private Format[] formats = new Format[15];
    
    private int count;
    
    private String[] attributes = new String[15];
    private int aCount;
    
    private String mode;
    
    private Connection connection;
    
    public MediaDescriptor(String m) {
        int pos1 = m.indexOf(61);
        int pos2 = m.indexOf(32, pos1);
        
        mediaType = MediaType.getInstance(m.substring(pos1 + 1, pos2));
        
        if (mediaType == MediaType.AUDIO) {
            fmtParser = new AudioFormatParser();
        } else if (mediaType == MediaType.VIDEO) {
            fmtParser = new VideoFormatParser();
        } else {
            throw new IllegalArgumentException("Unknown media type " + mediaType);
        }
        
        pos1 = m.indexOf(32, pos2 + 1);
        port = Integer.parseInt(m.substring(pos2 + 1, pos1));
        
        pos2 = m.indexOf(32, pos1 + 1);
        profile = m.substring(pos1 + 1, pos2);
        
        pos1 = pos2;
        pos2 = m.indexOf(32, pos1 + 1);
        
        Format format = null;
        while (pos2 > 0) {
            int f = Integer.parseInt(m.substring(pos1 + 1, pos2));
            format = fmtParser.getFormat(f);
            
            if(format!=null){
            	fmt[count] = f;
            	formats[count++] = format;
            }
            
            pos1 = pos2;
            pos2 = m.indexOf(32, pos1 + 1);
        }
        
        
        format = fmtParser.getFormat(fmt[count]);
        
        if(format != null){
        	fmt[count] = Integer.parseInt(m.substring(pos1 + 1, m.length()));
        	formats[count] = format;
        	
            count++;
        }
        

    }
    
    public MediaDescriptor(MediaType mediaType, int port) {
        this.mediaType = mediaType;
        this.port = port;
        
        fmtParser = new AudioFormatParser();
        if (mediaType == MediaType.AUDIO) {
            fmtParser = new AudioFormatParser();
        } else if (mediaType == MediaType.VIDEO) {
            fmtParser = new VideoFormatParser();
        } else {
            throw new IllegalArgumentException("Unknown media type " + mediaType);
        }
    }

    
    public MediaType getMediaType() {
        return mediaType;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getProfle() {
        return profile;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    
    public int getFormatCount() {
        return count;
    }
        
    public int getPyaloadType(int i) {
        return fmt[i];
    }
    
    public Format getFormat(int i) {
        return formats[i];
    }
    
    public void addFormat(int payload, Format format) {
        fmt[count] = payload;
        formats[count++] = format;
    }
    
    public void addAttribute(String attribute) {
        attributes[aCount++] = attribute;
    }
    
    protected void parseAtribute(String a) {
        if (a.startsWith("a=rtpmap:")) {
            if (fmtParser.parse(a, fmt, formats, count)) {
                count++;
            }
        } else if (a.equals("a=sendrecv")) {
            this.mode = "sendrecv";
        } else if (a.equals("a=sendonly")) {
            this.mode = "sendonly";
        } else if (a.equals("a=recvonly")) {
            this.mode = "recvonly";
        } else if (a.startsWith("c=")) {
        	connection = new Connection(a);
        }
    }
    
    public void write(StringBuffer buffer) {
        buffer.append("m=" + mediaType.getName() + " " + port  + " RTP/AVP");
        for (int j = 0; j < count; j++) {
            buffer.append(" " + fmt[j]);
        }        
        buffer.append("\n");
        
        if(this.connection != null ){
        	buffer.append(connection.toString());
        }
        
        for (int j = 0; j < aCount; j++) {
            buffer.append("a=" + attributes[j] + "\n");
        }
        
        for (int j = 0; j < count; j++) {
            fmtParser.write(buffer, fmt[j], formats[j]);
            buffer.append("\n");
        }
        
        if(this.mediaType== MediaType.AUDIO) {
        	buffer.append("a=control:audio\n");
        } else if(this.mediaType== MediaType.VIDEO){
        	buffer.append("a=control:video\n");
        }
        
        buffer.append("a=silenceSupp:off\n");
    }

	public Connection getConnection() {
		return connection;
	}
}
