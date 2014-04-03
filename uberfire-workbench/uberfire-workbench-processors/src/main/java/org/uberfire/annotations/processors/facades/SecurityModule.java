package org.uberfire.annotations.processors.facades;


/**
 * A collection of type names in the UberFire Security module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class SecurityModule {

    private SecurityModule() {}

    public static final String rolesType =  "org.uberfire.security.annotations.RolesType" ;
    public static final String securityTrait =  "org.uberfire.security.annotations.SecurityTrait" ;

    public static String getSecurityTraitClass() {
        return securityTrait;
    }

    public static String getRolesTypeClass() {
        return rolesType;
    }

}
