package org.uberfire.backend.server.repositories;

import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.config.ConfigGroup;

public interface RepositoryFactoryHelper {

    boolean accept( ConfigGroup repoConfig );

    Repository newRepository( ConfigGroup repoConfig );
}
