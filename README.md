# jdbc-maven-plugin

I am very interested in the DRY principle and how this principle can be applied across numerous use-cases.  Specifically 
this project is in support of applying this principle within the context of relational databases.

This project is a JDBC maven plugin which allows handlers to be invoked to generate specific artifacts off of a database.
The intention is that the handlers would interrogate the database's schema and produce either a software artifact or an
artifact for human consumption.  Two projects that illustrate how to use this plugin are:
 
- [schemadoc-8-handler](https://github.com/graeme-lockley/schemadoc-8-handler): generates a schema diagram based on the underlying tables and foreign key structures.
- jsqldsl-8-handler: generates Java classes to describe the table structure and foreign key relationships to support a 
type-safe JDBC interface into the associated database.


