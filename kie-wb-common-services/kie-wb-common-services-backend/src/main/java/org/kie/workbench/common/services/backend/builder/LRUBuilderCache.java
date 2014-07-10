/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<KieProject, Builder> {

    @Inject
    private POMService pomService;

    @Inject
    private ProjectService projectService;

    @Inject
    private ProjectImportsService importsService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Any
    private Instance<BuildValidationHelper> anyValidators;

    private final List<BuildValidationHelper> validators = new ArrayList<BuildValidationHelper>();

    @PostConstruct
    public void setupValidators() {
        final Iterator<BuildValidationHelper> itr = anyValidators.iterator();
        while (itr.hasNext()) {
            validators.add(itr.next());
        }
    }

    public synchronized void invalidateProjectCache(@Observes final InvalidateDMOProjectCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                event);
        final Project project = event.getProject();

        //If resource was not within a Project there's nothing to invalidate
        if (project != null && project instanceof KieProject) {
            invalidateCache((KieProject) project);
        }
    }

    public synchronized Builder assertBuilder(final KieProject project) {
        Builder builder = getEntry(project);
        if (builder == null) {
            final Path pathToPom = project.getPomXMLPath();
            final POM pom = pomService.load(pathToPom);
            builder = new Builder(Paths.convert(project.getRootPath()),
                    pom.getGav(),
                    ioService,
                    projectService,
                    importsService,
                    validators);

            setEntry(project,
                    builder);
        }
        return builder;
    }

}
