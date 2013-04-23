package org.uberfire.backend.server.repositories.git;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.server.repositories.EnvironmentParameters;

@Portable
public class RemoteGitRepository extends LocalGitRepository {

    public RemoteGitRepository() {
    }

    public RemoteGitRepository( final String alias ) {
        super( alias );
    }

    @Override
    public boolean isValid() {
        final Object origin = getEnvironment().get( EnvironmentParameters.ORIGIN );
        return super.isValid() && origin != null;
    }

}
