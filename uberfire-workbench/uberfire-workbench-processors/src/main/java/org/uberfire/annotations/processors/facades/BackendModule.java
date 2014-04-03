package org.uberfire.annotations.processors.facades;

/**
 * A collection of type names in the UberFire Backend API module.
 * Due to a bug in Eclipse annotation processor dependencies, we refer to all UberFire type names using Strings,
 * Elements, and TypeMirrors. We cannot refer to the annotation types as types themselves.
 */
public class BackendModule {

    private BackendModule(){};

    public static final String path = "org.uberfire.backend.vfs.Path";

    public static String getPathClass() {
        return path;
    }
}