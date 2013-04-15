package org.uberfire.backend.group;

import java.util.Collection;

import org.uberfire.backend.repositories.Repository;

public interface Group {

    String getName();

    String getOwner();

    Collection<Repository> getRepositories();

}
