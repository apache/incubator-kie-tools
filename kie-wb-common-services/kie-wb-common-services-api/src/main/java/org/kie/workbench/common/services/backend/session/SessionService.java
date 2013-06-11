package org.kie.workbench.common.services.backend.session;

import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.shared.project.Project;
import org.uberfire.backend.vfs.Path;

public interface SessionService {

    KieSession newKieSession(final Project project,
                             final String sessionName);

}
