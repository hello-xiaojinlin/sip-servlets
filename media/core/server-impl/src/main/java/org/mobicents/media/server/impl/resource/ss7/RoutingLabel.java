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

package org.mobicents.media.server.impl.resource.ss7;

/**
 *
 * @author kulikov
 */
public class RoutingLabel {
    private int opc;
    private int dpc;
    private int sls;
    
    private byte[] bin =new byte[4];
    
    public RoutingLabel(int opc, int dpc, int sls) {
        this.opc = opc;
        this.dpc = dpc;
        this.sls = sls;
        
        bin[0] = (byte) dpc;        
        bin[1] = (byte)(((dpc >> 8) & 0x3f) |((opc & 0x03) << 6));
        bin[2] = (byte)(opc >> 2);
        bin[3] = (byte)(((opc >> 10) & 0x0f)| (sls << 4));
    }
    
    public RoutingLabel(byte[] sif) {
        dpc = (sif[0] & 0xff | ((sif[1] & 0x3f) << 8));    
        opc = ((sif[1] & 0x03) >> 6) | ((sif[2] & 0xff) << 2) | ((sif[3] & 0x0f) << 10);
        sls = (sif[3] & 0xf0) >>> 4;
    }
    
    public int getOPC() {
        return opc;
    }
    
    public int getDPC() {
        return dpc;
    }
    
    public int getSLS() {
        return sls;
    }
    
    public byte[] toByteArray() {
        return bin;
    }
}
