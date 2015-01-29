package org.kie.workbench.common.screens.social.hp.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.uberfire.social.activities.server.SocialConfiguration;

@ApplicationScoped
@Service
public class SocialConfigurationServiceImpl implements  SocialConfigurationService {

    @Inject
    private SocialConfiguration socialConfiguration;

    @Override
    public Boolean isSocialEnable() {
        return socialConfiguration.isSocialEnable();
    }
}
