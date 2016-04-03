# `optional-rest-spring`

This module provides Spring interation, meaning that bean classes which implement `ScopeContainer` will automatically be picked up, as will beans implementing `ParameterMapperProvider` and `ResponseConverterProvider`. A `scopeManager` bean and `scopeRegistry` bean will automatically be created and added to the Spring context, as will a `rootController` which relays all requests and responses for `@RequestMapping("*/**")`.
