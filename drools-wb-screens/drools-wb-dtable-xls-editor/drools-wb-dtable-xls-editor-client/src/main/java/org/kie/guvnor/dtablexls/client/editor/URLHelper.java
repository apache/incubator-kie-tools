package org.kie.guvnor.dtablexls.client.editor;

import com.google.gwt.core.client.GWT;
import org.kie.guvnor.services.file.upload.FileManagerFields;
import org.uberfire.backend.vfs.Path;

/**
 * Utility to get the Servlet URL
 */
public class URLHelper {

    private static final String BASE = "dtablexls/file";

    public static String getServletUrl() {
        return GWT.getModuleBaseURL() + BASE;
    }

    public static String getDownloadUrl( final Path path ) {
        final StringBuilder sb = new StringBuilder( URLHelper.getServletUrl() );
        sb.append( "?" ).append( FileManagerFields.FORM_FIELD_PATH ).append( "=" ).append( path.toURI() );
        return sb.toString();
    }

}
