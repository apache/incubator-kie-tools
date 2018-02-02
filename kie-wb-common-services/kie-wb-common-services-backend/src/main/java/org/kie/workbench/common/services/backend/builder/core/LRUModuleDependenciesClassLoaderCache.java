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
import org.kie.scanner.KieModuleMetaData;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;

@ApplicationScoped
@Named("LRUModuleDependenciesClassLoaderCache")
public class LRUModuleDependenciesClassLoaderCache
        extends LRUCache<KieModule, ClassLoader> {

    private BuildInfoService buildInfoService;

    public LRUModuleDependenciesClassLoaderCache() {
    }

    @Inject
    public LRUModuleDependenciesClassLoaderCache(BuildInfoService buildInfoService) {
        this.buildInfoService = buildInfoService;
    }

    protected void setBuildInfoService(final BuildInfoService buildInfoService) {
        this.buildInfoService = buildInfoService;
    }

    public ClassLoader assertDependenciesClassLoader(final KieModule module) {
        ClassLoader classLoader = getEntry(module);
        if (classLoader == null) {
            classLoader = buildClassLoader(module);
            setEntry(module,
                     classLoader);
        }
        return classLoader;
    }

    /**
     * This method and the subsequent caching was added for performance reasons, since the dependencies calculation and
     * module class loader calculation tends to be time consuming when we manage module with transitives dependencies.
     * Since the module ClassLoader may change with ever incremental build it's better to store in the cache the
     * ClassLoader part that has the module dependencies. And the module ClassLoader can be easily calculated using
     * this ClassLoader as parent. Since current module classes are quickly calculated on each incremental build, etc.
     */
    public static ClassLoader buildClassLoader(final KieModule module,
                                               final KieModuleMetaData kieModuleMetaData) {
        //By construction the parent class loader for the KieModuleMetadata.getClassLoader() is an URLClass loader
        //that has the module dependencies. So this implementation relies on this. BUT can easily be changed to
        //calculate this URL class loader given that we have the pom.xml and we can use maven libraries classes
        //to calculate module maven dependencies. This is basically what the KieModuleMetaData already does. The
        //optimization was added to avoid the maven transitive calculation on complex modules.
        final ClassLoader classLoader = kieModuleMetaData.getClassLoader().getParent();
        if (classLoader instanceof URLClassLoader) {
            return classLoader;
        } else {
            //this case should never happen. But if ProjectClassLoader calculation for KieModuleMetadata changes at
            //the error will be notified for implementation review.
            throw new RuntimeException("It was not possible to calculate project dependencies class loader for project: "
                                               + module.getKModuleXMLPath());
        }
    }

    public void setDependenciesClassLoader(final KieModule module,
                                                        final ClassLoader classLoader) {
        setEntry(module,
                 classLoader);
    }

    private ClassLoader buildClassLoader(final KieModule module) {
        return buildClassLoader(module,
                                KieModuleMetaData.Factory.newKieModuleMetaData(buildInfoService.getBuildInfo(module).getKieModuleIgnoringErrors()));
    }
}