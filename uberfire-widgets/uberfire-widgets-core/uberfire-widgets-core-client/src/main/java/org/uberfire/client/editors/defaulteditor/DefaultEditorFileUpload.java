package org.uberfire.client.editors.defaulteditor;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.resources.i18n.CoreConstants;

public class DefaultEditorFileUpload
        extends DefaultEditorFileUploadBase {

    private Path path;

    @Override
    protected Map<String, String> getParameters() {
        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("path", path.toURI());

        return parameters;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void download() {

        Window.open(getFileDownloadURL(),
                CoreConstants.INSTANCE.Downloading(),
                "resizable=no,scrollbars=yes,status=no");
    }

    private String getFileDownloadURL() {
        return GWT.getModuleBaseURL() + "defaulteditor/download?path=" + path.toURI();
    }
}
