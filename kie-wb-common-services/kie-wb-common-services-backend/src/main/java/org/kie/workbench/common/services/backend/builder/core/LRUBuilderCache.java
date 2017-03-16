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
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.builder.service.BuildValidationHelper;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.kie.workbench.common.services.backend.builder.JavaSourceFilter;
import org.kie.workbench.common.services.backend.whitelist.PackageNameWhiteListServiceImpl;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.io.IOService;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.StreamSupport.stream;

/**
 * A simple LRU cache for Builders
 */
@ApplicationScoped
public class LRUBuilderCache extends LRUCache<Project, Builder > {

    private IOService ioService;

    private KieProjectService projectService;

    private ProjectImportsService importsService;

    private Instance<BuildValidationHelper> buildValidationHelperBeans;

    private LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache;

    private LRUPomModelCache pomModelCache;

    private PackageNameWhiteListServiceImpl packageNameWhiteListService;

    private Instance<Predicate<String>> classFilterBeans;

    private final List<BuildValidationHelper> buildValidationHelpers = new ArrayList<>();

    private final List<Predicate<String>> classFilters = new ArrayList<>();

    public LRUBuilderCache() {
        //CDI proxy
    }

    @Inject
    public LRUBuilderCache(@Named("ioStrategy") IOService ioService,
                           KieProjectService projectService,
                           ProjectImportsService importsService,
                           @Any Instance<BuildValidationHelper> buildValidationHelperBeans,
                           @Named("LRUProjectDependenciesClassLoaderCache") LRUProjectDependenciesClassLoaderCache dependenciesClassLoaderCache,
                           @Named("LRUPomModelCache") LRUPomModelCache pomModelCache,
                           PackageNameWhiteListService packageNameWhiteListService,
                           @JavaSourceFilter Instance<Predicate<String>> classFilterBeans) {
        this.ioService = ioService;
        this.projectService = projectService;
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

    public synchronized void invalidateProjectCache(@Observes final InvalidateDMOProjectCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        final Project project = event.getProject();

        //If resource was not within a Project there's nothing to invalidate
        if (project != null) {
            invalidateCache(project);
        }
    }

    public synchronized Builder assertBuilder(POM pom)
            throws NoBuilderFoundException {
        for (Project project : getKeys()) {
            if (project.getPom().getGav().equals(pom.getGav())) {
                return makeBuilder(project);
            }
        }
        throw new NoBuilderFoundException();
    }

    public synchronized Builder assertBuilder(final Project project) {
        return makeBuilder(project);
    }

    public synchronized Builder getBuilder(final Project project) {
        return getEntry(project);
    }

    private Builder makeBuilder(Project project) {
        Builder builder = getEntry(project);
        if (builder == null) {
            builder = new Builder(project,
                                  ioService,
                                  projectService,
                                  importsService,
                                  buildValidationHelpers,
                                  dependenciesClassLoaderCache,
                                  pomModelCache,
                                  packageNameWhiteListService,
                                  createSingleClassFilterPredicate());

            setEntry(project,
                     builder);
        }
        return builder;
    }

    private Predicate<String> createSingleClassFilterPredicate() {
        return classFilters.stream().reduce(o -> true,
                                            (p1, p2) -> p1.and(p2));
    }
}
