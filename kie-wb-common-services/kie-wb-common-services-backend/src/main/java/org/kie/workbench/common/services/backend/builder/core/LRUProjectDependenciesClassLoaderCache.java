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

import java.net.URLClassLoader;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.api.builder.KieModule;
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieProject;

@ApplicationScoped
@Named("LRUProjectDependenciesClassLoaderCache")
public class LRUProjectDependenciesClassLoaderCache extends LRUCache<KieProject, ClassLoader> {

    private BuildInfoService buildInfoService;

    public LRUProjectDependenciesClassLoaderCache( ) {
    }

    @Inject
    public LRUProjectDependenciesClassLoaderCache( BuildInfoService buildInfoService ) {
        this.buildInfoService = buildInfoService;
    }

    protected void setBuildInfoService( final BuildInfoService buildInfoService ) {
        this.buildInfoService = buildInfoService;
    }

    public synchronized ClassLoader assertDependenciesClassLoader(final KieProject project) {
        ClassLoader classLoader = getEntry(project);
        if (classLoader == null) {
            classLoader = buildClassLoader(project);
            setEntry(project,
                     classLoader);
        }
        return classLoader;
    }

    public synchronized void setDependenciesClassLoader(final KieProject project,
                                                        ClassLoader classLoader) {
        setEntry(project,
                 classLoader);
    }

    private ClassLoader buildClassLoader(final KieProject project) {
        final KieModule module = buildInfoService.getBuildInfo(project).getKieModuleIgnoringErrors();
        return buildClassLoader(project,
                                KieModuleMetaData.Factory.newKieModuleMetaData(module));
    }

    /**
     * This method and the subsequent caching was added for performance reasons, since the dependencies calculation and
     * project class loader calculation tends to be time consuming when we manage project with transitives dependencies.
     * Since the project ClassLoader may change with ever incremental build it's better to store in the cache the
     * ClassLoader part that has the project dependencies. And the project ClassLoader can be easily calculated using
     * this ClassLoader as parent. Since current project classes are quickly calculated on each incremental build, etc.
     */
    public static ClassLoader buildClassLoader(final KieProject project,
                                               final KieModuleMetaData kieModuleMetaData) {
        //By construction the parent class loader for the KieModuleMetadata.getClassLoader() is an URLClass loader
        //that has the project dependencies. So this implementation relies on this. BUT can easily be changed to
        //calculate this URL class loader given that we have the pom.xml and we can use maven libraries classes
        //to calculate project maven dependencies. This is basically what the KieModuleMetaData already does. The
        //optimization was added to avoid the maven transitive calculation on complex projects.
        final ClassLoader classLoader = kieModuleMetaData.getClassLoader().getParent();
        if (classLoader instanceof URLClassLoader) {
            return classLoader;
        } else {
            //this case should never happen. But if ProjectClassLoader calculation for KieModuleMetadata changes at
            //the error will be notified for implementation review.
            throw new RuntimeException("It was not posible to calculate project dependencies class loader for project: "
                                               + project.getKModuleXMLPath());
        }
    }
}