package org.kie.workbench.common.profile.api.preferences;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface ProfileService {
    
    public boolean isForce();

}
