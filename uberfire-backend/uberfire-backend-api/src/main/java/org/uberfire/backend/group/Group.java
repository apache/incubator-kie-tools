package org.uberfire.backend.group;

import java.util.Collection;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.security.authz.RuntimeResource;

public interface Group extends RuntimeResource {

    String getName();

    String getOwner();

    Collection<Repository> getRepositories();

}
