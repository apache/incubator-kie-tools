package org.uberfire.shared.browser;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class ResultListContent {

    private List<Path> breadcrumbs = new ArrayList<Path>();
    private List<FileContent> files = new ArrayList<FileContent>();

    public ResultListContent() {
    }

    public ResultListContent( List<Path> breadcrumbs,
                              List<FileContent> files ) {
        this.breadcrumbs = breadcrumbs;
        this.files = files;
    }

    public List<Path> getBreadcrumbs() {
        return breadcrumbs;
    }

    public List<FileContent> getFiles() {
        return files;
    }
}
