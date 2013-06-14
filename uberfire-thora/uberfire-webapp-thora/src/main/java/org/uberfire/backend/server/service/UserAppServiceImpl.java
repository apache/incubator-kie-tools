package org.uberfire.backend.server.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.backend.server.UserRegistry;
import org.uberfire.shared.repository.RepositoryInfo;
import org.uberfire.shared.user.UserAppService;
import org.uberfire.shared.user.UserProfileModel;

@Service
@ApplicationScoped
public class UserAppServiceImpl implements UserAppService {

    @Inject
    private RepositoryAppServiceImpl repositoryAppService;

    @Inject
    private UserActionsService userActions;

    @Override
    public UserProfileModel getUserProfile( final String userName ) {
        return new UserProfileModel( UserRegistry.getFullName( userName ), UserRegistry.getEmail( userName ), UserRegistry.getWebsite( userName ), new Date().toLocaleString(), repositoryAppService.getUserRepositories( userName ).size(), userActions.getLastContribs( userName ) );
    }

}
