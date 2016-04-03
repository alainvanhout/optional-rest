# Optional REST

While delving into microservices and fullfledged (level 3) REST, I formed some ideas about quick and self-documenting REST via the OPTIONS verb, about an alternative approach to typical MVC controllers, and about truly logicless templates. Trying out those ideas is the only purpose of this repository, so view it the sandbox that it is. If I like what I end up with, I might move it beyond that, but we're not there yet.

The following text describes the general usage of the library, regardless of whether a dependency injection system is used. The associated code can be found in the `optional-rest-core` module. The readme for [optional-rest-core](/optional-rest-core/README.md) specifically described how to use it on its own, while the readme for the [optional-rest-spring](/optional-rest-core/README.md) module lists the small number of steps needed to work with the library on top of a Spring (MVC) setting.

## Concepts

### REST

TODO

### Optional REST

### Scopes

### Resource scopes

### Entity scopes

### Instance scopes

### Relative scopes

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

### Instance scopes

TODO

### Relative scopes

TODO

