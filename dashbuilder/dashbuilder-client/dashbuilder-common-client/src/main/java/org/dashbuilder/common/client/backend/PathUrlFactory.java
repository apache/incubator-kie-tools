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
    private static final String DOWNLOAD_SERVLET_URL = "defaulteditor/download";

    private String getUrl(String context, String path) {
        return new StringBuilder(GWT.getModuleBaseURL())
            .append(context)
            .append("?")
            .append("path")
            .append("=")
            .append(URL.encode(path))
            .toString();
    }

    /**
     * <p>Returns the download URL for a given file provided by a servlet method.</p>
     * @param path The path of the file.
     */
    public String getDownloadFileUrl(final Path path) {
        return getUrl(DOWNLOAD_SERVLET_URL, path.toURI());
    }

    /**
     * <p>Returns the download URL for a given file provided by a servlet method.</p>
     * @param path The path of the file.
     */
    public String getDownloadFileUrl(final String path) {
        return getUrl(DOWNLOAD_SERVLET_URL, path);
    }

    /**
     * <p>Returns the upload URL for a given file provided by a servlet method.</p>
     * @param path The path of the file.
     */
    public String getUploadFileUrl(String path) {
        return getUrl(UPLOAD_SERVLET_URL, path);
    }
}
