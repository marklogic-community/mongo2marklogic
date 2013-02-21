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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.marklogic.xcc.exceptions.XccConfigException;

public class FileImporter extends Importer {
    
    private String outdir;

    
	void run(String[] args) throws XccConfigException, Exception {

		
		

		String input = getArg(args, "input", null);
		outdir = getArg( args , "directory" , ".");
        bUseId = XCCImporter.hasArg(args , "id");

        
       
        
		InputStream is = new BufferedInputStream(input == null ? System.in : new FileInputStream(input));

		BSONInputStream bos = new BSONInputStream(is);

		
		File f = new File( outdir );
		if( f.exists() && ! f.isDirectory() )
		    throw new Exception("Output is not a directory: " + f.getAbsolutePath() );
		
		if( ! f.exists() ){
		    System.out.println("Creating output directory: " + f.getAbsolutePath());
		    f.mkdirs();
	    }		
		
		int files = 0;
		while (is.available() > 0) {
			
			mWriter = getWriter(args);
			mWriter.writeDocumentRoot(bos);
			
			put(  mWriter.close() );

			files++;
		}
		
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
		return outdir + "/" + ( bUseId ? mWriter.getId() : getRandom()) + mWriter.getSuffix();
	}
	
	}
