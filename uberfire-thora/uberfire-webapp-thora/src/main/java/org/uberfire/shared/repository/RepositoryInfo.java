package org.uberfire.shared.repository;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class RepositoryInfo {

    private String owner;
    private String name;
    private String description;
    private String uri;
    private String origin;
    private Path root;

    public RepositoryInfo() {
    }

    public RepositoryInfo( String owner,
                           String name,
                           String description,
                           String uri,
                           Path root ) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.root = root;
    }

    public RepositoryInfo( String owner,
                           String name,
                           String description,
                           String uri,
                           String origin,
                           Path root ) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.origin = origin;
        this.root = root;
    }

    public RepositoryInfo( String owner,
                           String name,
                           String description,
                           String uri ) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.origin = null;
    }

    public RepositoryInfo( String owner,
                           String name,
                           String description,
                           String uri,
                           String origin ) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.origin = origin;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }

    public Path getRoot() {
        return root;
    }

    public String getOrigin() {
        return origin;
    }
}
