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
import javax.inject.Inject;
import javax.inject.Named;

import org.appformer.maven.support.PomModel;
import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.model.Module;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
@Named("LRUPomModelCache")
public class LRUPomModelCache
        extends LRUCache<Module, PomModel> {

    private KieModuleService moduleService;

    public LRUPomModelCache() {
        //CDI proxy
    }

    @Inject
    public LRUPomModelCache(final KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    public void invalidateProjectCache(@Observes final InvalidateDMOModuleCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        final Path resourcePath = event.getResourcePath();
        final KieModule module = moduleService.resolveModule(resourcePath);
        if (module != null) {
            invalidateCache(module);
        }
    }
}
