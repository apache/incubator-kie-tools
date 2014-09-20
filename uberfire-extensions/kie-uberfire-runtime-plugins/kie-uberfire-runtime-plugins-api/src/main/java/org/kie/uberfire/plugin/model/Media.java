package org.kie.uberfire.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class Media {

    private String externalURI;
    private Path path;

    public Media() {
    }

    public Media( final String externalURI,
                  final Path path ) {
        this.externalURI = externalURI;
        this.path = path;
    }

    public String getExternalURI() {
        return externalURI;
    }

    public Path getPath() {
        return path;
    }
}
