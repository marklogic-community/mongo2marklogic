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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.marklogic.mongo.BSON.AttributeNames;
import com.marklogic.mongo.BSON.ElementNamesBS;
import com.marklogic.xcc.Content;
import com.marklogic.xcc.ContentCreateOptions;
import com.marklogic.xcc.ContentFactory;
import com.marklogic.xcc.ContentSource;
import com.marklogic.xcc.ContentSourceFactory;
import com.marklogic.xcc.SecurityOptions;
import com.marklogic.xcc.Session;
import com.marklogic.xcc.exceptions.RequestException;
import com.marklogic.xcc.exceptions.XccConfigException;

public class FileImporter extends Importer {
    
    private String outdir;
    
	void run(String[] args) throws XccConfigException, Exception {

		
		

		String input = getArg(args, "input", null);
		outdir = getArg( args , "directory" , ".");
		
		InputStream is = new BufferedInputStream(input == null ? System.in : new FileInputStream(input));

		BSONInputStream bos = new BSONInputStream(is);

        long tm_start = System.currentTimeMillis();
		
		int files = 0;
		while (is.available() > 0) {
			
			XMLWriter bxw = getWriter(args);
			bxw.writeDocumentRoot(bos);
			
			put(  bxw.close() );

			files++;
		}
		
		long tm_stop = System.currentTimeMillis();
        long ms = tm_stop - tm_start ; 
        
        Importer.completed(files);
        
		
		
	}
    private  void put( byte[] bytes) throws IOException 
	{
		File f = new File( getUri() );
		FileOutputStream fos = new FileOutputStream( f );
		fos.write(bytes);
		fos.close();
		
		
		
	}
	
	private String getUri() {
		return outdir + "/" + getRandom() + ".xml";
	}
	
	}
