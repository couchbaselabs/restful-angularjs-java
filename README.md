# Full Stack Java, AngularJS and Couchbase Example

A very basic example of a full stack application that makes use of Couchbase Server's N1QL query language.

The full stack application separates Java and Couchbase Server into the back-end and leaves AngularJS, HTML, and CSS as the front-end that requests data from the back-end and presents it to the user.

## Prerequisites

There are not many prerequisites required to build and run this project, but you'll need the following:

* Maven
* Java 1.7+
* Couchbase Server 4.1+

## Installation & Configuration

Certain configuration in both the application and the database must be done before this project is usable.

### Application

Checkout the latest master branch from GitHub and navigate into it using your Terminal (Mac & Linux) or Command Prompt (Windows).

### Database

This project requires Couchbase 4.1 or higher in order to function because it makes use of the N1QL query language.  With Couchbase Server installed, create a new bucket called **restful-sample** or whatever you've named it in your **src/main/resources/application.properties** file.

We're not done yet.  In order to use N1QL queries in your application you must create a primary index on your bucket.  This can be done by using the Couchbase Query Client (CBQ).

On Mac, run the following to launch CBQ:

```
./Applications/Couchbase Server.app/Contents/Resources/couchbase-core/bin/cbq
```

On Windows, run the following to launch CBQ:

```
C:/Program Files/Couchbase/Server/bin/cbq.exe
```

With CBQ running, create an index like so:

```
CREATE PRIMARY INDEX ON `restful-sample` USING GSI;
```

Your database is now ready for use.

## Testing

With all dependencies installed and Couchbase Server configured, run the following from your Command Prompt or Terminal:

```
mvn spring-boot:run
```

Now when you visit **http://localhost:8080** from your web browser you will be able to use the application.

## Resources

Couchbase - [http://www.couchbase.com](http://www.couchbase.com)

AngularJS - [http://www.angularjs.org](http://www.angularjs.org)
