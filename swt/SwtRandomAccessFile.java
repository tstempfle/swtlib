/*
 * Copyright 2015 Tobias Stempfle <tobias.stempfle@gmx.net>
 * 
 * This file is part of swtlib.
 * 
 * swtlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * swtlib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with swtlib.  If not, see <http://www.gnu.org/licenses/>.
 */

package swtlib.swt;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SwtRandomAccessFile extends RandomAccessFile {
	private final String CHARSET = "Windows-1252";

	public SwtRandomAccessFile(File file, String mode) throws IOException {
		super(file, mode);
	}

	public int readLittleEndianUnsignedShort() throws IOException {
		int ret = readUnsignedByte();
		ret += readUnsignedByte() * 0x100;
		return ret;
	}
	
	public void writeLittleEndianUnsignedShort(int num) throws IOException {
		writeByte(num % 0x100);
		writeByte(num / 0x100);
	}
	
	public String readNullTerminatedString(int size) throws IOException {
		byte[] byteArray = new byte[size];
		int numBytesRead = read(byteArray);
		if(numBytesRead <= 0) {
			throw new EOFException();
		}
		for(int byteIndex = 0; byteIndex < numBytesRead; byteIndex++) {
			if(byteArray[byteIndex] == 0) {
				return new String(byteArray, 0, byteIndex, CHARSET);
			}
		}
		return new String(byteArray, 0, numBytesRead, CHARSET);
	}
	
	public void writeNullTerminatedString(String str) throws IOException {
		writeBytes(str);
		writeByte(0x00);
	}
	
}
