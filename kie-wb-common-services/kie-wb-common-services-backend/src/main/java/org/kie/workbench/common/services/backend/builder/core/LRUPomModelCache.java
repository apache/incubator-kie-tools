/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.builder.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

import org.appformer.maven.support.PomModel;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.builder.ObservablePOMFile;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.model.Project;
import org.kie.soup.commons.validation.PortablePreconditions;

@ApplicationScoped
@Named("LRUPomModelCache")
public class LRUPomModelCache extends LRUCache<Project, PomModel> {

    private ObservablePOMFile observablePOMFile;

    public LRUPomModelCache() {
        observablePOMFile = new ObservablePOMFile();
    }

    public synchronized void invalidateProjectCache(@Observes final InvalidateDMOProjectCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);

        if (event.getResourcePath() != null
                && event.getProject() != null
                && observablePOMFile.accept(event.getResourcePath().getFileName())) {
            invalidateCache(event.getProject());
        }
    }
}
