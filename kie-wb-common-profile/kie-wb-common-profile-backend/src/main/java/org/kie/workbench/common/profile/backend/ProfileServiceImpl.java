package org.kie.workbench.common.profile.backend;

import static org.kie.workbench.common.profile.api.preferences.ProfileDefinitions.FORCE_PREFIX;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.profile.api.preferences.ProfileService;

@Service
@ApplicationScoped
public class ProfileServiceImpl implements ProfileService {
    
    private boolean force;

    public ProfileServiceImpl() {
    }

    @PostConstruct
    public void checkIfIsForcedProfile() {
        String profile = System.getProperty("org.kie.workbench.profile");
        force = Objects.nonNull(profile) && profile.startsWith(FORCE_PREFIX);
    }
    
    @Override
    public boolean isForce() {
        return force;
    }

}
