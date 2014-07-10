package org.kie.workbench.common.services.backend.session;

import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.shared.project.KieProject;

/**
 * Provides a KSession for given project
 */
public interface SessionService {

    /**
     * Returns the default KSession for a Project
     */
    KieSession newKieSession( final KieProject project );

}
