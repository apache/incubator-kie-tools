package org.kie.workbench.common.services.datamodel.backend.server;

import org.guvnor.common.services.backend.file.DotFileFilter;
import org.kie.commons.java.nio.file.Path;

/**
 * A filter to ensure only Model related resources are included
 */
public class ModelFilter extends DotFileFilter {

    private static final String[] PATTERNS = new String[]{ "pom.xml", ".model.drl", ".drl" };

    @Override
    public boolean accept( final Path path ) {
        boolean accept = super.accept( path );
        if ( !accept ) {
            return accept;
        }

        final String uri = path.toUri().toString();
        for ( final String pattern : PATTERNS ) {
            if ( uri.substring( uri.length() - pattern.length() ).equals( pattern ) ) {
                return true;
            }
        }
        return false;
    }
}
