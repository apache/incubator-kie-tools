/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.POM;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.backend.builder.JavaSourceFilter;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.io.IOService;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.StreamSupport.stream;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Module, Builder> {

    private final List<BuildValidationHelper> buildValidationHelpers = new ArrayList<>();
    private final List<Predicate<String>> classFilters = new ArrayList<>();
    private IOService ioService;
    private KieModuleService moduleService;
    private ProjectImportsService importsService;
    private Instance<BuildValidationHelper> buildValidationHelperBeans;
    private LRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache;
    private LRUPomModelCache pomModelCache;
    private PackageNameWhiteListServiceImpl packageNameWhiteListService;
    private Instance<Predicate<String>> classFilterBeans;

    public LRUBuilderCache() {
        //CDI proxy
    }

    @Inject
    public LRUBuilderCache(final @Named("ioStrategy") IOService ioService,
                           final KieModuleService moduleService,
                           final ProjectImportsService importsService,
                           final @Any Instance<BuildValidationHelper> buildValidationHelperBeans,
                           final @Named("LRUModuleDependenciesClassLoaderCache") LRUModuleDependenciesClassLoaderCache dependenciesClassLoaderCache,
                           final @Named("LRUPomModelCache") LRUPomModelCache pomModelCache,
                           final PackageNameWhiteListService packageNameWhiteListService,
                           final @JavaSourceFilter Instance<Predicate<String>> classFilterBeans) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.importsService = importsService;
        this.buildValidationHelperBeans = buildValidationHelperBeans;
        this.dependenciesClassLoaderCache = dependenciesClassLoaderCache;
        this.pomModelCache = pomModelCache;
        this.packageNameWhiteListService = (PackageNameWhiteListServiceImpl) packageNameWhiteListService;
        this.classFilterBeans = classFilterBeans;
    }

    @PostConstruct
    public void loadInstances() {
        stream(buildValidationHelperBeans.spliterator(),
               false).collect(toCollection(() -> buildValidationHelpers));
        stream(classFilterBeans.spliterator(),
               false).collect(toCollection(() -> classFilters));
    }

    @PreDestroy
    public void destroyInstances() {
        buildValidationHelpers.forEach(helper -> buildValidationHelperBeans.destroy(helper));
        classFilters.forEach(filter -> classFilterBeans.destroy(filter));
    }

    public void invalidateProjectCache(@Observes final InvalidateDMOModuleCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        final Module project = event.getModule();

        //If resource was not within a Module there's nothing to invalidate
        if (project != null) {
            invalidateCache(project);
        }
    }

    public Builder assertBuilder(POM pom)
            throws NoBuilderFoundException {
        for (Module project : getKeys()) {
            if (project.getPom().getGav().equals(pom.getGav())) {
                return makeBuilder(project);
            }
        }
        throw new NoBuilderFoundException();
    }

    public Builder assertBuilder(final Module module) {
        return makeBuilder(module);
    }

    public Builder getBuilder(final Module module) {
        return getEntry(module);
    }

    private Builder makeBuilder(final Module module) {
        Builder builder = getEntry(module);
        if (builder == null) {
            builder = new Builder(module,
                                  ioService,
                                  moduleService,
                                  importsService,
                                  buildValidationHelpers,
                                  dependenciesClassLoaderCache,
                                  pomModelCache,
                                  packageNameWhiteListService,
                                  createSingleClassFilterPredicate());

            setEntry(module,
                     builder);
        }
        return builder;
    }

    private Predicate<String> createSingleClassFilterPredicate() {
        return classFilters.stream().reduce(o -> true,
                                            (p1, p2) -> p1.and(p2));
    }
}
