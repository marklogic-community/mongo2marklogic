MongoImport README

MongoImport is a POC for support for importing data from a Mongo Database into a MarkLogic dataase.
BUILD

    ant jar

Result
   mongoimport.jar
   
   
RUN


To import a collection generated from mongodump run  
   java -jar mongoimport.jar [-input file] -connection conn [-root root] [-collection collection] [-threads n] [-batch n]
   
   
Options
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
          Specifies the root URI to place generated XML files in MarkLogic
          Default: "" 
          
   -collection coll
           Specifies the collection to place the documents
           Default: none
          
   -connection  url
          Specifies the xcc or xccs connection 
          Default: xcc://localhost:9003
          
   
   
Documents are created named 

      <root><random #>.xml
      
      
  
   