package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for backend module.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-backend.
 */
public class BackendModule {

    private static Class<? extends Annotation> path;

    private BackendModule(){};

    static {
        try {
            path = (Class<? extends Annotation>) Class.forName( "org.uberfire.backend.vfs.Path" );

        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( e );
        }
    }

    public static Class<? extends Annotation> getPathClass() {
        return path;
    }
}