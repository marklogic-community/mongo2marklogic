![mongo2marklogic](http://developer.marklogic.com/media/mongo2marklogic.png)

# Get your data out of Mongo 

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

1. [Download MarkLogic][], browse to the [Admin interface on port 8001](http://localhost:8001), and 
   request a [free license][]. After that, create a database named `Tweets-DB` 
   and set up a REST API instance on it using port `8003`.
   You can use a different database name and port, but this example uses these values. The 
   [MarkLogic Setup Screen Cast][] will help you through these steps if you get stuck.
2. Use the [Admin interface on port 8001](http://localhost:8001) to create a MarkLogic XDBC Server with the 
   following details:

        Server Name: Tweets-XDBC
        Root: /
        Port: 9003
        Database: Tweets-DB 
    
    Use the defaults for everything else. You can use a different server name or port or use another database 
    if you choose. This example uses port 9003 and assumes you are starting with an empty database. 
    For more details, see the section in the MarkLogic Administrator's Guide on 
    [Creating XDBC Servers][MarkLogic XDBC Server].

# Build the mongo2marklogic jar

If you haven't yet, you must build the mongo2marklogic jar. Pull down the code from 
https://github.com/marklogic/mongo2marklogic and run

    % ant jar

This creates a file called `mongo2marklogic.jar`.   

# Import your data into MarkLogic

You will need a username and password to connect to MarkLogic. (You can use the admin user account if you 
have not yet created any other).  To import the sample `twitter.bson` dump into MarkLogic and place 
it into a collection named `twitter` :

    % cd sample
    % unzip sample.zip
    % cd ..
    % java -Xmx1024m  -jar mongo2marklogic.jar \
         -input sample/twitter.bson \
         -connection xcc://username:password@localhost:9003 \
         -root / -collection twitter
    
# Search your data

First, you'll find it handy to configure the REST API to send back JSON errors 

    % curl -X PUT --anyauth --user 'user:password' \
        'http://localhost:8003/v1/config/properties/error-format?format=json' -d'{"error-format": "json"}'

Then, you can get a count of the documents in your database via the `search` end-point: 

    % curl -s --anyauth --user 'user:password' 'http://localhost:8003/v1/search?q=&format=json'

This returns a large chunk of JSON. You can use the command line [json tool][] to grab the total as

    % curl -s --anyauth --user 'user:password' 'http://localhost:8003/v1/search?q=&format=json' | json total

And you'll see there were 10,000 tweets in the collection. To do a simple fulltext search across 
the entire database and see the MarkLogic URIs (unique keys) for the first 10 tweets:

    % curl -s --anyauth --user 'user:password' 'http://localhost:8003/v1/search?q=&format=json' | \
       json results | json -a uri

You will get a response with URIs like:

    /9BF8E4474C9F12ECC582653D.json
    /01B1955B565482E590BA7D6C.json
    /EA5EE08E32CD676E52DE55A8.json
    /7059E9FBDDE131E550601B50.json
    /BDE73B671A1BDB17439D2F12.json
    /EF9A667F6587223CFB4696C5.json
    /9D4CB37E1A6E431B0A73C06E.json
    /274586CF3C8108A8372021BB.json
    /7C46DEC3597CD99B1CFA294B.json
    /4F194E4C65E72B0E99C89375.json

because the converter assigns URIs to documents using a random string. 
To find a single tweet, you can look it up by its URI:

    % curl -s --anyauth --user 'user:password' \
        'http://localhost:8003/v1/documents?uri=/4F194E4C65E72B0E99C89375.json&format=json' | json

To find a tweet based on its Mongo `_id`, you can use the `keyvalue` end-point as:

    % curl -s --anyauth --user 'user:password' \
        'http://localhost:8003/v1/keyvalue?key=_id&value=51144AC2892B1877BF620695&format=json' | json results | json -a uri

The REST API's `search` endpoint is quite powerful and flexible, yet it has a simple API for doing 
google-style searches.  For example, to find all the tweets that have the word 
`niners` OR `ravens` (case insensitive) in them, you can do

    % curl -s --anyauth --user 'user:password' 'http://localhost:8003/v1/search?q=niners%20OR%20ravens&format=json' |\
         json results

# Next Steps

To learn more about how MarkLogic stores JSON and the kinds of things you can do with it, 
see [Working with JSON in MarkLogic][]. To get a more thorough introduction to the MarkLogic REST API
see this [tutorial][learn about the MarkLogic REST API].  And if you'd like to know more about MarkLogic architecture
see this [article][arch-101].

[MarkLogic]: http://developer.marklogic.com    
[LICENSE.txt]: https://github.com/marklogic/mongo2marklogic/blog/master/LICENSE.txt
[Enterprise NoSQL]: http://developer.marklogic.com/products/marklogic-server/enterprise-nosql
[Download MarkLogic]: http://developer.marklogic.com/products
[Architectural Summary]: http://developer.marklogic.com/learn/arch/diagram-101
[free license]: http://developer.marklogic.com/free-developer
[MarkLogic XDBC Server]: http://docs.marklogic.com/guide/admin/xdbc#id_21458
[mongodump]: http://docs.mongodb.org/manual/reference/mongodump/
[MarkLogic Setup Screen Cast]: http://www.youtube.com/watch?feature=player_embedded&v=n4Oem-DsQaU
[XCC Sessions]: http://docs.marklogic.com/guide/xcc/concepts#id_15580
[learn about the MarkLogic REST API]: http://developer.marklogic.com/learn/rest
[Working with JSON in MarkLogic]: http://docs.marklogic.com/guide/app-dev/json
[sample]: https://github.com/marklogic/mongo2marklogic/tree/master/sample
[json tool]: https://github.com/trentm/json
[arch-101]: http://developer.marklogic.com/learn/arch/diagram-101
