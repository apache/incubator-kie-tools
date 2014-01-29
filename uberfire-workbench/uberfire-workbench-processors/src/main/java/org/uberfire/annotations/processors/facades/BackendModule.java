package org.uberfire.annotations.processors.facades;

import java.lang.annotation.Annotation;

import org.uberfire.annotations.processors.exceptions.GenerationException;

/**
 * A facade for backend module.
 * Due to a bug in Eclipse annotation processor and inner projects dependencies,
 * this class handle with the dependencies of uberfire-backend.
 */
public class BackendModule {


    private final Class<? extends Annotation> path;

    public BackendModule() throws GenerationException {

        try {
            path = (Class<? extends Annotation>) Class.forName( "org.uberfire.backend.vfs.Path" );

        } catch ( ClassNotFoundException e ) {
            throw new GenerationException( e.getMessage(), e.getCause() );
        }
    }

    public Class<? extends Annotation> getPathClass() {
        return path;
    }
}