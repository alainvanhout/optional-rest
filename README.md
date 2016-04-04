# Optional REST

While delving into microservices and fullfledged (level 3) REST, I formed some ideas about quick and self-documenting REST via the OPTIONS verb, about an alternative approach to typical MVC controllers, and about truly logicless templates. Trying out those ideas is the only purpose of this repository/library, so view it the sandbox that it is. If I like what I end up with, I might move it beyond that, but we're not there yet.

The following text describes the general usage of the library, regardless of whether a dependency injection system is used. The associated code can be found in the `optional-rest-core` module. The readme for [optional-rest-core](/optional-rest-core/README.md) specifically described the setup to use the library on its own, while the readme for the [optional-rest-spring](/optional-rest-core/README.md) module lists the small number of steps needed to work with the library on top of a Spring (MVC) setting. The `springboot-demo` module is the actual sandbox, which build on Spring Data with MongoDB.

## Concepts

### REST

REST (**Re**presentation **S**tate **T**ransfer) is [typically described](http://martinfowler.com/articles/richardsonMaturityModel.html) based on the 3 (or rather 4) possible levels:
* Level 0. All http requests are handled by a single endpoint. Only the content of the request body determines how it's handled
* Level 1. Different endpoints are used for different purposes (referred to as resources)
* Level 2. Http verbs/methods (GET, POST, DELETE, etc) are used for each endpoint, to specify intent
* Level 3. HATEOAS (Hypertext As The Engine Of Application State): the body of responses contain information on what actions can be taken next (e.g. where to point a POST request if we want to make a new record for a given resource)

### Optional REST

Optional REST encapsulates most of the same, but adds a focus on the hierarchical nature of endpoints:
* Different endpoints are used for different purposes (cf. REST level 1)
* Http methods specify intent (cf. REST level 2)
* Endpoints tend to for hierarchies which make intuitive sense
* The modelling behind endpoints should inherently reflect these hierarchical relationships rather than adding them as an implementation detail
* To maintain the separation between data and meta-data, and avoid needing to retrieve data to get meta-data, the endpoint information (including child hierarchies) is collected via the OPTIONS http method rather than being part of the (e.g. GET) response itself.

### Scopes

A scope equates to an http endpoint with its associated logic, including
* the type of requests it accepts (e.g. http methods, body structure, contentType header, ...)
* the type of responses it returns (body structure and mime types)
* the scopes that are relative to it

### Resource scopes

A resource scope is the most general type of scope, meaning it is not one of the more specific types of scope.

### Relative scopes

For requests such as `GET /company/staff/`) we have a root scope which forwards the request to the company scope, which in turn forwards it to the staff scope. In this case, the company scope is a relative scope of the root scope and the staff scope is a relative scope of the company scope. 

### Entity scopes

REST very often revolves around exposing a domain model. Entity scopes provide an endpoint through which a list of (all or some) entity instances can be obtainsed (via `GET example/`) and new entity instances can be created (via `POST persons/` with query parameters or a request body).

Just like resource scopes in general, entity scopes can of course also have relative scopes. Typically, this involves concrete subgroups which warrant their own endpoint (e.g. `company/staff/retired`), but it may also expose related information that does not belong to the entity endpoint itself (e.g. `company/staff/documents/`). 

### Instance scopes

Typically, entity scopes can also be used to retrieve (or PUT, POST or DELETE) entity instances via their specific unique identifier, e.g. using the ´GET /persons/535154` (where 535154 is the unique identifier). This is not done via the entity scope itself, since GET, PUT and POST already have a function for that scope. Instead, if an entity scope can't pass a given request to a specific relative scope, then it will ask its instance scope (if any) to handle it.

## Defining scopes

As a bare minimum a scope class must implement the marker interface `RootScopeContainer`.

```Java
public class ExampleScope implements RootScopeContainer {

   ...
   
}
```

## Scope methods

Scope methods do the actual work in a scope. To make a method catch and handle all requests that reach said scope, annotate it with `@Handle`. 

```Java
@Handle
private Response exampleMethod(){
  return new Response().responseCode(200);
}
```

Specifying that you want the scope to only handle GET requests can be via `@Handle(methods = HttpMethod.GET)` or, for convenience, via `@Get`. For multiple http methods, use `@Handle(methods = {HttpMethod.GET, HttpMethod.POST})` or `@Get @Post`.

### Relative scopes

@Relative
TODO

### Instance scopes

@Instance
TODO


