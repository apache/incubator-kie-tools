package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for security module.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-security.
 */
public class SecurityModule {

    private static Class<? extends Annotation> rolesType;
    private static Class<? extends Annotation> securityTrait;
    private static final Logger logger = LoggerFactory.getLogger( SecurityModule.class );

    private SecurityModule() {}

    static {

        try {
            rolesType = (Class<? extends Annotation>) Class.forName( "org.uberfire.security.annotations.RolesType" );
            securityTrait = (Class<? extends Annotation>) Class.forName( "org.uberfire.security.annotations.SecurityTrait" );

        } catch ( ClassNotFoundException e ) {
            logger.error( e.getMessage() );
        }
    }

    public static Class<? extends Annotation> getSecurityTraitClass() {
        return securityTrait;
    }

    public static Class<? extends Annotation> getRolesTypeClass() {
        return rolesType;
    }

}
