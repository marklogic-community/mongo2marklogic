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
import java.util.Date;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.marklogic.mongo.BSON.AttributeNames;
import com.marklogic.mongo.BSON.ElementNamesBS;

public abstract class XMLWriter
{

    protected static XMLOutputFactory mOutputFactory = XMLOutputFactory.newInstance();
    protected ByteArrayOutputStream bos;
    protected XMLStreamWriter writer;
    protected boolean bHasElement = false;
    
    

    XMLWriter() throws XMLStreamException
    {
        bos = new ByteArrayOutputStream();
        writer = mOutputFactory.createXMLStreamWriter(bos); 
        writer.writeStartDocument();
        writer.setPrefix(getNSPrefix(),getNSURI());
        
        
    }
    
    
    abstract String getNSURI() ;
    abstract String getNSPrefix();
    abstract String getSuffix();
    


    protected abstract void writeBinary(String name, AttributeNames type, BSONInputStream docStream)
            throws XMLStreamException, IOException;


    protected abstract void writeRegex(String name, AttributeNames type, String s1, String s2)
            throws XMLStreamException;

    protected abstract void writeScriptScope(String name, AttributeNames type, BSONInputStream bis)
            throws XMLStreamException, IOException, BSONException;



    void attribute(String name, String value) throws XMLStreamException
    {
        writer.writeAttribute(name, value);
    }

    void attribute(String name, long value) throws XMLStreamException
    {
        writer.writeAttribute(name, String.valueOf(value));
    }

    public void writeDocument(String name, AttributeNames type, BSONInputStream data) throws XMLStreamException,
            IOException, BSONException
    {
        startElement(name, type);
        int size = data.readInt32();

        BSONInputStream docStream = data.openDataStream(size - 4);
        while (docStream.available() > 0)
        {
            writeElement(docStream);

        }
        endElement();
    }

    public void writeDocumentNS(String name, AttributeNames type, BSONInputStream data)
            throws XMLStreamException, IOException, BSONException
    {
        startElementBS(name, type);
        writer.writeNamespace(getNSPrefix(), getNSURI());

        int size = data.readInt32();

        BSONInputStream docStream = data.openDataStream(size - 4);
        while (docStream.available() > 0)
        {
            writeElement(docStream);

        }
        endElement();
    }

    

    
    public void writeDocumentBS(String name, AttributeNames type, BSONInputStream data)
            throws XMLStreamException, IOException, BSONException
    {
        startElementBS(name, type);
        int size = data.readInt32();

        BSONInputStream docStream = data.openDataStream(size - 4);
        while (docStream.available() > 0)
        {
            writeElement(docStream);

        }
        endElement();
    }

    /*
     * element ::=
     * "\x01" e_name double Floating point 
     * "\x02" e_name string UTF-8 string | 
     * "\x03" e_name document Embedded document | 
     * "\x04" e_name document Array | 
     * "\x05" e_name binary Binary data | 
     * "\x06" e_name Undefined  Deprecated | 
     * "\x07" e_name (byte*12) ObjectId | 
     * "\x08" e_name |
     * "\x00" Boolean "false" | 
     * "\x08" e_name "\x01" Boolean "true" | 
     * "\x09" e_name int64 UTC datetime | 
     * "\x0A" e_name Null value | 
     * "\x0B" e_name cstring cstring Regular expression | 
     * "\x0C" e_name string (byte*12) DBPointer  Deprecated | 
     * "\x0D" e_name string JavaScript code | 
     * "\x0E" e_name string Symbol  Deprecated | 
     * "\x0F" e_name code_w_s JavaScript code w/ scope | 
     * "\x10" e_name int32 32-bit Integer | 
     * "\x11" e_name int64 Timestamp | 
     * "\x12" e_name int64 64-bit integer | 
     * "\xFF" e_name Min key |
     * "\x7F" e_name Max key
     */

    protected void writeElement(BSONInputStream docStream) throws IOException, XMLStreamException, BSONException
    {
        int code = docStream.readByte();
        if (code <= 0)
            return;

        String e_name = docStream.readCString();
        AttributeNames type = BSON.getElementType(code);

        switch (code) {

        // "\x01" e_name double Floating point
        case 0x01:
            writeDouble(e_name, type, docStream.readDouble());
            break;

        // "\x02" e_name string UTF-8 string
        case 0x02:
            writeString(e_name, type, docStream.readString());
            break;

        // "\x03" e_name document Embedded document
        case 0x03:
            writeDocument(e_name, type, docStream);
            break;

        // "\x04" e_name document Array
        case 0x04:
            writeDocument(e_name, type, docStream);
            break;

        // "\x05" e_name binary Binary data
        case 0x05:
            writeBinary(e_name, type, docStream);
            break;

        // "\x07" e_name (byte*12) ObjectId
        case 0x07:
            writeObjectID(e_name, type, docStream);
            break;

        // "\x08" e_name "\x00" Boolean "false"
        // "\x08" e_name "\x01" Boolean "true"
        case 0x08:
            writeBoolean(e_name, type, docStream.readByte() != 0);
            break;

        // "\x09" e_name int64 UTC datetime
        case 0x09:
            writeDatetime(e_name, type, docStream.readInt64());
            break;

        // "\x0A" e_name Null value
        case 0x0A:
            writeNull(e_name, type);
            break;

        // "\x0B" e_name cstring cstring Regular expression
        case 0x0B:
            String s1 = docStream.readCString();
            String s2 = docStream.readCString();
            writeRegex(e_name, type, s1, s2);
            break;
        // "\x0D" e_name string JavaScript code
        case 0x0D:
            writeString(e_name, type, docStream.readString());
            break;

        // "\x0F" e_name code_w_s JavaScript code w/ scope
        case 0x0F:
            writeScriptScope(e_name, type, docStream);
            break;

        // "\x10" e_name int32 32-bit Integer
        case 0x10:
            writeLong(e_name, type, docStream.readInt32());
            break;

        // "\x11" e_name int64 Timestamp
        case 0x11:
            writeDatetime(e_name, type, docStream.readInt64());
            break;

        // "\x12" e_name int64 64-bit integer
        case 0x12:
            writeLong(e_name, type, docStream.readInt64());
            break;

        // "\xFF" e_name Min key
        // "\x7F" e_name Max key

        case 0xFF:
        case 0x7F:
            writeKey(e_name, type);
            break;

        // "\x0E" e_name string Symbol  Deprecated
        // "\x06" e_name Undefined  Deprecated
        case 0xC:
        case 0xE:
        case 0x6:
        default:
            throw new IOException("Invalid element code: " + code);

        }

    }



    
    

    protected void value(String string) throws XMLStreamException
    {
        
        try {
            writer.writeCharacters(string);

        } catch( Exception e ) 
        {
           System.err.println("Exception writing invalid value: " + string );   
        }
    }

    protected void endElement() throws XMLStreamException
    {
        writer.writeEndElement();

    }

    protected void value(double d) throws XMLStreamException
    {
        writer.writeCharacters(String.valueOf(d));

    }

    protected void value(int l) throws XMLStreamException
    {
        writer.writeCharacters(String.valueOf(l));

    }

    protected void value(long l) throws XMLStreamException
    {
        writer.writeCharacters(String.valueOf(l));

    }

    public byte[] close() throws XMLStreamException
    {

        if (!bHasElement)
            return null;

        writer.writeEndDocument();
        return bos.toByteArray();

    }
    protected void startElement(String name) throws XMLStreamException
    {
        writer.writeStartElement(encodeForNCName(name));
    }

    protected abstract void type(AttributeNames type) throws XMLStreamException ;
    
    protected void startElement(String name, AttributeNames type) throws XMLStreamException
    {
        bHasElement = true;
        startElement(name);
        type(type);
    
    }
   
    
    protected void writeDouble(String name, AttributeNames type, double d) throws XMLStreamException
    {
    	startElement(name,type );
    	value( d);
    	endElement();
    	
    }
    protected void writeBoolean(String name, AttributeNames type, boolean b) throws XMLStreamException
    {
    	startElement(name,type);
    	value( b ? 1 : 0 );
    	endElement();
    	
    }
    protected void writeLong(String name, AttributeNames type, long value) throws XMLStreamException
    {
    
    	startElement(name,type);
    	value(value);
    	endElement();
    	
    }
    
    // Simplified compatible versions of xdmp:encode-for-NCName and xdmp:decode-from-NCName
    String encodeForNCName( String name )
    {
    	StringBuffer sb = new StringBuffer( name.length() * 2 );
    	char[] chars = name.toCharArray();
    	
    	boolean bFirst = true ;
    	boolean escaped = false ;
    	for( char ch : chars ){
    		if( ch == '_') {
    			sb.append("__");
    			escaped = true ;
    		} 
    		else 
    		if( ch  == ':' || 
    			(bFirst? !BSON.isInitialNameChar(ch) : 
    			!BSON.isNameChar(ch))){
    			  sb.append('_');
    		      BSON.toHexChar( ch , sb );
    			  sb.append('_');
    		      escaped = true;
    
    		}
    	    else
    		     sb.append(ch);
    	}
    	if( sb.length() == 0 )
    		  sb.append('_');
    	else
    	if( ! escaped ) 
    		return name; 
    
    	return sb.toString();
    	
    }


    protected void writeDatetime(String name, AttributeNames type, long tm) throws XMLStreamException
    {
    	startElement(name,type);
    	
    	Date d = new Date(tm);
    	value( BSON.formatXSDateTime(d));
    	endElement();
    	
    	
    	
    }


    protected void writeNull(String name, AttributeNames type) throws XMLStreamException
    {
    	startElement(name,type);
    	endElement();
    	
    }


    protected void writeKey(String name, AttributeNames type) throws XMLStreamException
    {
    	startElement(name,type);
    	endElement();
    	
    }


    protected void writeObjectID(String name, AttributeNames type, BSONInputStream docStream) throws XMLStreamException, IOException
    {
    	startElement(name,type);
    	byte[] data = new byte[12];
    	docStream.read(data);
    	value( BSON.toHex( data ));
    	endElement();
    	
    }


    protected void writeString(String name, AttributeNames type, String string) throws XMLStreamException
    {
        startElement(name,type);
        value(string);
    	endElement();
    
    }


    protected void startElementBS(String name, AttributeNames type) throws XMLStreamException
    {
        bHasElement = true;
    
        writer.writeStartElement(getNSURI(), name);
        type(type);
    
    }


    public abstract void writeDocumentRoot(BSONInputStream bos) throws XMLStreamException, IOException, BSONException;

}
