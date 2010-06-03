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
package org.mobicents.media.server.impl.resource.dtmf;

import org.mobicents.media.server.impl.BaseComponent;
import org.mobicents.media.server.impl.NotifyEventImpl;

/**
 * 
 * @author kulikov
 */
public class DtmfEvent extends NotifyEventImpl {

    public final static int DTMF_0 = 0;
    public final static int DTMF_1 = 1;
    public final static int DTMF_2 = 2;
    public final static int DTMF_3 = 3;
    public final static int DTMF_4 = 4;
    public final static int DTMF_5 = 5;
    public final static int DTMF_6 = 6;
    public final static int DTMF_7 = 7;
    public final static int DTMF_8 = 8;
    public final static int DTMF_9 = 9;
    public final static int DTMF_A = 10;
    public final static int DTMF_B = 11;
    public final static int DTMF_C = 12;
    public final static int DTMF_D = 13;
    public final static int DTMF_HASH = 14;
    public final static int DTMF_STAR = 15;
    private int volume;

    
    public DtmfEvent(BaseComponent source, int eventID, int volume) {
        super(source, eventID,"Dtmf");
        this.volume = volume;
    }
    
    public int getVolume() {
        return volume;
    }
    
    public static int getId(String evt) {
        if (evt.equals("0")) {
            return DTMF_0;
        } else if (evt.equals("1")) {
            return DTMF_1;
        } else if (evt.equals("2")) {
            return DTMF_2;
        } else if (evt.equals("3")) {
            return DTMF_3;
        } if (evt.equals("4")) {
            return DTMF_4;
        } else if (evt.equals("5")) {
            return DTMF_5;
        } if (evt.equals("6")) {
            return DTMF_6;
        } else if (evt.equals("7")) {
            return DTMF_7;
        } if (evt.equals("8")) {
            return DTMF_8;
        } else if (evt.equals("8")) {
            return DTMF_8;
        } if (evt.equals("9")) {
            return DTMF_9;
        } else if (evt.equals("A")) {
            return DTMF_A;
        } if (evt.equals("B")) {
            return DTMF_B;
        } else if (evt.equals("C")) {
            return DTMF_C;
        } if (evt.equals("D")) {
            return DTMF_D;
        } else if (evt.equals("#")) {
            return DTMF_HASH;
        } else if (evt.equals("*")) {
            return DTMF_STAR;
        } else {
            return -1;
        }
    }
}
