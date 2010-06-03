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
package org.mobicents.media.server.impl.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.mobicents.media.Buffer;
import org.mobicents.media.Format;
import org.mobicents.media.Inlet;
import org.mobicents.media.MediaSink;
import org.mobicents.media.MediaSource;
import org.mobicents.media.server.impl.AbstractSink;
import org.mobicents.media.server.impl.AbstractSource;
import org.mobicents.media.server.impl.AbstractSourceSet;
import org.mobicents.media.server.spi.Connection;
import org.mobicents.media.server.spi.Endpoint;
import org.mobicents.media.server.spi.SyncSource;
import org.mobicents.media.server.spi.clock.Task;
import org.mobicents.media.server.spi.clock.TimerTask;

/**
 * A Demultiplexer is a media processing component that takes an interleaved 
 * media stream as input, extracts the individual tracks from the stream, and 
 * outputs the resulting tracks. It has one input and multiple outputs.
 * 
 * @author Oleg Kulikov
 */
public class Demultiplexer extends AbstractSourceSet implements Inlet {

    private Format[] outputFormats = new Format[0];
    private Input input = null;
    private Buffer buff;
    private long timestamp;
    
    /**
     * Creates new instance of the demultiplexer.
     * 
     * @param name
     */
    public Demultiplexer(String name) {
        super(name);
        input = new Input(name);
    }

    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.Inlet#getInput(). 
     */
    public AbstractSink getInput() {
        return input;
    }

    public void connect(MediaSource source) {
        input.connect(source);
    }

    public void disconnect(MediaSource source) {
        input.disconnect(source);
    }

    @Override
    public AbstractSource createSource(MediaSink otherParty) {
        Output output = new Output(getName() + "[output]");
        output.setSyncSource(input);
        output.setEndpoint(getEndpoint());
        output.setConnection(getConnection());
        return output;
    }
    
    @Override
    public void setConnection(Connection connection) {
        super.setConnection(connection);
        input.setConnection(connection);
        
        Collection<AbstractSource> list = getStreams();
        for (AbstractSource stream : list) {
            stream.setConnection(connection);
        }
    }

    @Override
    public void setEndpoint(Endpoint endpoint) {
        super.setEndpoint(endpoint);
        input.setEndpoint(endpoint);
        
        Collection<AbstractSource> list = getStreams();
        for (AbstractSource stream : list) {
            stream.setEndpoint(endpoint);
        }
    }
    
    /**
     * (Non Java-doc).
     * 
     * @see org.mobicents.media.MediaSource#getFormats().
     */
    public Format[] getFormats() {
        return input.getOtherPartyFormats();
    }

    /**
     * Reassemblies the list of used formats. This method is called each time
     * when connected/disconnected source
     */
    private void reassemblyFormats() {
        ArrayList list = new ArrayList();
        Collection<AbstractSource> streams = getStreams();
        for (AbstractSource stream : streams) {
            Format[] fmts = ((Output)stream).getOtherPartyFormats();
            for (Format format : fmts) {
                if (!list.contains(format)) {
                    list.add(format);
                }
            }
        }

        outputFormats = new Format[list.size()];
        list.toArray(outputFormats);
    }

    @Override
    public void start() {
        input.start();
    }
    
    @Override
    public void stop() {
        input.stop();
    }
    /**
     * Implements input stream of the Demultiplxer.
     * 
     */
    private class Input extends AbstractSink implements SyncSource {

        /**
         * Creates new instance of input stream.
         * 
         * The name of the demultiplxer.
         */
        public Input(String name) {
            super(name + "[input]");
        }

        /**
         * Reads supported formats from other party if connected.
         * 
         * @return if other party connected returns array of supported formats
         * or empty array if not connected.
         */
        protected Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.server.impl.AbstractSink#onMediaTransfer(org.mobicents.media.Buffer). 
         */
        public void onMediaTransfer(Buffer buffer) throws IOException {
            buff = buffer;
            timestamp = buffer.getTimeStamp();
            Collection<AbstractSource> streams = getStreams();
            for (AbstractSource stream : streams) {
                ((Output) stream).perform();
            }
            buffer.dispose();
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.MediaSink#getFormats().
         */
        public Format[] getFormats() {
            reassemblyFormats();
            return outputFormats;
        }

        public void sync(MediaSource mediaSource) {
        }

        public void unsync(MediaSource mediaSource) {
        }

        public long getTimestamp() {
            return timestamp;
        }

        public TimerTask sync(Task task) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Implements output stream.
     */
    private class Output extends AbstractSource {

        public Output(String parent) {
            super(parent);
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        /**
         * Gets list of formats supported by other party.
         * 
         * @return array of formats or empty array if not connected yet.
         */
        public Format[] getOtherPartyFormats() {
            return otherParty != null ? otherParty.getFormats() : new Format[0];
        }

        /**
         * (Non Java-doc).
         * 
         * @see org.mobicents.media.MediaSource#getFormats() 
         */
        public Format[] getFormats() {
            return input.getOtherPartyFormats();
        }

        @Override
        public void evolve(Buffer buffer, long timestamp) {
            buffer.copy(buff);
        }
    }

    @Override
    public void evolve(Buffer buffer, long timestamp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void destroySource(AbstractSource source) {
    }

}
