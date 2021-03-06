<a href="http://developer.marklogic.com/labs/mongo2marklogic"><img src="http://developer.marklogic.com/media/mongo2marklogic.png" alt="mongo2marklogic" title="mongo2marklogic"/></a>

# MarkLogic Converter for MongoDB<sup>®</sup> 

The converter is a Java-based tool for transfering data from MongoDB into MarkLogic's [Enterprise NoSQL][] database. 

It reads JSON data from MongoDB's [mongodump][] tool and loads data into MarkLogic using a [MarkLogic XDBC Server][].

Here is a concrete [Example][].

# Build 

To build the converter, pull down the code from https://github.com/marklogic/mongo2marklogic and run

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
          Example: xcc://admin:admin@localhost:9003
   
    -directory dir
          Specifies documents are to be written to a local filesystem directory "dir" instead of storing in a
          MarkLogic server
    
    -writer  json|bson
          Indicates which format is used for conversion from BSON to MarkLogic documents.  
          "json" - uses the MarkLogic basic strategy for converting from JSON.  
                    (See http://docs.marklogic.com/guide/app-dev/json for details.)
          "bson" - use a conversion designed to fully maintain Mongo's BSON semantics.
                    (Details TBD).
    -id
          Indicates to use the _id member of the top level Mongo object to create the generated document
          name instead of using a random number.
   
Documents are created named `<root><random #>.xml` or `<root><random #>.json` unless the -id argument is present.
If the -id arguemnt is present then Documents are named `<root><_id>.xml` or `<root><_id>.json`



# Usage

To import a collection generated from mongodump and store to a MarkLogic server run:    

    % java -jar mongo2marklogic.jar  [-input file] -connection conn \
         [-root root] [-collection collection] \
         [-threads n] [-batch n] [-writer json|bson]
   
To read a collection generated from mongodump, convert to MarkLogic format, and store to a local filesystem directory:

    % java -jar mongo2marklogic.jar [-input file] -directory dir [-writer json|bson]

# License
 
The converter is licensed under the Apache License, Version 2.0 (see [LICENSE.txt][]).

[MarkLogic]: http://developer.marklogic.com    
[LICENSE.txt]: https://github.com/marklogic/mongo2marklogic/blog/master/LICENSE.txt
[Enterprise NoSQL]: http://developer.marklogic.com/products/marklogic-server/enterprise-nosql
[Download MarkLogic]: http://developer.marklogic.com/products
[Architectural Summary]: http://developer.marklogic.com/learn/arch/diagram-101
[free license]: http://developer.marklogic.com/developer
[MarkLogic XDBC Server]: http://docs.marklogic.com/guide/admin/xdbc#id_21458
[mongodump]: http://docs.mongodb.org/manual/reference/mongodump/
[MarkLogic Setup Screen Cast]: http://www.youtube.com/watch?feature=player_embedded&v=n4Oem-DsQaU
[XCC Sessions]: http://docs.marklogic.com/guide/xcc/concepts#id_15580
[Working with JSON in MarkLogic]: http://docs.marklogic.com/guide/app-dev/json
[BSON]: http://bsonspec.org/
[Example]: https://github.com/marklogic/mongo2marklogic/tree/master/sample
