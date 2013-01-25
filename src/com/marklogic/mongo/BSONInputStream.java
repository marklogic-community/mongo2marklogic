/**
 * Copyright 2003-2013 MarkLogic Corporation
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 */

package com.marklogic.mongo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BSONInputStream {
	InputStream mInput;
	
	BSONInputStream( InputStream is )
	{
		mInput = is ;
		
	}
	
	/*
	 * 
byte	1 byte (8-bits)
int32	4 bytes (32-bit signed integer)
int64	8 bytes (64-bit signed integer)
double	8 bytes (64-bit IEEE 754 floating point)


	 */
	int readByte() throws IOException
	{
		return mInput.read();
	}
	
	int  readInt32() throws IOException {
		byte[] buf = new byte[4];
		
        if( mInput.read( buf ) != 4 )
        	throw new IOException("Short read");
        	
		
		return (buf[0]&0xFF) | ( (buf[1]&0xFF)  <<8)| ( (buf[2]&0xFF)  << 16 ) | ( (buf[3]&0xFF)   << 24) ;
		
	}
	
	long  readInt64() throws IOException {
		byte[] buf = new byte[8];
        if( mInput.read( buf ) != 8 )
        	throw new IOException("Short read");
        	
		
		return  ((long)(buf[0]&0xFF)) | ( ((long)(buf[1]&0xFF)  <<8)) | (( (long)(buf[2]&0xFF)  << 16 )) | ( ((long)(buf[3]&0xFF))   << 24) |
				( ((long)(buf[4]&0xFF) << 32)) | ( ((long)(buf[5]&0xFF)  <<40))| ( ((long)(buf[6]&0xFF)  << 48 )) | (( (long)(buf[7]&0xFF)   << 56));
		
	}
	
	double readDouble() throws IOException
	{
		long bits = readInt64();
		return Double.longBitsToDouble(bits);
		
	}
	
	String readString() throws IOException
	{
		int len = readInt32();
		if( len <= 0)
			return null ;
		
		byte[] buf = new byte[ len ];
		if( mInput.read(buf) != len )
			throw new IOException("Short read");
		
		return new String( buf , 0 , len-1 , "UTF8"); // no null
		
		
		
	}
	
	String readCString() throws IOException 
	{
		
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          int b;
          while(( b = mInput.read()) > 0 )
        	  bos.write(b);
          
          return bos.toString("UTF8");
          
	}
	
	
	MessageHeader readMessageHeader() throws IOException
	{
		MessageHeader header = new MessageHeader();
		try {
			header.messageLength = readInt32();
		} catch (IOException e) {
			return null ;
			
		}
		
		header.requestID = readInt32();
		header.responseTo = readInt32();
		header.opCode = readInt32();
		return header;
		
		
	}
	
	BSONInputStream openDataStream(int len) throws IOException
	{
		if( len <= 0 )
			return null ;
		
		
		return new BSONInputStream( new LimitedInputStream( mInput , len  ));
		
		
	}

	public int available() throws IOException {
        return mInput.available();
	}

	public void read(byte[] data) throws IOException {
		
		if( mInput.read(data) != data.length )
			throw new IOException("Short read");
		
	}

}
