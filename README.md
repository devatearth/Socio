# Socio [Spring_MVC || Swagger || Hibernate || Junit || Mokito || Postgres ] - Web Service :maple_leaf:
Developing REST API endpoints of various functionalities required for a website (similar to Quora). In order to observe the functionality of the endpoints, you will use the Swagger user interface and store the data in the PostgreSQL database. Also, the project has to be implemented using Java Persistence API (JPA).

The Projects follows the below structure:

:star: 1. quora-api

:one: config - This directory must consist of all the required configuration files of the project (if any). 

:two: controller - This directory must consist of all the controller classes required for the project.

:three: exception - This directory must consist of the exception handlers for all the exceptions. 

:four: endpoints - This directory consists of the JSON files which are used to generate the Request and Response models.

:five: test - This directory consists of tests for all the controller classes.  

:star: 2. quora-db

:one: config - This directory consists of the database properties and environment properties for local development.

:two: sql - This directory consists of all the SQL queries to create database schema tables.
 

:star: 3. quora-service

:one: business - This directory must consist of all the implementations of the business logic of the application.

:two: dao - This directory allows us to isolate the application/business layer from the persistence layer and must consist of the implementation of all the data access object classes.

:three:entity - This directory must consist of all the entity classes related to the project to map these class objects with the database. 

:four: exception - This directory consists of all the exceptions related to the project.
