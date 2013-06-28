package org.kie.workbench.common.services.backend.source;

import org.kie.commons.java.nio.file.Path;

/**
 * Base implementation of all SourceServices
 */
public abstract class BaseSourceService<T>
        implements SourceService<T> {

    @Override
    public boolean accepts( final Path path ) {
        final String pattern = getPattern();
        final String suffix = "." + pattern;
        final String uri = path.toUri().toString();
        return uri.substring( uri.length() - suffix.length() ).equals( suffix );
    }

}
