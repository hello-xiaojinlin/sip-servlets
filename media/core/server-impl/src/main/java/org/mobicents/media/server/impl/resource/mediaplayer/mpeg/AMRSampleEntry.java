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
 * Look 3Gpp TS 26.244 section 6.5
 * 
 * @author amit bhayani
 * 
 */
public class AMRSampleEntry extends AudioSampleEntry {

	// File Type = samr
	static byte[] TYPE = new byte[] { AsciiTable.ALPHA_s, AsciiTable.ALPHA_a, AsciiTable.ALPHA_m, AsciiTable.ALPHA_r };
	static String TYPE_S = "samr";
	static {
		bytetoTypeMap.put(TYPE, TYPE_S);
	}

	private AmrSpecificBox amrSpecificBox;

	public AMRSampleEntry(long size) {
		super(size, TYPE_S);
	}

	@Override
	protected int load(DataInputStream fin) throws IOException {
		int count = super.load(fin);
		if (count < getSize()) {
			long len = readU32(fin);
			String type = readType(fin);
			if (type.equals("damr")) {
				amrSpecificBox = new AmrSpecificBox(len, type);
				count += amrSpecificBox.load(fin);
			} else {
				throw new IOException("Unknown box=" + new String(type) + "parent = AudioSampleEntry");
			}
		}
		return count;
	}

	public AmrSpecificBox getAmrSpecificBox() {
		return amrSpecificBox;
	}

}
