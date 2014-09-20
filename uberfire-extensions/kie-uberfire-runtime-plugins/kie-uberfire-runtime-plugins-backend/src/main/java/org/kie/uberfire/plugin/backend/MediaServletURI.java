package org.kie.uberfire.plugin.backend;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class MediaServletURI {

    private String uri;

    public MediaServletURI() {
    }

    public MediaServletURI( final String uri ) {
        setURI( uri );
    }

    public String getURI() {
        return uri;
    }

    public void setURI( final String uri ) {
        this.uri = checkNotEmpty( "uri", uri );
    }
}
