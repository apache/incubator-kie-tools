package org.uberfire.client.editors.defaulteditor;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.backend.vfs.Path;

public class DefaultEditorNewFileUpload
        extends DefaultEditorFileUploadBase {

    private Path path;
    private String fileName;

    public DefaultEditorNewFileUpload() {
        super(false);
    }

    @Override
    protected Map<String, String> getParameters() {
        HashMap<String, String> parameters = new HashMap<String, String>();

        parameters.put("folder", path.toURI());
        parameters.put("fileName", fileName);

        return parameters;
    }

    public void setFolderPath(Path path) {
        this.path = path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
