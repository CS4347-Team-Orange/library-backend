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

Before trying to build, make sure postgres isn't already running - it'll come up when you run `bash start.sh` in here.

From the project's root dir, `library-backend`, run `./gradlew bootRun`.  This will start the development server on http://localhost:8080

Once the development server is running, you can use `curl` to test it.  Examples are below.

# Using Spring

## Building a controller

Take a look at `src/main/java/edu/utdallas/cs4347/library/controller`.

I'll break down the first method of the `BorrowersController` for this example.

```
import {...} - [A]

@RestController - [B]
@CrossOrigin(origins = "*") - [C]
@RequestMapping("/api/borrower") - [D]
public class BorrowersController {

    private static final Logger log = LogManager.getLogger(BorrowersController.class); - [E]

    @Autowired - [F]
    BorrowerMapper borrowerMapper;

    @GetMapping("/") - [G]
    public LibraryResponse list() {
        LibraryResponse resp = new LibraryResponse(); - [H]
        List<Borrower> borrowers = null;
        try {
            borrowers = borrowerMapper.getAll(); - [I]
            resp.setData( borrowers );
        } catch (Exception e) {
            log.error("Exception in list()", e);
            return new LibraryResponse(123, "It's broken, captain!"); - [J]
        }
        return resp;
    }
```

###  A: Imports

You'll notice a lot of imports in the controller.  Go ahead and copy them for the other controllers, most of what you need should already be here.

###  B: @RestController

This tells Spring that this is a controller class, and to expose it on the HTTP API.

###  C: @CrossOrigin

This tells Spring to allow Cross-Origin requests from all referrers, so that the API will accept requests from a decoupled frontend.

###  D: @RequestMapping

This tells Spring where to map the controller on the HTTP API, eg here we map to `/api/borrowers`.  You'll want to make a different mapping for each controller class to avoid conflicts.  Eg, a Checkout controller should live at `/api/checkout`

###  E: Logger

This creates a `log` object available to the class for logging output.  See the try/catch block for example usage of log.

###  F: @Autowired

This tells Spring about the dependencies of this object.  Autowired fields on a class will create the dependency, or link to an existing instance of the dependency.  You should autowire dependent business-tier classes, like Mappers and Services.  You should NOT Autowire domain objects.

###  G: @GetMapping

REST APIs accept a number of verbs.  The select few we're using so far are...

* GET: Get some data
* POST: Here's some data
* PATCH: Update some object, here's the new object data
* DELETE: Delete an object

The `@GetMapping("/")` tells Spring a few things.  

First, the `GetMapping` indicates to Spring that the following method should be mapped to the HTTP GET verb.

Next, the path, `/`, tells Spring to map this method to the controller's root mapping, eg `/api/borrowers`

Effectively, this function will now run any time I run an HTTP GET to `/api/borrowers`

###  H: LibraryResponse object

This is a standard object we can use to model responses to the GUI.  It'll help the GUI team by providing a consistent interface for responses.

LibraryResponse has a code, message, and data.

* code: The error code, by default 0 indicates success.  Override this with a specific error code if something went wrong.
* message: The response message, eg "Failed to create!".  Defaults to "success"
* data: Any Java Object, we use this field to return the result of an action, eg GET /api/borrowers will return a list of borrowers, which gets plugged into the data field of the response object before being returned to the API Client.

###  I: BorrowerMapper

This project uses MyBatis as an ORM.  The `BorrowerMapper` provides an interface for functions that can be executed to query the database.  The mapper's implementation is automatically generated by MyBatis, following the XML file located in `resources/mapper`


###  J: LibraryResponse error

This one provides an example of how the LibraryResponse object can be used to return an error.


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
 --data-raw '{"cardNumber":"ID000005","address":"41260 Kedzie Terrace","phone":"(469) 213-2549","firstName":"Linda","lastName":"Queen","ssn":"906-63-3588","email":"lking4@illinois.edu","city":"Dallas","state":"TX"}' \
 -H "Content-Type: application/json"
```


## Delete a Borrower

```
curl http://localhost:8080/api/borrower/e3770212-e68a-11eb-ac38-0242ac150002 \
-X DELETE \
-H "Content-Type: application/json"
```