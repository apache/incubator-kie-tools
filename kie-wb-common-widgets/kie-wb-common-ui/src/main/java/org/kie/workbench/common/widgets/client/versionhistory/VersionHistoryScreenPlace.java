package org.kie.workbench.common.widgets.client.versionhistory;

import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

public class VersionHistoryScreenPlace
        extends DefaultPlaceRequest {

    public final static String URI = "uri";
    public final static String FILENAME = "filename";
    public final static String VERSION = "version";

    public VersionHistoryScreenPlace(ObservablePath pathToFile, String filename, String version) {
        super("versionHistoryScreen");

        addParameter(URI, pathToFile.toURI());
        addParameter(FILENAME, filename);
        addParameter(VERSION, version);
    }
}
