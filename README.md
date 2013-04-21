# jongo

## A RESTful Interface for JDBC

Jongo is a Java server which provides CRUD operations over any JDBC supported RDBMS using REST.

By REST we basically mean that the different CRUD operations are performed by different HTTP methods:

* POST to create a resource
* GET to read a resource
* PUT to update a resource
* DELETE to delete a resource

Jongo is based on the premise that you love your database, hence there's no administration. If there's something missing in Jongo, it probably means you have to do it in your database (roles, triggers, stored procedures, views, etc.)

## Features
* Easy installation & configuration.
* Support for any RDBMS with a JDBC Driver.
* Support for multiple databases on a single Jongo instance.
* Tested for security and performance.
* No administration.
* Grails style Dynamic Finders
* Model/Store API for Python.
* Convention over configuration
* Call functions and stored procedures.
* Deploy in your favorite JEE application server (JBoss, Tomcat, Glassfish).

##Usages
This are some projects where Jongo is ideal:

* JavaScript applications without any server-side coding (backbone.js, ExtJS, jQuery).
* Python, Perl and Bash scripts without any database driver.
* ETL & KPIs.
* Data-warehousing.
* Cron jobs.
* Provide restricted access to a database server.

## Future
* Google Apps Engine (GAE) support
* OAuth authentication
* More RDBMS dialects
* NoSQL database dialects

You can find more news & articles in my [blog](http://monocaffe.blogspot.com/search/label/jongo)

## Examples
This examples are from data when running Jongo en demo mode.

### Read (GET) a resource
To read (GET) a user with id 1 from its table, we perform the following:
```
$curl -i -X GET -H "Accept: application/json" "http://localhost:8080/jongo/demo1/user/1"
HTTP/1.1 200 OK
Content-Type: application/json
Date: 2012-03-26T23:23:48.470+02:00
Content-Count: 1
Content-Location: user
Content-MD5: uLcUX7L3SZknHpLvhUkERg==
Content-Length: 143
Server: Jetty(8.1.2.v20120308)
```
```json
{
   "success":true,
   "cells":[
      {
         "birthday":"1992-01-15",
         "credit":"0.00",
         "lastupdate":"2012-03-27T14:23:48.466+02:00",
         "name":"bar",
         "age":33,
         "id":1
      }
   ]
}
```

This is the full HTTP with its header and body. As you can see, we asked in the HTTP request header `Accept: application/json` for a JSON representation of the user entity with id 1 in the database/schema `demo1`.

By default, jongo uses JSON as the transport format, but you can also use XML by simply changing the Accept header:

```
$ curl -i -X GET -H "Accept: application/xml" "http://localhost:8080/jongo/demo1/user/1"
HTTP/1.1 200 OK
Content-Type: application/xml
Date: 2012-03-26T23:31:06.110+02:00
Content-Count: 1
Content-Location: user
Content-MD5: eOsCCV/1/zkXhxKIRMV4ug==
Content-Length: 261
Server: Jetty(8.1.2.v20120308)
```

```xml
<response>
  <success>true</success>
  <resource>user</resource>
  <rows>
    <row roi="0">
      <columns>
        <birthday>1992-01-15</birthday>
        <credit>45.00</credit>
        <lastupdate>2012-03-27T14:31:06.110+02:00</lastupdate>
        <name>foo user</name>
        <age>30</age>
        <id>1</id>
      </columns>
    </row>
  </rows>
</response>
```

If the resource doesn't exists, you'll get a 404

```
$ curl -i -X GET -H "Accept: application/json" "http://localhost:8080/jongo/demo1/user/2"
HTTP/1.1 404 Not Found
Content-Type: application/json
Date: 2012-03-26T23:37:54.100+02:00
Content-MD5: 0+WJ/rKID3SXLnjerbee1g==
Content-Length: 39
Content-Length: 39
Content-Location: user
Server: Jetty(8.1.2.v20120308)
```

```json
{"success":false,"message":"Not Found"}
```

Depending on RDBMS support, SQL errors will be mapped to HTTP errors.  

### Create (POST) a resource
To insert (POST) a new registry in the user table, we perform the following:

```
$curl -i -X POST -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -d '{"name":"my foo user", "age":30, "credit":45.0}' \
  "http://localhost:8080/jongo/demo1/user"
HTTP/1.1 201 Created
Content-Type: application/json
Date: 2012-03-26T23:29:17.125+02:00
Content-Count: 1
Content-MD5: eLzHpBV+Q6y46F/teUejMA==
Content-Length: 30
Server: Jetty(8.1.2.v20120308)
```

```json
{"success":true,"cells":[ {}]}
```

The HTTP response code changed to 201 (Created) which means the entity was created successfully.

### Update (PUT) a resource

You can also update values by providing a PUT request with the ID and the columns/values.

```
$ curl -i -X PUT -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  -d '{"name":"im updated!", "age":40}' \
  "http://localhost:8080/jongo/demo1/user/3"
HTTP/1.1 200 OK
Content-Type: application/json
Date: 2012-03-26T23:36:14.299+02:00
Content-Count: 1
Content-Location: user
Content-MD5: dtt+NFNh9z5Ssh5ZYGt9yw==
Content-Length: 152
Server: Jetty(8.1.2.v20120308)
```

```json
{
   "success":true,
   "cells":[
      {
         "birthday":"2012-03-27",
         "credit":"45.00",
         "lastupdate":"2012-03-27T14:36:14.299+02:00",
         "name":"im updated!",
         "age":40,
         "id":3
      }
   ]
}
```

### Delete (DELETE) a resource

To delete you use a DELETE request with the ID in the table.
```
curl -i -X DELETE -H "Accept: application/json"  "http://localhost:8080/jongo/demo1/user/2"
HTTP/1.1 200 OK
Content-Type: application/json
Date: 2012-03-26T23:37:20.803+02:00
Content-Count: 1
Content-Location: user
Content-MD5: eLzHpBV+Q6y46F/teUejMA==
Content-Length: 30
Server: Jetty(8.1.2.v20120308)
```
```json
{"success":true,"cells":[ {}]}
```

### Tables and meta

For Jongo, everything after the jongo/ path is an accessible resource, for example, the demo1 schema:

```
$ curl -i -X GET -H "Accept: application/json" "http://localhost:8080/jongo/demo1"
HTTP/1.1 200 OK
Content-Type: application/json
Date: 2012-03-26T23:39:36.340+02:00
Content-Count: 8
Content-Location: demo1
Content-MD5: Fq5vDIHzxEYTBBeamLXbGQ==
Content-Length: 2337
Server: Jetty(8.1.2.v20120308)
```

```json
{
   "success":true,
   "cells":[
      {
         "table_name":"CAR",
         "type_cat":"null",
         "remarks":"null",
         "type_schem":"null",
         "hsqldb_type":"MEMORY",
         "commit_action":"null",
         "table_schem":"PUBLIC",
         "table_cat":"PUBLIC",
         "self_referencing_col_name":"null",
         "type_name":"null",
         "ref_generation":"null",
         "table_type":"TABLE",
         "read_only":"FALSE"
      },
      ...
      {
         "table_name":"USER",
         "type_cat":"null",
         "remarks":"null",
         "type_schem":"null",
         "hsqldb_type":"MEMORY",
         "commit_action":"null",
         "table_schem":"PUBLIC",
         "table_cat":"PUBLIC",
         "self_referencing_col_name":"null",
         "type_name":"null",
         "ref_generation":"null",
         "table_type":"TABLE",
         "read_only":"FALSE"
      }
   ]
}
```

If you ask for the database, its entities are returned. You can also query for a table's metadata with HEAD

```
$ curl -i -X HEAD -H "Accept: application/json" "http://localhost:8080/jongo/demo1/user"
HTTP/1.1 200 OK
Content-Type: application/json
Date: 2012-03-26T23:42:15.254+02:00
Content-Location: user
User: ID=INTEGER(11);NAME=VARCHAR(25);AGE=INTEGER(11);BIRTHDAY=DATE(10);LASTUPDATE=TIMESTAMP(26);CREDIT=DECIMAL(8)
Content-MD5: dWYBu98TXrlGBjdvsDa8Gg==
Content-Length: 801
Server: Jetty(8.1.2.v20120308)
```
In this case, the HEAD response doesn't include any body, but your table metadata is included in a header with the name of it.

## Querying

Apart of using GET requests, you can also use two more ways of querying

### Calling functions or stored procedures

```
$ curl -i -X POST -H "Content-Type: application/json" -d '[{"value":2010, "name":"year", "outParameter":false, "type":"INTEGER", "index":1},{"name":"out_total", "outParameter":true, "type":"INTEGER", "index":2}]' "http://localhost:8080/jongo/demo1/call/get_year_sales"
HTTP/1.1 200 OK
Content-Type: */*
Content-Count: 1
Content-Location: get_year_sales
Date: 2012-04-02T01:24:35.677+02:00
Content-MD5: mRewGokS4XVF1CEgjXrPxg==
Content-Length: 44
```
```json
{"success":true,"cells":[ {"out_total":12}]}
```
As you can see, you call your stored procedure `/jongo/demo1/call/get_year_sales` with a POST request, giving Jongo the needed parameters in the request's body:

```json
[
  {"value":2010, "name":"year", "outParameter":false, "type":"INTEGER", "index":1},
  {"name":"out_total", "outParameter":true, "type":"INTEGER", "index":2}
]
```

### Jongo also provides JongoDynamicFinders inspired by Grails

```
$ curl -i -X GET -H "Accept: application/json" "http://localhost:8080/jongo/demo1/user/dynamic/findAllByIdBetween?args=0&args=4"
HTTP/1.1 200 OK
Content-Type: application/json
Date: 2012-03-26T23:34:50.145+02:00
Content-Count: 4
Content-Location: user
Content-MD5: 3mTUmmN9ody68WB6/YXz7Q==
Content-Length: 507
Server: Jetty(8.1.2.v20120308)
```

Here we are telling Jongo to find all `demo1.user` entities where id is between 0 and 4

```json
{
   "success":true,
   "cells":[
      {
         "birthday":"1982-12-13",
         "credit":"32.50",
         "lastupdate":"2012-03-27T14:34:50.144+02:00",
         "name":"foo",
         "age":30,
         "id":0
      },
      ...
      {
         "birthday":"2012-03-27",
         "credit":"45.00",
         "lastupdate":"2012-03-27T14:34:50.145+02:00",
         "name":"my foo user",
         "age":30,
         "id":3
      }
   ]
}
```
