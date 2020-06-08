# Step 2: Upload Data to an Amazon ES Domain for Indexing<a name="es-gsg-upload-data"></a>

**Important**  
This process is a concise tutorial for uploading a small amount of test data\. For more information, see [Indexing Data in Amazon Elasticsearch Service](es-indexing.md)\.

You can upload data to an Amazon Elasticsearch Service domain using the command line or most programming languages\.

The following example requests use [curl](https://curl.haxx.se/), a common HTTP client, for brevity and convenience\. Clients like curl can't perform the request signing that is required if your access policies specify IAM users or roles\. To successfully perform the instructions in this step, you must use fine\-grained access control with a master user name and password, like you configured in [step 1](es-gsg-create-domain.md)\.

You can install curl on Windows and use it from the command prompt, but we recommend a tool like [Cygwin](https://www.cygwin.com/) or [the Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10)\. macOS and most Linux distributions come with curl pre\-installed\.

**To upload a single document to an Amazon ES domain**
+ Run the following command to add a single document to the *movies* domain:

  ```
  curl -XPUT -u master-user:master-user-password domain-endpoint/movies/_doc/1 -d '{"director": "Burton, Tim", "genre": ["Comedy","Sci-Fi"], "year": 1996, "actor": ["Jack Nicholson","Pierce Brosnan","Sarah Jessica Parker"], "title": "Mars Attacks!"}' -H 'Content-Type: application/json'
  ```

For a detailed explanation of this command and how to make signed requests to Amazon ES, see [Indexing Data in Amazon Elasticsearch Service](es-indexing.md)\.

**To upload a JSON file that contains multiple documents to an Amazon ES domain**

1. Create a file called `bulk_movies.json`\. Copy and paste the following content into it, and add a trailing newline:

   ```
   { "index" : { "_index": "movies", "_id" : "2" } }
   {"director": "Frankenheimer, John", "genre": ["Drama", "Mystery", "Thriller", "Crime"], "year": 1962, "actor": ["Lansbury, Angela", "Sinatra, Frank", "Leigh, Janet", "Harvey, Laurence", "Silva, Henry", "Frees, Paul", "Gregory, James", "Bissell, Whit", "McGiver, John", "Parrish, Leslie", "Edwards, James", "Flowers, Bess", "Dhiegh, Khigh", "Payne, Julie", "Kleeb, Helen", "Gray, Joe", "Nalder, Reggie", "Stevens, Bert", "Masters, Michael", "Lowell, Tom"], "title": "The Manchurian Candidate"}
   { "index" : { "_index": "movies", "_id" : "3" } }
   {"director": "Baird, Stuart", "genre": ["Action", "Crime", "Thriller"], "year": 1998, "actor": ["Downey Jr., Robert", "Jones, Tommy Lee", "Snipes, Wesley", "Pantoliano, Joe", "Jacob, Ir\u00e8ne", "Nelligan, Kate", "Roebuck, Daniel", "Malahide, Patrick", "Richardson, LaTanya", "Wood, Tom", "Kosik, Thomas", "Stellate, Nick", "Minkoff, Robert", "Brown, Spitfire", "Foster, Reese", "Spielbauer, Bruce", "Mukherji, Kevin", "Cray, Ed", "Fordham, David", "Jett, Charlie"], "title": "U.S. Marshals"}
   { "index" : { "_index": "movies", "_id" : "4" } }
   {"director": "Ray, Nicholas", "genre": ["Drama", "Romance"], "year": 1955, "actor": ["Hopper, Dennis", "Wood, Natalie", "Dean, James", "Mineo, Sal", "Backus, Jim", "Platt, Edward", "Ray, Nicholas", "Hopper, William", "Allen, Corey", "Birch, Paul", "Hudson, Rochelle", "Doran, Ann", "Hicks, Chuck", "Leigh, Nelson", "Williams, Robert", "Wessel, Dick", "Bryar, Paul", "Sessions, Almira", "McMahon, David", "Peters Jr., House"], "title": "Rebel Without a Cause"}
   ```

1. Run the following command to upload the file to the *movies* domain:

   ```
   curl -XPOST -u master-user:master-user-password domain-endpoint/_bulk --data-binary @bulk_movies.json -H 'Content-Type: application/json'
   ```

For more information about the bulk file format, see [Indexing Data in Amazon Elasticsearch Service](es-indexing.md)\.