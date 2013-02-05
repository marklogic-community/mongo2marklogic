sample.zip contains sample json data from a random sampling of superbowl 2013 related twitter tweets.
You can import into mongodb by doing: 

unzip sample.zip
mongoimport -d test -c twitter --file sample.json