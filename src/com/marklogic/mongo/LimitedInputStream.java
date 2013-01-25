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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends FilterInputStream {

	private int mLength ;
	private int mMark = 0;
	public LimitedInputStream(InputStream in, int len) {
		super(in);
		mLength = len ;
	}
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if( mLength < 1 )
			throw new IOException("Not enough data");
		// TODO Auto-generated method stub
		int ret =  super.read();
		if( ret >= 0 )
			mLength --;
		return ret ;
	}
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if( mLength < len )
			throw new IOException("Not enough data");
		
		int ret = super.read(b, off, len);
		mLength -= ret ;
		return ret ;
	}
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#skip(long)
	 */
	@Override
	public long skip(long n) throws IOException {
		if( mLength < n )
			throw new IOException("Not enough data");

		long ret = super.skip(n);
		mLength -= ret ;
		return ret ;
	}
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#available()
	 */
	@Override
	public int available() throws IOException {
		return mLength ;
	}
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#mark(int)
	 */
	@Override
	public synchronized void mark(int readlimit) {
		mMark = mLength ;
		super.mark(readlimit);
	}
	/* (non-Javadoc)
	 * @see java.io.FilterInputStream#reset()
	 */
	@Override
	public synchronized void reset() throws IOException {
		// TODO Auto-generated method stub
		super.reset();
		mLength = mMark ;
	}
	
	

}
