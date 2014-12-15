An Example REST app written using Play
=================================

This app was written using the Typesafe Activator.  The easiest way to get going with this is to go here:

https://www.playframework.com/download

To startup the activator shell 'cd' into the main project directory and run 'activator'.

##Running Unit Tests

From the activator shell run 'test'.  All of the REST tests are in AnalysisSpec.scala

##Running the Server

From the activator shell run 'run'.  The server will be accessible from localhost:9000.  Before actually trying to
use the API, you must go here: http://localhost:9000/ and click the red button to initialize the database.

##Using the API

**Perform Analysis (POST localhost:9000/analysis)**

The body of the request should contain the file content.

The response will look something like this:

```json
{"wordCount":5,"wordMap":{"Test":1,"Hello":2,"World":2},"id":1}
```

- *wordCount* is the total word count in the file
- *wordMap* is a map containing counts for each word in the file
- *id* is the analysis id

You can also optionally tack on a ?=ignoreFilter to ignore words containing the filter text.

**Get a list of previous analysis data (GET localhost:9000/analysis)**

The response will look something like this:

```json
[{"wordCount":5,"wordMap":{"Test":1,"Hello":2,"World":2},"id":1},{"wordCount":3,"wordMap":{"Test":2,"Hello":1},"id":2}]
```

**Get a single past analysis (GET localhost:9000/analysis/:id)**

* :id is the analysis ID
