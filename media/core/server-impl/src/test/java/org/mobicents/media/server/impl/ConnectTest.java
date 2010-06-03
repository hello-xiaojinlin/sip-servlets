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
package org.mobicents.media.server.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;

/**
 *
 * @author kulikov
 */
public class ConnectTest {

    
    private TestSource src = new TestSource("source");
    private TestSink sink = new TestSink("sink");
    private TestSink2 sink2 = new TestSink2("sink2");
    
    
    public ConnectTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Simple case: source.connect(sink).
     */
    @Test
    public void testNegotiation() {
        src.connect(sink);
        
        boolean res = src.getFormat() != null && src.getFormat().matches(AVProfile.PCMA);
        assertTrue("Format mismatch",res);
        
        res = sink.getFormat() != null && sink.getFormat().matches(AVProfile.PCMA);
        assertTrue("Format mismatch",res);
    }

    /**
     * Simple case: sink.connect(source).
     */
    @Test
    public void testNegotiation2() {
        sink.connect(src);
        
        boolean res = src.getFormat() != null && src.getFormat().matches(AVProfile.PCMA);
        assertTrue("Format mismatch",res);
        
        res = sink.getFormat() != null && sink.getFormat().matches(AVProfile.PCMA);
        assertTrue("Format mismatch",res);
    }
    

    /**
     * Simple case: source.connect(sink).
     */
    @Test
    public void testDisconnect() {
        src.connect(sink);
        src.disconnect(sink);
        
        assertTrue("Format still assigned",src.getFormat() == null);
        assertTrue("Format still assigned",sink.getFormat() == null);
    }

    /**
     * Simple case: sink.connect(source).
     */
    @Test
    public void testDisconnect2() {
        sink.connect(src);
        sink.disconnect(src);
        
        assertTrue("Format still assigned",src.getFormat() == null);
        assertTrue("Format still assigned",sink.getFormat() == null);
    }

    /**
     * Simple case: sink.connect(source).
     */
    @Test
    public void testDisconnect3() {
        //join/drop from different ends
        sink.connect(src);
        src.disconnect(sink);
        
        assertTrue("Format still assigned",src.getFormat() == null);
        assertTrue("Format still assigned",sink.getFormat() == null);
    }

    /**
     * Simple case: sink.connect(source).
     */
    @Test
    public void testDisconnect4() {
        //join/drop from different ends
        src.connect(sink);
        sink.disconnect(src);
        
        assertTrue("Format still assigned",src.getFormat() == null);
        assertTrue("Format still assigned",sink.getFormat() == null);
    }
    
    /**
     * Simple case: sink.connect(source).
     * Format missmatch expected
     */
    @Test
    public void failureTest() {
        try {
            sink2.connect(src);
            fail("Format missmatch expected");
        } catch (Exception e) {
            
        }
    }

    /**
     * Simple case: source.connect(sink).
     * Format missmatch expected
     */
    @Test
    public void failureTest2() {
        try {
            src.connect(sink2);
            fail("Format missmatch expected");
        } catch (Exception e) {
            
        }
    }
    
    private class TestSource extends AbstractSource {

        public TestSource(String name) {
            super(name);
        }

        public Format[] getFormats() {
            return new Format[]{AVProfile.PCMA, AVProfile.PCMU, AVProfile.L16_MONO};
        }

        public String getOtherPartyName() {
            return this.otherParty.getName();
        }

        @Override
        public void evolve(Buffer buffer, long timestamp) {
        }
    }

    private class TestSink extends AbstractSink {

        public TestSink(String name) {
            super(name);
        }

        public Format[] getFormats() {
            return new Format[]{AVProfile.PCMA};
        }

        public boolean isAcceptable(Format format) {
            return true;
        }

        public String getOtherPartyName() {
            return this.otherParty.getName();
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
        }
    }

    private class TestSink2 extends AbstractSink {

        public TestSink2(String name) {
            super(name);
        }

        public Format[] getFormats() {
            return new Format[]{AVProfile.G729};
        }

        public boolean isAcceptable(Format format) {
            return true;
        }

        public String getOtherPartyName() {
            return this.otherParty.getName();
        }

        @Override
        public void onMediaTransfer(Buffer buffer) {
        }
    }
    
}