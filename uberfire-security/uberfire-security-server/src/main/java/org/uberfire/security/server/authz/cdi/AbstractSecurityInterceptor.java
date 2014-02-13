package org.uberfire.security.server.authz.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.uberfire.security.Subject;
import org.uberfire.security.annotations.RolesType;
import org.uberfire.security.annotations.SecurityTrait;
import org.uberfire.security.authz.AuthorizationException;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.authz.RuntimeResource;
import org.uberfire.security.server.cdi.AppResourcesAuthz;

public abstract class AbstractSecurityInterceptor {

    @Inject
    @AppResourcesAuthz
    private AuthorizationManager authzManager;

    @Inject
    @SessionScoped
    private Subject subject;

    @AroundInvoke
    public Object interceptInvoke( final InvocationContext ctx ) throws Exception {

        final Method method = ctx.getMethod();

        final RuntimeResource resource = new RuntimeResource() {

            List<String> roles = null;
            List<String> traits = null;

            @Override
            public String getSignatureId() {
                return method.toString();
            }

            @Override
            public Collection<String> getRoles() {
                if ( roles == null ) {
                    build();
                }
                return roles;
            }

            @Override
            public Collection<String> getTraits() {
                if ( traits == null ) {
                    build();
                }
                return traits;
            }

            private synchronized void build() {
                this.roles = new ArrayList<String>();
                this.traits = new ArrayList<String>();

                final List<Annotation[]> availableAnnotations = new ArrayList<Annotation[]>( 2 );
                availableAnnotations.add( method.getAnnotations() );
                availableAnnotations.add( method.getDeclaringClass().getAnnotations() );

                for ( final Annotation[] annotations : availableAnnotations ) {
                    for ( final Annotation annotation : annotations ) {
                        if ( annotation.annotationType().getAnnotation( RolesType.class ) != null ) {
                            for ( final Method annotationMethod : annotation.getClass().getDeclaredMethods() ) {
                                final String paramName = annotationMethod.getName().intern();
                                if ( paramName.equals( "value" ) ) {
                                    try {
                                        if ( annotationMethod.getReturnType().isArray() ) {
                                            final Object[] params = (Object[]) annotationMethod.invoke( annotation );
                                            for ( final Object param : params ) {
                                                roles.add( param.toString() );
                                            }
                                        } else {
                                            final Object param = annotationMethod.invoke( annotation );
                                            roles.add( param.toString() );
                                        }
                                    } catch ( Exception e ) {
                                    }
                                    break;
                                }
                            }
                        } else if ( annotation.annotationType().getAnnotation( SecurityTrait.class ) != null ) {
                            traits.add( annotation.annotationType().getName() );
                        }
                    }
                }
            }
        };

        if ( !authzManager.authorize( resource, subject ) ) {
            throw new AuthorizationException( "Invalid credentials." );
        }

        return ctx.proceed();
    }

}

