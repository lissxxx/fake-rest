# Fake-Rest

Create REST API using configuration.

With it, you can test your code where need external services.

API uses mostly json data.

### Getting started

Compile project
```
mvn clean package
```
Create .yml configuration or use this one
[application.yml](core/src/main/resources/application.yml)

Start application
```
java -jar FakeRest.jar
```
If using .yml from example - then send request
```
curl http://localhost:8450/test2/1
```
Should return 
```
{"id":"1","data":"value"}
```

### Configuration
#### Controllers configuration
Controllers configuration contains parameters:
- URI - base uri with or without pattern
- Method - GET, POST, PUT, DELETE
- Answer - need for init data in controller collection or specify static returning data
- GenerateId - flag for POST method. Should generate id or should use from body request
- GenerateIdPatterns - UUID, Sequence. Uses if generateId is true. Default: sequence.
- DelayMs - time to delay answer

Controller works on 2 modes:
- Static - return specified data from 'answer' or request body what you send.
- Collection - services with collection, where you can make CRUD operations

Example static configuration:

|Method                |Uri       |Real mapped Uri|Answer        |Function                                             |
|----------------------|----------|-------------- |--------------|-----------------------------------------------------|
|GET, POST, PUT, DELETE|/test     |/test          |example       |Return 'example'                                     |
|GET, POST, PUT, DELETE|/test     |/test          |              |Return data from body or Bad request if body is empty|
|GET                   |/test     |/test          |              |Return empty answer                                  |

For collection mode should configure uri with id in brackets '{', '}'. Id param can have any name.

Collection controllers can be specified with multiple ids.

For connect controllers collections with each other - should use general base uri

Example collection configuration:

|Method|Uri       |Real mapped Uri|Answer        |GenerateId |GenerateIdPatterns|Function                                             |
|------|----------|-------------- |--------------|-----------|------------------|-----------------------------------------------------|
|GET   |/test/{id}|/test/         |{"id":"1"}    |           |                  |Added json to collection. Return all records         |
|      |          |/test/{id}     |              |           |                  |Return record by id                                  |
|POST  |/test/{id}|/test/         |{"id":"1"}    |           |                  |Added json to collection. Create new records. Expected id in body json|
|POST  |/test/{id}|/test/         |              |true       |                  |Create new records. Id "id" generate by sequence     |
|POST  |/test/{id}|/test/         |              |true       |id:UUID           |Create new records. Id "id" generate by uuid         |
|PUT   |/test/{id}|/test/{id}     |              |           |                  |Update record. Rewrite id in body json from url value|
|DELETE|/test/{id}|/test/{id}     |              |           |                  |Update record. Rewrite id in body json from url value|

See real example yml configuration
[application.yml](core/src/main/resources/application.yml).

### Configuration routers
Routers configuration contains parameters:
- URI - base uri with or without pattern
- Method - GET, POST, PUT, DELETE
- toUrl - uri or url where need to route request

If you configure method like GET, router will consume GET and send request to toUrl by same method.

Example routers configuration:

|Method|Uri       |ToUrl    |Function                                         |
|------|----------|---------|-------------------------------------------------|
|GET   |/test/{id}|/test    |Resend from uri with pattern to static controller|
