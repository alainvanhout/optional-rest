# Optional REST

While delving into microservices and fullfledged (level 3) REST, I formed some ideas about quick and self-documenting REST via the OPTIONS verb, about an alternative approach to typical MVC controllers, and about truly logicless templates. Trying out those ideas is the only purpose of this repository, so view it the sandbox that it is. If I like what I end up with, I might move it beyond that, but we're not there yet.

## `optional-rest-core`


### Defining scopes

As a bare minimum a scope class must implement the marker interface `RootScopeContainer`.

```Java
public class ExampleScope implements RootScopeContainer {

   ...
   
}
```

#### Scope methods

Scope methods do the actual work in a scope. To make a method catch and handle all requests that reach said scope, annotate it with `@Handle`. 

```Java
@Handle
private Response exampleMethod(){
  return new Response().responseCode(200);
}
```

Specifying that you want the scope to only handle GET requests can be via `@Handle(methods = HttpMethod.GET)` or, for convenience, via `@Get`. For multiple http methods, use `@Handle(methods = {HttpMethod.GET, HttpMethod.POST})` or `@Get @Post`.

#### Instance scopes

#### Relative scopes

### Scope manager setup

To get things working, you need to define a `ScopeManager` which, as the name suggests, manages the whole `optional-rest`flow. To function properly, the scopeManager requires:
* a scope registry, which is responsible for handling scope identities
* a collection of scope containers
* a collection of parameter mapper providers
* a collection of response converter providers

```Java
ScopeManager scopeManager = new ScopeManager();

scopeManager.scopeRegistry(new ScopeRegistry())
      .containers(containers)
      .optionsRequestHandler(new OptionsRequestHandlerImpl())
      .parameterMapperProviders(parameterMapperProviders)
      .responseConverterProviders(responseConverterProviders);
```

The scope manager can then be handed requests, which it will convert into responses.

```Java
Request request = ...;
Response response = scopeManager.follow(rootScopeContainer, request);
```

Requests can be formed by adding  

```Java
Request request = Request.fromQuery(requestUri);
request.getParameters().addAll(httpRequest.getParameterMap());
request.getHeaders().addAll(headers);
request.reader(httpRequest.getReader());
```

## `optional-rest-spring`

This module provides Spring interation, meaning that bean classes which implement `ScopeContainer` will automatically be processed, as will beans implementing `ParameterMapperProvider` and `ResponseConverterProvider`. A `scopeManager` bean and `scopeRegistry` bean will automatically be created, as will a `rootController` which relays all requests and responses for `@RequestMapping("*/**")`.
