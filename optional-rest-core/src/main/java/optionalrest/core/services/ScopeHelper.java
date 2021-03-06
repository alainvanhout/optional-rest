package optionalrest.core.services;

import optionalrest.core.RestException;
import optionalrest.core.annotations.requests.Handle;
import optionalrest.core.annotations.scopes.Scope;
import optionalrest.core.request.meta.HttpMethod;
import optionalrest.core.services.mapping.MethodRequestHandler;
import optionalrest.core.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ScopeHelper {

    public String retrieveScopeId(Class parentClass){
        Scope annotation = ReflectionUtils.retrieveAnnotation(parentClass, Scope.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.value())){
            return annotation.value();
        }
        return parentClass.getName();
    }

    public String retrieveScopeId(AccessibleObject accessibleObject, Class parentClass){
        Scope annotation = retrieveAnnotation(accessibleObject, parentClass, Scope.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.value())){
            return annotation.value();
        }
        return parentClass.getName();
    }

    public String retrieveRelativeScopeId(AccessibleObject accessibleObject){
        Scope annotation = ReflectionUtils.retrieveAnnotation(accessibleObject, Scope.class);
        if (annotation != null && StringUtils.isNotBlank(annotation.value())){
            return annotation.value();
        }
        if (accessibleObject instanceof Field){
            return ((Field)accessibleObject).getType().getCanonicalName();
        }
        if (accessibleObject instanceof Method){
            Class declaringClass = ((Method) accessibleObject).getDeclaringClass();
            return declaringClass.getCanonicalName() + "-" + ((Method) accessibleObject).getName();
        }
        throw new RestException("Type not supported: " + accessibleObject.getClass());
    }

    public String retrieveInstanceScopeId(AccessibleObject accessibleObject, Class container) {
        Scope annotation = ReflectionUtils.retrieveAnnotation(accessibleObject, Scope.class);
        if(annotation != null){
            return annotation.value();
        }

        String parentId = retrieveScopeId(container);
        return parentId + "-instance";
    }

    public <T> T retrieveAnnotation(AccessibleObject accessibleObject, Class parentClass, Class<T> annotationClass){
        // first try on accessible object (i.e. method or field)
        T annotation = ReflectionUtils.retrieveAnnotation(accessibleObject, annotationClass);
        if (annotation != null){
            return annotation;
        }
        // then try on parent class
        return (T) ReflectionUtils.retrieveAnnotation(parentClass, annotationClass);
    }

    public void updateSupported(MethodRequestHandler requestHandler, Handle handle) {
        requestHandler.getSupported()
                .methods(getMethods(handle))
                .contentType(getContentType(handle))
                .accept(getAccept(handle));
    }

    private String[] getAccept(Handle handle) {
        if (handle != null) {
            String[] accept = handle.accept();
            if (accept != null && accept.length > 0) {
                return accept;
            }
        }
        return new String[]{};
    }

    private String[] getContentType(Handle handle) {
        if (handle != null) {
            String[] contentType = handle.contentType();
            if (contentType != null && contentType.length > 0) {
                return contentType;
            }
        }
        return new String[]{};
    }

    private HttpMethod[] getMethods(Handle handle) {
        if (handle != null) {
            HttpMethod[] methods = handle.methods();
            if (methods != null && methods.length > 0) {
                return methods;
            }
        }
        return new HttpMethod[]{};
    }
}
