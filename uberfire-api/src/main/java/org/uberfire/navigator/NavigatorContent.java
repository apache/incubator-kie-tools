package org.uberfire.navigator;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class NavigatorContent {

    private String repoName;
    private Path root;
    private List<Path> breadcrumbs = new ArrayList<Path>();
    private List<DataContent> content = new ArrayList<DataContent>();

    public NavigatorContent() {
    }

    public NavigatorContent( final String repoName,
                             final Path root,
                             final List<Path> breadcrumbs,
                             final List<DataContent> content ) {
        this.repoName = repoName;
        this.root = root;
        this.breadcrumbs = breadcrumbs;
        this.content = content;
    }

    public List<Path> getBreadcrumbs() {
        return breadcrumbs;
    }

    public List<DataContent> getContent() {
        return content;
    }

    public Path getRoot() {
        return root;
    }

    public String getRepoName() {
        return repoName;
    }
}
