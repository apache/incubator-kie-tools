/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.datamodel.backend.server.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.vfs.Path;

/**
 * A simple LRU cache for Module DataModelOracles
 */
@ApplicationScoped
@Named("ModuleDataModelOracleCache")
public class LRUModuleDataModelOracleCache
        extends LRUCache<KieModule, ModuleDataModelOracle> {

    private ModuleDataModelOracleBuilderProvider builderProvider;
    private KieModuleService moduleService;
    private BuildInfoService buildInfoService;

    public LRUModuleDataModelOracleCache() {
    }

    @Inject
    public LRUModuleDataModelOracleCache(final ModuleDataModelOracleBuilderProvider builderProvider,
                                         final KieModuleService moduleService,
                                         final BuildInfoService buildInfoService) {
        this.builderProvider = builderProvider;
        this.moduleService = moduleService;
        this.buildInfoService = buildInfoService;
    }

    public void invalidateModuleCache(@Observes final InvalidateDMOModuleCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        final Path resourcePath = event.getResourcePath();
        final KieModule module = moduleService.resolveModule(resourcePath);

        //If resource was not within a Module there's nothing to invalidate
        if (module != null) {
            invalidateCache(module);
        }
    }

    //Check the ModuleOracle for the Module has been created, otherwise create one!
    public ModuleDataModelOracle assertModuleDataModelOracle(final KieModule module) {
        ModuleDataModelOracle moduleOracle = getEntry(module);
        if (moduleOracle == null) {
            moduleOracle = makeModuleOracle(module);
            setEntry(module,
                     moduleOracle);
        }
        return moduleOracle;
    }

    private ModuleDataModelOracle makeModuleOracle(final KieModule module) {
        return builderProvider.newBuilder(module,
                                          buildInfoService.getBuildInfo(module)).build();
    }
}