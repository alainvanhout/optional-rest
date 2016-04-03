# optional-rest-core

## Scope manager setup

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

## Handing over request

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
