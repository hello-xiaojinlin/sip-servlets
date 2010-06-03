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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mobicents.media.server.spi.MediaType;
import static org.junit.Assert.*;

/**
 *
 * @author kulikov
 * @author amit bhayani
 */
public class DescriptorTest {

    private SessionDescriptor desc;
    private String sdp = "v=0\n" +
            "o=- 8 2 IN IP4 192.168.1.2\n" +
            "s=CounterPath X-Lite 3.0\n" +
            "c=IN IP4 192.168.1.2\n" +
            "t=0 0\n" +
            "m=audio 39958 RTP/AVP 8 101\n" +
            "a=alt:1 1 : aZNEKdX5 FpbpFGUv 192.168.1.2 39958\n" +
            "a=fmtp:101 0-15\n" +
            "a=rtpmap:101 telephone-event/8000\n" +
            "a=sendrecv";
    
    private String linPhoneSDP = "v=0\n"+
								    "o=blocked-sender 123456 654321 IN IP4 192.168.0.11\n"+
								    "s=A conversation\n"+
								    "c=IN IP4 192.168.0.11\n"+
								    "t=0 0\n"+
								    "m=audio 7078 RTP/AVP 111 110 0 8 101\n"+
								    "c=IN IP4 192.168.0.11\n"+
								    "a=rtpmap:111 speex/16000/1\n"+
								    "a=rtpmap:110 speex/8000/1\n"+
								    "a=rtpmap:0 PCMU/8000/1\n"+
								    "a=rtpmap:8 PCMA/8000/1\n"+
								    "a=rtpmap:101 telephone-event/8000/1\n"+
								    "a=fmtp:101 0-11\n"+
								    "a=sendrecv\n"+
								    "m=video 9078 RTP/AVP 99 34 97 98 100\n"+
								    "c=IN IP4 192.168.0.11\n"+
								    "a=rtpmap:99 MP4V-ES/90000\n"+
								    "a=fmtp:99 profile-level-id=3\n"+
								    "a=rtpmap:34 H263/90000\n"+
								    "a=rtpmap:97 theora/90000\n"+
								    "a=rtpmap:98 H263-1998/90000\n"+
								    "a=fmtp:98 CIF=1;QCIF=1\n"+
								    "a=rtpmap:100 x-snow/90000\n";


    public DescriptorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
 
    @Before
    public void setUp() {
        desc = new SessionDescriptor(linPhoneSDP);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getVersion method, of class Descriptor.
     */
    @Test
    public void testGetVersion() {
        String version = desc.getVersion();
        assertEquals("0", version);
    }

    @Test
    public void testOrigin() {
        assertEquals("blocked-sender", desc.getOrigin().getName());
        assertEquals("123456", desc.getOrigin().getSessionID());
        assertEquals("654321", desc.getOrigin().getSessionVersion());
        assertEquals("IN", desc.getOrigin().getNetworkType());
        assertEquals("IP4", desc.getOrigin().getAddressType());
        assertEquals("192.168.0.11", desc.getOrigin().getAddress());
    }

    @Test
    public void testSession() {
        assertEquals("A conversation", desc.getSession());
    }

    @Test
    public void testConnection() {
        assertEquals("IN", desc.getConnection().getNetworkType());
        assertEquals("IP4", desc.getConnection().getAddressType());
        assertEquals("192.168.0.11", desc.getConnection().getAddress());
    }
    
    @Test
    public void testMediaConnection() {
    	
    	MediaDescriptor audMedDesc = desc.getMediaDescriptor(0);
    	
        assertEquals("IN", audMedDesc.getConnection().getNetworkType());
        assertEquals("IP4", audMedDesc.getConnection().getAddressType());
        assertEquals("192.168.0.11", audMedDesc.getConnection().getAddress());
        
        MediaDescriptor videoMedDesc = desc.getMediaDescriptor(1);      
        
        assertEquals("IN", videoMedDesc.getConnection().getNetworkType());
        assertEquals("IP4", videoMedDesc.getConnection().getAddressType());
        assertEquals("192.168.0.11", videoMedDesc.getConnection().getAddress());        
    }    

    @Test
    public void testMediaDescriptor() {
        assertEquals(2, desc.getMediaTypeCount());
        MediaDescriptor audMedDesc = desc.getMediaDescriptor(0);
        
        assertEquals("audio", audMedDesc.getMediaType().getName());
        assertEquals(7078, audMedDesc.getPort());
        assertEquals("sendrecv", audMedDesc.getMode());
        assertEquals(5, audMedDesc.getFormatCount());
        
        assertEquals(0, audMedDesc.getPyaloadType(0));
        assertEquals(8, audMedDesc.getPyaloadType(1));
        assertEquals(101, audMedDesc.getPyaloadType(2));
        assertEquals(111, audMedDesc.getPyaloadType(3));
        assertEquals(110, audMedDesc.getPyaloadType(4));

        assertEquals(AVProfile.PCMU, audMedDesc.getFormat(0));
        assertEquals(AVProfile.PCMA, audMedDesc.getFormat(1));
        assertEquals(AVProfile.DTMF, audMedDesc.getFormat(2));
        assertEquals(AVProfile.SPEEX, audMedDesc.getFormat(4));
        
        assertNotNull(audMedDesc.getFormat(3));
        
        
        MediaDescriptor videoMedDesc = desc.getMediaDescriptor(1);
        
        assertEquals("video", videoMedDesc.getMediaType().getName());
        assertEquals(9078, videoMedDesc.getPort());
        assertEquals(5, videoMedDesc.getFormatCount());

        assertEquals(34, videoMedDesc.getPyaloadType(0));
        assertEquals(99, videoMedDesc.getPyaloadType(1));
        assertEquals(97, videoMedDesc.getPyaloadType(2));
        assertEquals(98, videoMedDesc.getPyaloadType(3));
        assertEquals(100, videoMedDesc.getPyaloadType(4));
        
        assertEquals(AVProfile.H263, videoMedDesc.getFormat(0));
        
        assertNotNull(videoMedDesc.getFormat(1));
        assertNotNull(videoMedDesc.getFormat(2));
        assertNotNull(videoMedDesc.getFormat(3));
        assertNotNull(videoMedDesc.getFormat(4));
    }

    @Test
    public void testToString() {
        String s = "v=0\n" +
                "o=- 8 2 IN IP4 192.168.1.2\n" +
                "s=CounterPath X-Lite 3.0\n" +
                "c=IN IP4 192.168.1.2\n" +
                "t=0 0\n" +
                "m=audio 39958 RTP/AVP 0 101\n" +
                "a=alt:1 1 : aZNEKdX5 FpbpFGUv 192.168.1.2 39958\n" +
                "a=rtpmap:0 pcma/8000\n" +
                "a=rtpmap:101 telephone-event/8000\n" +
                "a=fmtp:101 0-15\n" + 
                "a=control:audio\n" +
                "a=silenceSupp:off";
        SessionDescriptor sd = new SessionDescriptor();
        sd.setSession("CounterPath X-Lite 3.0");
        sd.createOrigin("-", "8", "2", "IN", "IP4", "192.168.1.2");
        sd.createConnection("IN", "IP4", "192.168.1.2");
        MediaDescriptor md = sd.addMedia(MediaType.AUDIO, 39958);
        md.addAttribute("alt:1 1 : aZNEKdX5 FpbpFGUv 192.168.1.2 39958");
        md.addFormat(0, AVProfile.PCMA);
        md.addFormat(101, AVProfile.DTMF);
        System.out.println(sd.toString());
        //assertEquals(s, sd.toString());
    }
}