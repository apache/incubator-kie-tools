package org.uberfire.backend.server.group;

import java.util.ArrayList;
import java.util.Collection;

import org.uberfire.backend.group.Group;
import org.uberfire.backend.repositories.Repository;

public class GroupImpl implements Group {

    private String name;
    private String owner;

    private Collection<Repository> repositories = new ArrayList<Repository>();

    public GroupImpl(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public Collection<Repository> getRepositories() {
        return repositories;
    }

    public void addRepository(Repository repository) {
        this.repositories.add(repository);
    }
}
