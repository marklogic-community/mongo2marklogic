package com.marklogic.mongo;

import java.util.Random;

import javax.xml.stream.XMLStreamException;

import com.marklogic.xcc.exceptions.XccConfigException;

public abstract class Importer
{

    private Random mRandom = new Random();


    abstract void run(String[] args) throws XccConfigException, Exception;


    protected synchronized void log(String string)
    {
    	System.out.println(string);
    	
    }


    synchronized void log(String string, Exception e)
    {
    	
    	System.out.println(string);
    	e.printStackTrace();
    }


    protected String getRandom()
    {
    	byte[] bytes = new byte[12];
    	mRandom.nextBytes(bytes);
    	
    	return BSON.toHex(bytes);
    	
    	
    }


    protected XMLWriter getWriter(String[] args) throws XMLStreamException
    {
        String type = getArg(args ,"writer","json");
        if( type.equals("bson"))
           return new BSONXMLWriter();
        else
            return new JSONXMLWriter();
    }


    /**
     * @param args
     * @throws Exception 
     * @throws XccConfigException 
     */
    public static void main(String[] args) throws XccConfigException, Exception
    {

        if( hasArg(args,"directory"))
            new FileImporter().run(args);
        else
        if(hasArg(args,"connection"))
            new XCCImporter().run(args);
        else
            usage();

    }

    static boolean hasArg(String[] args, String name)
    {
    
    	for(int i = 0 ; i < args.length ; i++ ){
    		if( args[i].startsWith("-")){
    			if( args[i].substring(1).equals(name) )
    				return true;
    		}
    	}
    	return false ;
    	
    }

    protected static String getArg(String[] args, String name, String def)
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


    private static void usage()
    {
    	System.err
    			.println("Usage: Importer [-input file] -connection conn [-root root] [-collection collection] [-threads n] [-batch n]  [-writer json|bson]");
    	System.err 
                .println("       Importer [-input file] -directory output [-writer json|bson]");
    	System.exit(1);
    
    }

}
