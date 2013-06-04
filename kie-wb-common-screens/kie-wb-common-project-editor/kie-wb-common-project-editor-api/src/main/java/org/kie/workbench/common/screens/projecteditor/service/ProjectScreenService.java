package org.kie.workbench.common.screens.projecteditor.service;

import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.Path;
import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface ProjectScreenService {

    public ProjectScreenModel load(Path path);

    void save(Path pathToPomXML, ProjectScreenModel model, String comment);
}
