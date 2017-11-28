package org.dashbuilder.common.client.backend;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import org.uberfire.backend.vfs.Path;

/**
 * Factory for the generation of URL for accessing GIT files on backend.
 */
@ApplicationScoped
public class PathUrlFactory {

    private static final String UPLOAD_SERVLET_URL = "defaulteditor/upload";
    private static final String EXPORT_SERVLET_URL = "defaulteditor/download";

    /**
     * <p>Returns the download URL for a given file provided by a servlet method.</p>
     * @param path The path of the file.
     */
    public String getDownloadFileUrl(final Path path) {
        final StringBuilder sb = new StringBuilder(GWT.getModuleBaseURL() + EXPORT_SERVLET_URL);
        sb.append("?").append("path").append("=").append(URL.encode(path.toURI()));
        return sb.toString();
    }

    /**
     * <p>Returns the upload URL for a given file provided by a servlet method.</p>
     * @param path The path of the file.
     */
    public String getUploadFileUrl(String path) {
        final StringBuilder sb = new StringBuilder(GWT.getModuleBaseURL() + UPLOAD_SERVLET_URL);
        sb.append("?").append("path").append("=").append(URL.encode(path));
        return sb.toString();
    }
}
