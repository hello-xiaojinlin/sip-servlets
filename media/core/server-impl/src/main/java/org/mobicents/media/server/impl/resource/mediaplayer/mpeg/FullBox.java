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

package org.mobicents.media.server.impl.resource.mediaplayer.mpeg;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author kulikov
 */
public class FullBox extends Box {

    /** is an integer that specifies the version of this format of the box. */
    private int version;
    /** is a map of flags */
    private int flags;
    
    public FullBox(long size, String type) {
        super(size, type);
    }

    /**
     * Gets the version of the format of the box.
     * 
     * @return the integer format identifier.
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Gets the map of flags.
     * 
     * @return the indeteger where loweresrt 24 bits are map of flags.
     */
    public int getFlags() {
        return flags;
    }
    
    protected long read64(DataInputStream fin) throws IOException {
        return (fin.readInt() << 32) | fin.readInt();
    }
    
    @Override
    protected int load(DataInputStream fin) throws IOException {
        this.version = fin.readByte();
        this.flags = this.read24(fin);
        return 4;
    }
    
}
