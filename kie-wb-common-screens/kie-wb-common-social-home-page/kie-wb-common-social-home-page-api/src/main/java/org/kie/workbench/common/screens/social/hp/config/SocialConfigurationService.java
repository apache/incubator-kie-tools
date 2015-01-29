package org.kie.workbench.common.screens.social.hp.config;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface SocialConfigurationService {

    public Boolean isSocialEnable();

}
