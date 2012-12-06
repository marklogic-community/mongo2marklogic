/**
 * $Id: $
 * $DateTime: $
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
