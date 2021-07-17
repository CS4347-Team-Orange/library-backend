# library-backend

This is a Spring 2.5.2 REST API, built with Java 11 and Gradle, using [Spring Initializr](https://start.spring.io/).  The project uses Spring's PostgreSQL Driver and Spring REST Framework.

It serves as the business tier of the library application.

## Environment Setup

### Install SDKMan

[SDKMan](https://sdkman.io/install) can manage and install multiple java versions for you.

```
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk version
```

This Should install sdkman, start it up, and report the sdkman verion number.

### Install Java 11

This installs Java 11.0.11, HotSpot VM, from AdoptOpenJDK.

```
sdk install java 11.0.11.hs-adpt
```

### Gradle, build & run the project

Before trying to build, be sure you start the db with `bash start.sh` in the db project.

From the project's root dir, `library-backend`, run `./gradlew bootRun`.  This will start the development server on http://localhost:8080

Once the development server is running, you can use `curl` to test it.  Examples are below.

# Using Spring

## Building a controller

## Object Modeling

Take a look at the `domain` package, it holds the classes that define our objects in the database.  There are special annotations here to make the persistence framework do it's magic.

## Persistence w/ MyBatis

[Reference Tutorial](https://www.programmersought.com/article/47604488909/)

Take a look at the `mapper` package, it holds an interface that maps domain objects to the database.  You'll need to create a new Mapper for each entity type, and then define a Mapper.xml in `resources/mapper`.

# Testing the API

## Get All Borrowers

```
curl http://localhost:8080/api/borrower/
```

## Get One Borrower

```
curl http://localhost:8080/api/borrower/${BORROWER_ID}
```


## Create a Borrower

```
curl http://localhost:8080/api/borrower/ \
 -X POST \
 --data-raw '{"name": "John Smith", "ssn": 1234567890, "address": "123 NE Street, Fairfax, VA 12345", "phone": "1234567890"}' \
 -H "Content-Type: application/json"
```

Notice how the JSON field names match exactly what's in the `Borrower.java` domain object.

## Update a Borrower

```
curl http://localhost:8080/api/borrower/ \
 -X PATCH \
 --data-raw '{"cardNumber": "e3770212-e68a-11eb-ac38-0242ac150002", "name": "John Johnson", "ssn": 1234567890, "address": "123 NE Street, Fairfax, VA 12345", "phone": "1234567890"}' \
 -H "Content-Type: application/json"
```


## Delete a Borrower

```
curl http://localhost:8080/api/borrower/e3770212-e68a-11eb-ac38-0242ac150002 \
-X DELETE \
-H "Content-Type: application/json"
```