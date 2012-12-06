package com.marklogic.mongo;

class MongoDoc {

	String url ;
	byte[] bytes;
	public MongoDoc(String url, byte[] bytes) {
		this.url = url;
		this.bytes = bytes;
	}
	
}