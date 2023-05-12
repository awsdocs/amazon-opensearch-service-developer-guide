# Searching data in Amazon OpenSearch Service<a name="searching"></a>

There are several common methods for searching documents in Amazon OpenSearch Service, including URI searches and request body searches\. OpenSearch Service offers additional functionality that improves the search experience, such as custom packages, SQL support, and asynchronous search\. For a comprehensive OpenSearch search API reference, see the [OpenSearch documentation](https://opensearch.org/docs/opensearch/query-dsl/full-text/)\.

**Note**  
The following sample requests work with OpenSearch APIs\. Some requests might not work with older Elasticsearch versions\.

**Topics**
+ [URI searches](#searching-uri)
+ [Request body searches](#searching-dsl)
+ [Paginating search results](#searching-paginating)
+ [Dashboards Query Language](#DashboardsQueryLanguages)
+ [Custom packages for Amazon OpenSearch Service](custom-packages.md)
+ [Querying your Amazon OpenSearch Service data with SQL](sql-support.md)
+ [k\-Nearest Neighbor \(k\-NN\) search in Amazon OpenSearch Service](knn.md)
+ [Cross\-cluster search in Amazon OpenSearch Service](cross-cluster-search.md)
+ [Learning to Rank for Amazon OpenSearch Service](learning-to-rank.md)
+ [Asynchronous search in Amazon OpenSearch Service](asynchronous-search.md)
+ [Point in time in Amazon OpenSearch Service](pit.md)

## URI searches<a name="searching-uri"></a>

Universal Resource Identifier \(URI\) searches are the simplest form of search\. In a URI search, you specify the query as an HTTP request parameter:

```
GET https://search-my-domain.us-west-1.es.amazonaws.com/_search?q=house
```

A sample response might look like the following:

```
{
  "took": 25,
  "timed_out": false,
  "_shards": {
    "total": 10,
    "successful": 10,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 85,
      "relation": "eq",
    },
    "max_score": 6.6137657,
    "hits": [
      {
        "_index": "movies",
        "_type": "movie",
        "_id": "tt0077975",
        "_score": 6.6137657,
        "_source": {
          "directors": [
            "John Landis"
          ],
          "release_date": "1978-07-27T00:00:00Z",
          "rating": 7.5,
          "genres": [
            "Comedy",
            "Romance"
          ],
          "image_url": "http://ia.media-imdb.com/images/M/MV5BMTY2OTQxNTc1OF5BMl5BanBnXkFtZTYwNjA3NjI5._V1_SX400_.jpg",
          "plot": "At a 1962 College, Dean Vernon Wormer is determined to expel the entire Delta Tau Chi Fraternity, but those troublemakers have other plans for him.",
          "title": "Animal House",
          "rank": 527,
          "running_time_secs": 6540,
          "actors": [
            "John Belushi",
            "Karen Allen",
            "Tom Hulce"
          ],
          "year": 1978,
          "id": "tt0077975"
        }
      },
      ...
    ]
  }
}
```

By default, this query searches all fields of all indices for the term *house*\. To narrow the search, specify an index \(`movies`\) and a document field \(`title`\) in the URI:

```
GET https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search?q=title:house
```

You can include additional parameters in the request, but the supported parameters provide only a small subset of the OpenSearch search options\. The following request returns 20 results \(instead of the default of 10\) and sorts by year \(rather than by `_score`\):

```
GET https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search?q=title:house&size=20&sort=year:desc
```

## Request body searches<a name="searching-dsl"></a>

To perform more complex searches, use the HTTP request body and the OpenSearch domain\-specific language \(DSL\) for queries\. The query DSL lets you specify the full range of OpenSearch search options\.

**Note**  
You can't include Unicode special characters in a text field value, or the value will be parsed as multiple values separated by the special character\. This incorrect parsing can lead to unintentional filtering of documents and potentially compromise control over their access\. For more information, see [A note on Unicode special characters in text fields](https://opensearch.org/docs/latest/opensearch/query-dsl/index/#a-note-on-unicode-special-characters-in-text-fields) in the OpenSearch documentation\.

The following `match` query is similar to the final [URI search](#searching-uri) example:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search
{
  "size": 20,
  "sort": {
    "year": {
      "order": "desc"
    }
  },
  "query": {
    "query_string": {
      "default_field": "title",
      "query": "house"
    }
  }
}
```

**Note**  
The `_search` API accepts HTTP `GET` and `POST` for request body searches, but not all HTTP clients support adding a request body to a `GET` request\. `POST` is the more universal choice\.

In many cases, you might want to search several fields, but not all fields\. Use the `multi_match` query:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search
{
  "size": 20,
  "query": {
    "multi_match": {
      "query": "house",
      "fields": ["title", "plot", "actors", "directors"]
    }
  }
}
```

### Boosting fields<a name="searching-dsl-boost"></a>

You can improve search relevancy by "boosting" certain fields\. Boosts are multipliers that weigh matches in one field more heavily than matches in other fields\. In the following example, a match for *john* in the `title` field influences `_score` twice as much as a match in the `plot` field and four times as much as a match in the `actors` or `directors` fields\. The result is that films like *John Wick* and *John Carter* are near the top of the search results, and films starring John Travolta are near the bottom\.

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search
{
  "size": 20,
  "query": {
    "multi_match": {
      "query": "john",
      "fields": ["title^4", "plot^2", "actors", "directors"]
    }
  }
}
```

### Search result highlighting<a name="searching-dsl-highlighting"></a>

The `highlight` option tells OpenSearch to return an additional object inside of the `hits` array if the query matched one or more fields:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search
{
  "size": 20,
  "query": {
    "multi_match": {
      "query": "house",
      "fields": ["title^4", "plot^2", "actors", "directors"]
    }
  },
  "highlight": {
    "fields": {
      "plot": {}
    }
  }
}
```

If the query matched the content of the `plot` field, a hit might look like the following:

```
{
  "_index": "movies",
  "_type": "movie",
  "_id": "tt0091541",
  "_score": 11.276199,
  "_source": {
    "directors": [
      "Richard Benjamin"
    ],
    "release_date": "1986-03-26T00:00:00Z",
    "rating": 6,
    "genres": [
      "Comedy",
      "Music"
    ],
    "image_url": "http://ia.media-imdb.com/images/M/MV5BMTIzODEzODE2OF5BMl5BanBnXkFtZTcwNjQ3ODcyMQ@@._V1_SX400_.jpg",
    "plot": "A young couple struggles to repair a hopelessly dilapidated house.",
    "title": "The Money Pit",
    "rank": 4095,
    "running_time_secs": 5460,
    "actors": [
      "Tom Hanks",
      "Shelley Long",
      "Alexander Godunov"
    ],
    "year": 1986,
    "id": "tt0091541"
  },
  "highlight": {
    "plot": [
      "A young couple struggles to repair a hopelessly dilapidated <em>house</em>."
    ]
  }
}
```

By default, OpenSearch wraps the matching string in `<em>` tags, provides up to 100 characters of context around the match, and breaks content into sentences by identifying punctuation marks, spaces, tabs, and line breaks\. All of these settings are customizable:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search
{
  "size": 20,
  "query": {
    "multi_match": {
      "query": "house",
      "fields": ["title^4", "plot^2", "actors", "directors"]
    }
  },
  "highlight": {
    "fields": {
      "plot": {}
    },
    "pre_tags": "<strong>",
    "post_tags": "</strong>",
    "fragment_size": 200,
    "boundary_chars": ".,!? "
  }
}
```

### Count API<a name="searching-dsl-count"></a>

If you're not interested in the contents of your documents and just want to know the number of matches, you can use the `_count` API instead of the `_search` API\. The following request uses the `query_string` query to identify romantic comedies:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_count
{
  "query": {
    "query_string": {
      "default_field": "genres",
      "query": "romance AND comedy"
    }
  }
}
```

A sample response might look like the following:

```
{
  "count": 564,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  }
}
```

## Paginating search results<a name="searching-paginating"></a>

If you need to display a large number of search results, you can implement pagination using several different methods\. 

### Point in time<a name="pag-pit"></a>

The point in time \(PIT\) feature is a type of search that lets you run different queries against a dataset that's fixed in time\. This is the preferred pagination method in OpenSearch, especially for deep pagination\. You can use PIT with OpenSearch Service version 2\.5 and later\. For more information about PIT, see [Point in time in Amazon OpenSearch Service](pit.md)\.

### The `from` and `size` parameters<a name="pag-from-size"></a>

The simplest way to paginate is with the `from` and `size` parameters\. The following request returns results 20–39 of the zero\-indexed list of search results:

```
POST https://search-my-domain.us-west-1.es.amazonaws.com/movies/_search
{
  "from": 20,
  "size": 20,
  "query": {
    "multi_match": {
      "query": "house",
      "fields": ["title^4", "plot^2", "actors", "directors"]
    }
  }
}
```

For more information about search pagination, see [Paginate results](https://opensearch.org/docs/latest/opensearch/search/paginate/) in the OpenSearch documentation\.

## Dashboards Query Language<a name="DashboardsQueryLanguages"></a>

You can use the [Dashboards Query Language \(DQL\)](https://opensearch.org/docs/latest/dashboards/dql/#terms-query) to search for data and visualizations in OpenSearch Dashboards\. DQL uses four primary query types: *terms*, *Boolean*, *date and range*, and *nested field*\.

**Terms query**

A terms query requires you to specify the term that you're searching for\. 

To perform a terms query, enter the following:

```
host:www.example.com
```

**Boolean query**

You can use the Boolean operators `AND`, `or`, and `not` to combine multiple queries\.

To perform a Boolean query, paste the following:

```
host.keyword:www.example.com and response.keyword:200
```

**Date and range query**

You can use a date and range query to find a date before or after your query\.
+ `>` indicates a search for a date after your specified date\.
+ `<` indicates a search for a date before your specified date\.

`@timestamp > "2020-12-14T09:35:33"`

**Nested field query**

If you have a document with nested fields, you have to specify which parts of the document that you want to retrieve\. The following is a sample document that contains nested fields:

```
{"NBA players":[
    {"player-name": "Lebron James",
      "player-position": "Power forward",
      "points-per-game": "30.3"
    },
    {"player-name": "Kevin Durant",
      "player-position": "Power forward",
      "points-per-game": "27.1"
    },
    {"player-name": "Anthony Davis",
      "player-position": "Power forward",
      "points-per-game": "23.2"
    },
    {"player-name": "Giannis Antetokounmpo",
      "player-position": "Power forward",
      "points-per-game":"29.9"
    }
  ]
}
```

To retrieve a specific field using DQL, paste the following:

```
NBA players: {player-name: Lebron James}
```

To retrieve multiple objects from the nested document, paste the following:

```
NBA players: {player-name: Lebron James} and NBA players: {player-name: Giannis Antetokounmpo}
```

To search within a range, paste the following:

```
NBA players: {player-name: Lebron James} and NBA players: {player-name: Giannis Antetokounmpo and < 30}
```

If your document has an object nested within another object, you can still retrieve data by specifying all of the levels\. To do this, paste the following:

```
Top-Power-forwards.NBA players: {player-name:Lebron James}
```