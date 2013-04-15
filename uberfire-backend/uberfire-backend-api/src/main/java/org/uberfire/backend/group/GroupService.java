package org.uberfire.backend.group;

import java.util.Collection;

import org.uberfire.backend.repositories.Repository;

public interface GroupService {

    Group getGroup(final String name);

    Collection<Group> getGroups();

    void createGroup(final String name, final String owner);

    void createGroup(final String name, final String owner, final Collection<Repository> repositories);

    void addRepository(Group group, final Repository repository);

    void removeRepository(Group group, final Repository repository);
}
