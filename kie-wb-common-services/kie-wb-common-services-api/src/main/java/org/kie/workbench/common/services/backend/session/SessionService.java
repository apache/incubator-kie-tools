package org.kie.workbench.common.services.backend.session;

import org.guvnor.common.services.project.model.Project;
import org.kie.api.runtime.KieSession;

public interface SessionService {

    KieSession newKieSession( final Project project );

}
