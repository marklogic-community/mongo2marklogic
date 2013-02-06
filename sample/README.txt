sample.zip contains sample json data from a random sampling of superbowl 2013 related twitter tweets.
You can import into mongodb by doing: 

unzip sample.zip
mongoimport -d test -c twitter --file sample.json

Also included is the BSON file produced by dumping out this json file as BSON using the command
   mongodump
   
The file twitter.bson is the binary  dump file produced by mongodump and can be used as an example 
file for importing using mongo2marklogic