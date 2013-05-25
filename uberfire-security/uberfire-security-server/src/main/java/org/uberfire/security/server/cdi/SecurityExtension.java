package org.uberfire.security.server.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.uberfire.security.impl.IdentityImpl;

public class SecurityExtension implements Extension {

    public <T> void processAnnotatedType( @Observes ProcessAnnotatedType<T> pat ) {
        if ( pat.getAnnotatedType().getJavaClass().equals( IdentityImpl.class ) ) {
            pat.veto();
        }
    }
}
