package org.uberfire.ext.plugin.model;

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

    public String getPreviewURI() {
        return externalURI + "?preview";
    }

    public String getExternalURI() {
        return externalURI;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Media ) ) {
            return false;
        }

        Media media = (Media) o;

        if ( externalURI != null ? !externalURI.equals( media.externalURI ) : media.externalURI != null ) {
            return false;
        }
        if ( path != null ? !path.equals( media.path ) : media.path != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = externalURI != null ? externalURI.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( path != null ? path.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
