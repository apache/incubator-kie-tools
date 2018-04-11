/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.project.backend.workitem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
@Specializes
public class WorkItemDefinitionProjectResources extends WorkItemDefinitionResources {

    // CDI proxy.
    protected WorkItemDefinitionProjectResources() {
        this(null);
    }

    @Inject
    public WorkItemDefinitionProjectResources(final @Named("ioStrategy") IOService ioService) {
        super(ioService);
    }

    // TODO: getPackageMainResourcesPath().toUri() -> default://master@myteam/rrr/src/main/resources/com/myteam/rrr

    @Override
    public Path resolveResourcesPath(final Metadata metadata) {
        final org.uberfire.backend.vfs.Path path = ((ProjectMetadata) metadata).getProjectPackage().getPackageMainResourcesPath();
        if (null != path) {
            return getIoService().get(path.toURI());
        }
        return super.resolveResourcesPath(metadata);
    }
}
