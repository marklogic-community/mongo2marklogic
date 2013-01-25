# mongo2marklogic 

mongo2marklogic is a Java-based tool for importing data from MongoDB into MarkLogic's [Enterprise NoSQL][] database. 

It reads data from MongoDB's [mongoexport][] tool and loads data using a [MarkLogic XDBC Server][].

If you are new to MarkLogic, go

 1. [Download MarkLogic][], browse to the [admin interface on port 8001](http://localhost:8001), and request a free license.
You may find this [MarkLogic Setup Screen Cast][] helpful as well.
 2. [Create a MarkLogic XDBC server][MarkLogic XDBC Server].
 3. You may want to read [Working with JSON in MarkLogic][].

After that you may choose to 
 * Use MarkLogic's REST API to search your data (link to wiki TBD)
 * Build a search application and explore your data using MarkLogic's Application Builder tool. (link to wiki TBD)

# Build

To build mongo2marklogic, run

    % ant jar

This creates a file called `mongo2marklogic.jar`.   
   
# Command-line Options

    -input filename   
          Specifies the input bson file
          Default: reads from stdin
    
    -threads n
          Specifies the number of threads to use for writing to the MarkLogic server
          Default: 1
    
    -batch  n
          Specifies the number of documents to batch in a single transaction
          Default: 100
          
    -root uri
          Specifies a root URI underwhich to place the documents in MarkLogic
          Default: "" 
          
    -collection coll
           Specifies a MarkLogic `collection` into which to place the documents
           Default: none
          
    -connection  url
          Specifies an xcc or xccs connection to your MarkLogic XDBC server.  
          (See http://docs.marklogic.com/guide/xcc/concepts#id_15580 for details.)
          Default: xcc://localhost:9003
   
    -directory dir
          Specifies documents are to be written to a local filesystem directory "dir" instead of storing in a MarkLogic server
    
    -writer  json|bson
          Indicates which format is used for conversion from BSON to MarkLogic documents.  
          "json" - uses the MarkLogic basic strategy for converting from JSON.  
                    (See http://docs.marklogic.com/guide/app-dev/json for details.)
          "bson" - use a conversion designed to fully maintain Mongo's BSON semantics.
                    (Details TBD).
   
Documents are created named `<root><random #>.xml`
      
# Examples

To import a collection generated from mongodump and store to a MarkLogic server run:    

    % java -jar mongo2marklogic.jar  [-input file] -connection conn \
         [-root root] [-collection collection] \
         [-threads n] [-batch n] [-writer json|bson]
   
To import a collection generated from mongodump and store to a local filesystem directory:

    % java -jar mongo2marklogic.jar [-input file] -directory dir [-writer json|bson]

# License

mongo2marklogic is licensed under the Apache License, Version 2.0 (see [LICENSE.txt][]).

[MarkLogic]: http://developer.marklogic.com    
[LICENSE.txt]: https://github.com/marklogic/mongo2marklogic/blog/master/LICENSE.txt
[Enterprise NoSQL]: http://developer.marklogic.com/products/marklogic-server/enterprise-nosql
[Download MarkLogic]: http://developer.marklogic.com/products
[Architectural Summary]: http://developer.marklogic.com/learn/arch/diagram-101
[free license]: http://developer.marklogic.com/express
[MarkLogic XDBC Server]: http://docs.marklogic.com/guide/admin/xdbc#id_21458
[mongoexport]: http://docs.mongodb.org/manual/reference/mongoexport/
[MarkLogic Setup Screen Cast]: http://www.youtube.com/watch?feature=player_embedded&v=n4Oem-DsQaU
[XCC Sessions]: http://docs.marklogic.com/guide/xcc/concepts#id_15580
[Working with JSON in MarkLogic]: http://docs.marklogic.com/guide/app-dev/json
[BSON]: http://bsonspec.org/
