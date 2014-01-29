package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;

import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for security module.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-security.
 */
public class SecurityModule {

    private final Class<? extends Annotation> rolesType;
    private final Class<? extends Annotation> securityTrait;

    public SecurityModule() throws GenerationException {

        try {
            rolesType = (Class<? extends Annotation>) Class.forName( "org.uberfire.security.annotations.RolesType" );
            securityTrait = (Class<? extends Annotation>) Class.forName( "org.uberfire.security.annotations.SecurityTrait" );

        } catch ( ClassNotFoundException e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
    }

    public Class<? extends Annotation> getSecurityTraitClass() {
        return securityTrait;
    }

    public Class<? extends Annotation> getRolesTypeClass() {
        return rolesType;
    }

}
