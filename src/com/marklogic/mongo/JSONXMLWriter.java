package com.marklogic.mongo;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.marklogic.mongo.BSON.AttributeNames;
import com.marklogic.mongo.BSON.ElementNamesBS;


public class JSONXMLWriter extends XMLWriter
{
    

    JSONXMLWriter() throws XMLStreamException
    {
        super();
    }

    @Override
    String getNSURI()
    {
        return "http://marklogic.com/xdmp/json/basic" ;
    }

    @Override
    String getNSPrefix()
    {
        // TODO Auto-generated method stub
        return "";
    }


    @Override
    protected void writeBinary(String name, AttributeNames type, BSONInputStream docStream) throws XMLStreamException,
            IOException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void writeRegex(String name, AttributeNames type, String s1, String s2) throws XMLStreamException
    {
        writeString( name , type , s1 + s2 );
        
    }

    @Override
    protected void writeScriptScope(String name, AttributeNames type, BSONInputStream bis) throws XMLStreamException,
            IOException, BSONException
    {
        throw new BSONException("Unimplemented method writeScriptScope");
        
    }
    

    @Override
    public void writeDocumentRoot(BSONInputStream bos) throws XMLStreamException, IOException, BSONException
    {
        writeDocumentNS("object", AttributeNames.document, bos);
        
    }
    
    
    
    static enum JSONType {
        number ,
        string ,      
        _null ,   
        _boolean,
        object,
       array  ;
        public String toString() {
            String n = name();
            if( n.startsWith("_"))
                return n.substring(1);
            else
                return n;
        }
    }
    
    private JSONType getJsonType(AttributeNames type )
    {
        switch( type ){
        case flags : return JSONType.number  ; 
        case db :    return JSONType.string  ;  
        case document:      return JSONType.object  ;   
        case collection:      return JSONType.string  ;   
        case cursor_id:      return JSONType.number  ;   
        case number_to_return:      return JSONType.number;   
        case opcode:      return JSONType.number  ;   
        case request_id:      return JSONType.number  ;   
        case response_to:      return JSONType.number  ;   
        case client_ip:      return JSONType.string  ;   
        case client_port:      return JSONType.number  ;   
        case _double:      return JSONType.number  ;   
        case string:      return JSONType.string  ;   
        case objectId  :      return JSONType.string  ;  
        case _boolean:      return JSONType._boolean  ;   
        case dateTime:      return JSONType.string  ;   
        case binary:      return JSONType.string  ;   
        case array:      return JSONType.array  ;   
        case _null:      return JSONType._null  ;   
        case javascript:      return JSONType.string  ;   
        case javascript_s:      return JSONType.string  ;   
        case int32:      return JSONType.number  ;   
        case timestamp:      return JSONType.string  ;   
        case int64:      return JSONType.number  ;   
        case min_key:      return JSONType.string  ;   
        case max_key:      return JSONType.string  ;   
        case number_to_skip:      return JSONType.number  ;   
        case subtype:      return JSONType.string  ;   
        case pattern:      return JSONType.string  ;   
        case options:      return JSONType.string  ;   
        case type:      return JSONType.string  ;   
        case regex :      return JSONType.string  ;  
        }
        return JSONType.string;
        
    }

    @Override
    protected void type(AttributeNames type) throws XMLStreamException
    {
        JSONType t = getJsonType( type );
        String stype = t.toString();
        attribute( "type" , stype );
        
    }
}
