/**
 * $Id: $
 * $DateTime: $
 *
 */

package com.marklogic.mongo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

public class BSON {

	
	
	static HashMap<Integer,AttributeNames>   sElementCodeToType = new HashMap<Integer,AttributeNames>();
	static HashMap<AttributeNames,Integer>   sElementTypeToCode = new HashMap<AttributeNames,Integer>();
	private static final String     sXSDT_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss";
	static String kBSON_NS = "http://www.marklogic.com/schema/bson";
	static String kBSON_PREFIX = "bson";
    static QName kQNAME_REPLY = new QName(kBSON_NS, "reply" );
	
    static enum UpdateFlags {
    	Upsert,
    	MultiUpdate
    };
    
    static enum InsertFlags 
    {
    	ContinueOnError 
    };
    
    static enum QueryFlags 
    {
    	Reserved,
    	TailableCursor,
    	SlaveOk,
    	OplogReplay,
    	NoCursorTimeout,
    	AwaitData,
    	Exaust,
    	Partial
    };
    
    
    static enum DeleteFlags {
    	SingleRemove
    }
    ;
    
    
    static enum ElementNamesBS {
    	delete,
    	script, scope, selector, document, getmore, insert, cursor, kill_cursors, request, query, message, update, update_doc
       
    }
    static enum AttributeNames {
    	flags, db, document, collection, cursor_id, number_to_return, opcode, request_id, response_to, client_ip, client_port, _double, string, objectId, _boolean, dateTime, binary, array, _null, javascript, javascript_s, int32, timestamp, int64, min_key, max_key, number_to_skip, subtype, pattern, options, type, regex ,
    	
    }
    
    
	static {
         /*
		
		            "\x01" e_name double	Floating point
				 |	"\x02" e_name string	UTF-8 string
				 |	"\x03" e_name document	Embedded document
				 |	"\x04" e_name document	Array
				 |	"\x05" e_name binary	Binary data
				 |	"\x06" e_name	Undefined — Deprecated
				 |	"\x07" e_name (byte*12)	ObjectId
				 |	"\x08" e_name "\x00"	Boolean "false"
				 |	"\x08" e_name "\x01"	Boolean "true"
				 |	"\x09" e_name int64	UTC datetime
				 |	"\x0A" e_name	Null value
				 |	"\x0B" e_name cstring cstring	Regular expression
				 |	"\x0C" e_name string (byte*12)	DBPointer — Deprecated
				 |	"\x0D" e_name string	JavaScript code
				 |	"\x0E" e_name string	Symbol — Deprecated
				 |	"\x0F" e_name code_w_s	JavaScript code w/ scope
				 |	"\x10" e_name int32	32-bit Integer
				 |	"\x11" e_name int64	Timestamp
				 |	"\x12" e_name int64	64-bit integer
				 |	"\xFF" e_name	Min key
				 |	"\x7F" e_name	Max key
		
		*/
		
		putElement( 0x01 ,AttributeNames._double);
		putElement( 0x02 ,AttributeNames.string);
		putElement( 0x03 ,AttributeNames.document);
		putElement( 0x04 ,AttributeNames.array);
		putElement( 0x05 ,AttributeNames.binary);
		putElement( 0x07 ,AttributeNames.objectId);
		putElement( 0x08 ,AttributeNames._boolean);
		putElement( 0x09 ,AttributeNames.dateTime);
		putElement( 0x0A ,AttributeNames._null);
		putElement( 0x0B ,AttributeNames.regex);
		putElement( 0x0D ,AttributeNames.javascript);
		putElement( 0x0F ,AttributeNames.javascript_s);
		putElement( 0x10 ,AttributeNames.int32);
		putElement( 0x11 ,AttributeNames.timestamp);
		putElement( 0x12 ,AttributeNames.int64);
		putElement( 0xFF,AttributeNames.min_key);
		putElement( 0x7F,AttributeNames.max_key);

		
		
	}
	
	private static List<String> reserved = Arrays.asList("double" , "boolean" , "null" );
	

	private static void putElement(int code, AttributeNames type) {
		sElementCodeToType.put(code, type);
		sElementTypeToCode.put( type , code );
		
	}
	
	static int getElementCode( String stype ) throws BSONException
	{
        AttributeNames type = getAttributeType( stype );	
		Integer icode =  sElementTypeToCode.get(type);
		if( icode != null )
			return icode ;
		else
			throw new BSONException("Unknown type: " + type  );
	}
	private static AttributeNames getAttributeType(String stype) {
		if( reserved.contains(stype))
			stype = "_" + stype ;
		else
			stype = stype.replace('-', '_');
		return AttributeNames.valueOf(stype);
		
		
	}

	static AttributeNames getElementType( int code)
	{
		return sElementCodeToType.get(code);
	}

	// Format as xs:datetime

	static String formatXSDateTime(Date date) {
		// YYYY-MM-DDThh:mm:ss
		return (new SimpleDateFormat(sXSDT_FORMAT_STR)).format(date);
	}
	static Date parseXSDateTime(String date) throws ParseException {
		// YYYY-MM-DDThh:mm:ss
		return (new SimpleDateFormat(sXSDT_FORMAT_STR)).parse(date);
	}
	 
    /*
	 * Copy an input to an output stream
	 */

	public static long copyStream(InputStream is, OutputStream os) throws IOException
	{
		byte[] buf = new byte[1024];
		int len;
		long size = 0;
		while ((len = is.read(buf)) > 0)
		{
			os.write(buf,0 , len);
			size += len;
		}

		return size;

	}

	static int  fromHexChars( char[] chars , int i )
	{
		
		
		int n1 = chars[i] >= 'A' ? (10 + (chars[i] - 'A')) : ( chars[i] - '0');
		i++;
		int n2 = chars[i] >= 'A' ? (10 + (chars[i] - 'A')) : ( chars[i] - '0');
		byte b = (byte)( (n1 << 4) | n2 );
		return b & 0xFF;
		
	}
	
	static void toHexByte( byte b, StringBuffer sb ) {
			int n1 = (b & 0xF0 ) >> 4 ;
			int n2 = (b & 0xF ) ;
			sb.append((char) ( n1 < 10 ? n1 + '0' : (n1 - 10)  + 'A' ) );
			sb.append( (char) (n2 < 10 ? n2 + '0' : (n2 - 10)  + 'A' ) );
		
	}
	

	static String toHex(byte[] data) {
		StringBuffer sb = new StringBuffer( data.length * 2 );
		for(byte b : data )
		    toHexByte( b , sb );
		return sb.toString();
		
	}
	static void toHexChar( char ch, StringBuffer sb )
	{
		if( (ch & 0xFF00) != 0 )
			toHexByte( (byte) ((ch >> 8 ) & 0xFF), sb  );
		toHexByte( (byte) (ch & 0xFF), sb  );
		
		
	}
	// Simplified compatible versions of xdmp:encode-for-NCName and xdmp:decode-from-NCName
	static String encodeForNCName( String name )
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
				(bFirst? !isInitialNameChar(ch) : 
				!isNameChar(ch))){
				  sb.append('_');
			      toHexChar( ch , sb );
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
	
	static String decodeFromNCName( String name )
	{

		  StringBuffer sb = new StringBuffer( name.length() * 2 );
		  boolean escaped = false;

          char chars[] = name.toCharArray() ;

 		  for( int i = 0 ; i < chars.length ; i++ )
 		  {
 			char ch = chars[i];
 			
		    if( ch == '_' ){
		      escaped = true ;
		      char c = 0;
		     
		 
		      while( ++i < chars.length-1 ){
		    	if( chars[i] == '_' ){
		    		break ;
		    	}
			    c <<= 8;
		    	c |= fromHexChars( chars , i );
		    	i +=2 ;
	

		      } 

		      if( c == 0 )
		        sb.append('_');
		      else
		        sb.append( c );



		    } else  
		    	sb.append(ch);
 		  }
 		  if( ! escaped )
 			  return name ;
 		  return sb.toString();
	}

	 static boolean isNameChar(char ch) {
		
		if (ch==':') return true;
		  if (ch<'A') return false;
		  if (ch<='Z') return true;
		  if (ch=='_') return true;
		  if (ch<'a') return false;
		  if (ch<='z') return true;
		  if (ch==0xb7) return true;
		  if (ch<0xc0) return false;
		  if (ch<=0xd6) return true;
		  if (ch<0xd8) return false;
		  if (ch<=0xf6) return true;
		  if (ch<0xf8) return false;
		  if (ch<=0x37d) return true;
		  if (ch<0x37f) return false;
		  if (ch<=0x1fff) return true;
		  if (ch<0x200c) return false;
		  if (ch<=0x200d) return true;
		  if (ch<0x203f) return false;
		  if (ch<=0x2040) return true;
		  if (ch<0x2070) return false;
		  if (ch<=0x218f) return true;
		  if (ch<0x2c00) return false;
		  if (ch<=0x2fef) return true;
		  if (ch<0x3001) return false;
		  if (ch<=0xd7ff) return true;
		  if (ch<0xf900) return false;
		  if (ch<=0xfdcf) return true;
		  if (ch<0xfdf0) return false;
		  if (ch<=0xfffd) return true;
		  return false; 
		
	}

	 static boolean isInitialNameChar(char ch) {
		 if (ch==':') return true;
		  if (ch<'A') return false;
		  if (ch<='Z') return true;
		  if (ch=='_') return true;
		  if (ch<'a') return false;
		  if (ch<='z') return true;
		  if (ch<0xc0) return false;
		  if (ch<=0xd6) return true;
		  if (ch<0xd8) return false;
		  if (ch<=0xf6) return true;
		  if (ch<0xf8) return false;
		  if (ch<=0x2ff) return true;
		  if (ch<0x370) return false;
		  if (ch<=0x37d) return true;
		  if (ch<0x37f) return false;
		  if (ch<=0x1fff) return true;
		  if (ch<0x200c) return false;
		  if (ch<=0x200d) return true;
		  if (ch<0x2070) return false;
		  if (ch<=0x218f) return true;
		  if (ch<0x2c00) return false;
		  if (ch<=0x2fef) return true;
		  if (ch<0x3001) return false;
		  if (ch<=0xd7ff) return true;
		  if (ch<0xf900) return false;
		  if (ch<=0xfdcf) return true;
		  if (ch<0xfdf0) return false;
		  if (ch<=0xfffd) return true;
		  return false;
	
	
	
	}

	static String getArg( String[] args , String name , String def )
	{
	
		for(int i = 0 ; i < args.length-1 ; i+=2 ){
			if( args[i].startsWith("-")){
				if( args[i].substring(1).equals(name) )
					return args[i+1];
			}
				else
			        break;
		}
		return def ;
		
	}

	static boolean hasArg( String[] args , String name  )
	{
	
		for(int i = 0 ; i < args.length ; i++ ){
			if( args[i].startsWith("-")){
				if( args[i].substring(1).equals(name) )
					return true;
			}
		}
		return false ;
		
	}
	
/**
	 * 
	 */
	static <E extends Enum<E>> int  enumSetToFlags(EnumSet<E>  set )
	{
		int flags =0;
		for( E e : set ){
			flags |= 1 << e.ordinal();  
		}
		return flags ;
		
	}
	
	static <E extends Enum<E>> String enumSetToString( EnumSet<E> set )
	{
		StringBuffer sb = new StringBuffer();
		for( Enum<E> e : set ){
			if( sb.length() > 0 )
			    sb.append(" ");
			sb.append( e.toString() );
		}
		return sb.toString();
	}
	
	static <E extends Enum<E>> EnumSet<E> flagsToEnumSet( Class<E> defs , int flags  )
	{
          EnumSet<E> all = EnumSet.noneOf(defs);
          for( E e : defs.getEnumConstants() ) 
        	  if( ((1 << e.ordinal()) & flags ) != 0 )
        		  all.add(e);
          return all ;
	}
	
	static <E extends Enum<E>> EnumSet<E>  stringsToEnumSet(Class<E> defs , String string )
	{
		String[] strings = string.split(" ");
        EnumSet<E> all = EnumSet.noneOf(defs);

	   for( String s : strings ){
		   E e = Enum.valueOf(defs, s);
		   all.add(e);
		   
	   }
	   return all ;
	}
	
	 static String getEnumName( Enum e )
 	{
		 String sname = e.name();
		 if( sname.startsWith("_" ) )
			 return sname.substring(1);
		 else
 		 return sname.replace('_','-');
 	}

}
