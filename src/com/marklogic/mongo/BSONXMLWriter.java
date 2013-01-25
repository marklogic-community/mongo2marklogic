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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import com.marklogic.mongo.BSON.AttributeNames;
import com.marklogic.mongo.BSON.ElementNamesBS;

class BSONXMLWriter  extends XMLWriter {
	
	/*
	 * datetime is ms since 1970
	 */


	BSONXMLWriter() throws XMLStreamException
    {
        super();
    }






    @Override
    String getNSURI()
    {
	    return BSON.kBSON_NS;
    }






    @Override
    String getNSPrefix()
    {
        // TODO Auto-generated method stub
        return BSON.kBSON_PREFIX ;
    }




    @Override
	protected void writeScriptScope(String name, AttributeNames type, BSONInputStream bis) throws XMLStreamException, IOException, BSONException {
		startElement(name,type);
		
		int len = bis.readInt32();
		BSONInputStream docStream = bis.openDataStream(len - 4 );
	     
		String js = docStream.readString();
		startElementBS(ElementNamesBS.script,type);
		value(js);
		endElement();
		writeDocumentBS( ElementNamesBS.scope ,AttributeNames.document,  bis );
		endElement();
		
		
		
	}
    
    protected void startElementBS(ElementNamesBS name, AttributeNames type) throws XMLStreamException
    {
        startElementBS( BSON.getEnumName(name), type );
    }
    
    private void writeDocumentBS(ElementNamesBS name, AttributeNames type, BSONInputStream data)
            throws XMLStreamException, IOException, BSONException
    {
        writeDocumentBS(BSON.getEnumName(name), type, data);
    }

	@Override
	protected void writeRegex(String name, AttributeNames type, String s1, String s2) throws XMLStreamException {

		startElement(name,type);
		attribute(AttributeNames.pattern , s1 );
		attribute(AttributeNames.options , s2 );
		endElement();

	}



	@Override
	protected void writeBinary(String  name, AttributeNames type, BSONInputStream docStream) throws XMLStreamException, IOException {
		startElement(name,type);
		
		int len = docStream.readInt32();
		int subtype = docStream.readByte() ;
		byte[] data = new byte[len];
		docStream.read( data );
		
		startElement( name,type );
		attribute(AttributeNames.subtype, String.valueOf(subtype));
		value( BSON.toHex( data ));
		endElement();

	}





    protected void attribute(AttributeNames attr, String string) throws XMLStreamException
    {
        writer.writeAttribute(BSON.getEnumName(attr), string);
    }






    void attribute(AttributeNames attr, long value) throws XMLStreamException
    {
        writer.writeAttribute(BSON.getEnumName(attr), String.valueOf(value));
    }




    protected void type(AttributeNames type) throws XMLStreamException
    {
        attribute(AttributeNames.type, BSON.getEnumName(type));
    
    }





    @Override
    public void writeDocumentRoot(BSONInputStream bos) throws XMLStreamException, IOException, BSONException
    {
        writeDocumentNS(BSON.getEnumName(ElementNamesBS.document), AttributeNames.document, bos);
        
    }
	

	
	
}