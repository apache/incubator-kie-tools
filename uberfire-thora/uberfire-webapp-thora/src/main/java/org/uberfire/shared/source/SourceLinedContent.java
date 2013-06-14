package org.uberfire.shared.source;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class SourceLinedContent {

    private List<String> content;
    private List<String> breadcrumb;
    private Path dirPath;

    public SourceLinedContent() {
    }

    public SourceLinedContent( List<String> content,
                               List<String> breadcrumb,
                               Path dirPath ) {
        this.content = content;
        this.breadcrumb = breadcrumb;
        this.dirPath = dirPath;
    }

    public List<String> getContent() {
        return content;
    }

    public List<String> getBreadcrumb() {
        return breadcrumb;
    }

    public Path getDirPath() {
        return dirPath;
    }
}
