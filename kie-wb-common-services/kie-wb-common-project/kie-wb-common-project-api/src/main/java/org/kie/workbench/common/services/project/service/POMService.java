package org.kie.workbench.common.services.project.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.project.service.model.POM;
import org.kie.workbench.common.services.shared.file.SupportsRead;
import org.kie.workbench.common.services.shared.file.SupportsUpdate;
import org.uberfire.backend.vfs.Path;

@Remote
public interface POMService extends SupportsRead<POM>,
                                    SupportsUpdate<POM> {

    Path create(final Path projectRoot, final String baseURL, POM pom);

}
