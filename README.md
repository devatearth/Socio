# Quora_a_socialApp [Spring_MVC || Swagger || Hibernate || Junit || Mokito] - Web Service
Developing REST API endpoints of various functionalities required for a website (similar to Quora) from scratch. In order to observe the functionality of the endpoints, you will use the Swagger user interface and store the data in the PostgreSQL database. Also, the project has to be implemented using Java Persistence API (JPA).

The Projects follows the below structure:

:star: 1. quora-api

config - This directory must consist of all the required configuration files of the project (if any). We have already provided swagger config file in the stub.

controller - This directory must consist of all the controller classes required for the project (the list of required controllers along with the API endpoints 
are listed in the next segment).

exception - This directory must consist of the exception handlers for all the exceptions. You have to implement the code for exception handler for all the 
exceptions to be implemented in the project.

endpoints - This directory consists of the JSON files which are used to generate the Request and Response models.

test - This directory consists of tests for all the controller classes. You need to uncomment all the given test cases to run these test cases after implementing
the project.

 

:star: 2. quora-db

config - This directory consists of the database properties and environment properties for local development.

sql - This directory consists of all the SQL queries to create database schema tables.
 

:star: 3. quora-service

business - This directory must consist of all the implementations of the business logic of the application.

dao - This directory allows us to isolate the application/business layer from the persistence layer and must consist of the implementation of all the data access object classes.

entity - This directory must consist of all the entity classes related to the project to map these class objects with the database. You need to observe the database schema and all the constraints given in SQL files carefully to map Java objects with the database.

exception - This directory consists of all the exceptions related to the project. All the exceptions required for the project have been implemented in the stub file.
