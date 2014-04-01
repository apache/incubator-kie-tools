package org.uberfire.security.server.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.errai.security.shared.api.identity.UserImpl;

public class SecurityExtension implements Extension {

    public <T> void processAnnotatedType( @Observes ProcessAnnotatedType<T> pat ) {
        if ( pat.getAnnotatedType().getJavaClass().equals( UserImpl.class ) ) {
            pat.veto();
        }
    }
}
