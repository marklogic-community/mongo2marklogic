/**
 * $Id: $
 * $DateTime: $
 *
 */

package com.marklogic.mongo;

/*
 * struct MsgHeader {
    int32   messageLength; // total message size, including this
    int32   requestID;     // identifier for this message
    int32   responseTo;    // requestID from the original request
                           //   (used in reponses from db)
    int32   opCode;        // request type - see table below
    
    
}

OP_REPLY	 1	 Reply to a client request. responseTo is set
OP_MSG	 1000	 generic msg command followed by a string
OP_UPDATE	 2001	 update document
OP_INSERT	 2002	 insert new document
RESERVED	 2003	 formerly used for OP_GET_BY_OID
OP_QUERY	 2004	 query a collection
OP_GETMORE	 2005	 Get more data from a query. See Cursors
OP_DELETE	 2006	 Delete documents
OP_KILL_CURSORS	 2007	 Tell database client is done with a cursor



 */
public class MessageHeader {
	
	static final int kOP_REPLY = 1 ;
	static final int kOP_MSG = 1000 ;
	static final int kOP_UPDATE = 2001 ;
	static final int kOP_INSERT = 2002 ;
	static final int kOP_RESERVED  = 2003 ;
	static final int kOP_QUERY = 2004 ;
	static final int kOP_GETMORE = 2005 ;
	static final int kOP_DELETE = 2006 ;
	static final int kOP_KILL_CURSORS = 2007 ;
	
	
	int 	messageLength ;
	int		requestID ; 
	int		responseTo ;
	int		opCode ;
	

}
