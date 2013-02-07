![mongo2marklogic](http://developer.marklogic.com/media/mongo2marklogic.png)

# Getting your data out of Mongo 

Use the [mongodump][] tool to get you data out of MongoDB.  To dump all your databases and collections 
from Mongo, do:

    % mongodump 
    
Look in the directory named `dump` that it creates.  And pluck out a collection you'd like to import
into MarkLogic.

In this example, we import a collection named `twitter` that contains a random sample of 
Superbowl 2013 related tweets.  You can find the data from [mongodump][] in the
.zip file in the [sample][] directory.  (You can also find the source json data we 
loaded into mongo named `sample.json` in the .zip file).

# Install and Set up MarkLogic

1. [Download MarkLogic][], browse to the [admin interface on port 8001](http://localhost:8001), and 
   request a free license. After that, create a database named `Import-DB` 
   and set up a REST API instance on it using port `8003`.
   You can use a different database name and port, but this example uses these values. The 
   [MarkLogic Setup Screen Cast][] will help you through these steps, if you get stuck.
2. Use the Admin UI on port 8001 to create a MarkLogic XDBC Server with the following details:

        Server Name: Import-XDBC
        Root: /
        Port: 9003
        Database: Import-DB 
    
    Use the defaults for everything else. You can use a different server name or port or another database 
    if you choose. This example uses port 9003 and assumes you are starting with an empty database. 
    For more details, please see section in the Administrator's Guide on [Creating XDBC Servers][MarkLogic XDBC Server].

# Build mongo2marklogic

If you haven't yet, you must build mongo2marklogic. Pull down the code from https://github.com/marklogic/mongo2marklogic and run

    % ant jar

This creates a file called `mongo2marklogic.jar`.   

# Importing into MarkLogic

You will need a username and password to connect to MarkLogic. (You can use the admin user account if you have not yet created any other).  To import the sample `twitter.bson` dump into MarkLogic and place it into a collection named `twitter` :

    % cd sample
    % unzip sample.zip
    % cd ..
    % java -Xmx1024m  -jar mongo2marklogic.jar \
         -input sample/twitter.bson \
         -connection xcc://username:password@localhost:9003 \
         -root / \
         -collection twitter
    
# Searching your data

You can get a count of the documents in your database via the REST API like

    curl --anyauth --username 'user:password' 'http://localhost:8003/v1/search?q=&format=json' \
        blah blah...

To do a simple fulltext search across the entire documents, you can do

    curl --anyauth --usernae 'user:password' 'blah....'

# Next Steps

You may want to read [Working with JSON in MarkLogic][] or [learn about the MarkLogic REST API][]

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
[learn about the MarkLogic REST API]: http://developer.marklogic.com/learn/rest
[Working with JSON in MarkLogic]: http://docs.marklogic.com/guide/app-dev/json
[BSON]: http://bsonspec.org/
[Example]: https://github.com/marklogic/mongo2marklogic/wiki/Example
[sample]: https://github.com/marklogic/mongo2marklogic/tree/master/sample
