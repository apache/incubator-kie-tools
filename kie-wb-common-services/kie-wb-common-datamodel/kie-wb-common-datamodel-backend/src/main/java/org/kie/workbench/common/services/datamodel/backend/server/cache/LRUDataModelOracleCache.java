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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.guvnor.common.services.backend.file.FileDiscoveryService;
import org.guvnor.common.services.project.builder.events.InvalidateDMOModuleCacheEvent;
import org.guvnor.common.services.project.builder.events.InvalidateDMOPackageCacheEvent;
import org.guvnor.common.services.project.model.Package;
import org.kie.scanner.KieModuleMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.commons.util.MVELEvaluator;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.backend.file.EnumerationsFileFilter;
import org.kie.workbench.common.services.backend.file.GlobalsFileFilter;
import org.kie.workbench.common.services.datamodel.backend.server.builder.packages.PackageDataModelOracleBuilder;
import org.kie.workbench.common.services.datamodel.spi.DataModelExtension;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.DirectoryStream.Filter;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * A simple LRU cache for Package DataModelOracles
 */
@ApplicationScoped
@Named("PackageDataModelOracleCache")
public class LRUDataModelOracleCache extends LRUCache<Package, PackageDataModelOracle> {

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_ENUMERATIONS = new EnumerationsFileFilter();

    private static final DirectoryStream.Filter<org.uberfire.java.nio.file.Path> FILTER_GLOBALS = new GlobalsFileFilter();

    private IOService ioService;

    private FileDiscoveryService fileDiscoveryService;

    private LRUModuleDataModelOracleCache cacheModules;

    private KieModuleService moduleService;

    private BuildInfoService buildInfoService;

    private Instance<DataModelExtension> dataModelExtensionsProvider;

    private MVELEvaluator evaluator;

    public LRUDataModelOracleCache() {
        //CDI proxy
    }

    @Inject
    public LRUDataModelOracleCache(final @Named("ioStrategy") IOService ioService,
                                   final FileDiscoveryService fileDiscoveryService,
                                   final @Named("ModuleDataModelOracleCache") LRUModuleDataModelOracleCache cacheModules,
                                   final KieModuleService moduleService,
                                   final BuildInfoService buildInfoService,
                                   final Instance<DataModelExtension> dataModelExtensionsProvider,
                                   final MVELEvaluator evaluator) {
        this.ioService = ioService;
        this.fileDiscoveryService = fileDiscoveryService;
        this.cacheModules = cacheModules;
        this.moduleService = moduleService;
        this.buildInfoService = buildInfoService;
        this.dataModelExtensionsProvider = dataModelExtensionsProvider;
        this.evaluator = evaluator;
    }

    public void invalidatePackageCache(@Observes final InvalidateDMOPackageCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        final Path resourcePath = event.getResourcePath();
        final Package pkg = moduleService.resolvePackage(resourcePath);

        //If resource was not within a Package there's nothing to invalidate
        if (pkg != null) {
            invalidateCache(pkg);
        }
    }

    public void invalidateProjectPackagesCache(@Observes final InvalidateDMOModuleCacheEvent event) {
        PortablePreconditions.checkNotNull("event",
                                           event);
        final Path resourcePath = event.getResourcePath();
        final KieModule module = moduleService.resolveModule(resourcePath);

        //If resource was not within a Module there's nothing to invalidate
        if (module == null) {
            return;
        }

        final String moduleUri = module.getRootPath().toURI();
        final List<Package> cacheEntriesToInvalidate = new ArrayList<Package>();
        for (final Package pkg : getKeys()) {
            final Path packageMainSrcPath = pkg.getPackageMainSrcPath();
            final Path packageTestSrcPath = pkg.getPackageTestSrcPath();
            final Path packageMainResourcesPath = pkg.getPackageMainResourcesPath();
            final Path packageTestResourcesPath = pkg.getPackageTestResourcesPath();
            if (packageMainSrcPath != null && packageMainSrcPath.toURI().startsWith(moduleUri)) {
                cacheEntriesToInvalidate.add(pkg);
            } else if (packageTestSrcPath != null && packageTestSrcPath.toURI().startsWith(moduleUri)) {
                cacheEntriesToInvalidate.add(pkg);
            } else if (packageMainResourcesPath != null && packageMainResourcesPath.toURI().startsWith(moduleUri)) {
                cacheEntriesToInvalidate.add(pkg);
            } else if (packageTestResourcesPath != null && packageTestResourcesPath.toURI().startsWith(moduleUri)) {
                cacheEntriesToInvalidate.add(pkg);
            }
        }
        for (final Package pkg : cacheEntriesToInvalidate) {
            invalidateCache(pkg);
        }
    }

    //Check the DataModelOracle for the Package has been created, otherwise create one!
    public PackageDataModelOracle assertPackageDataModelOracle(final KieModule module,
                                                               final Package pkg) {
        PackageDataModelOracle oracle = getEntry(pkg);
        if (oracle == null) {
            oracle = makePackageDataModelOracle(module,
                                                pkg);
            setEntry(pkg,
                     oracle);
        }
        return oracle;
    }

    private PackageDataModelOracle makePackageDataModelOracle(final KieModule module,
                                                              final Package pkg) {
        final String packageName = pkg.getPackageName();
        final PackageDataModelOracleBuilder dmoBuilder = PackageDataModelOracleBuilder.newPackageOracleBuilder(evaluator,
                                                                                                               packageName);
        final ModuleDataModelOracle moduleOracle = cacheModules.assertModuleDataModelOracle(module);
        dmoBuilder.setModuleOracle(moduleOracle);

        //Add Guvnor enumerations
        loadEnumsForPackage(dmoBuilder,
                            module,
                            pkg);

        //Add DSLs
        loadExtensionsForPackage(dmoBuilder,
                                 pkg);

        //Add Globals
        loadGlobalsForPackage(dmoBuilder,
                              pkg);

        return dmoBuilder.build();
    }

    private void loadEnumsForPackage(final PackageDataModelOracleBuilder dmoBuilder,
                                     final KieModule module,
                                     final Package pkg) {
        final org.kie.api.builder.KieModule kieModule = buildInfoService.getBuildInfo(module).getKieModuleIgnoringErrors();
        final ClassLoader classLoader = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule).getClassLoader();
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(pkg.getPackageMainResourcesPath());
        final Collection<org.uberfire.java.nio.file.Path> enumFiles = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                                                         FILTER_ENUMERATIONS);
        for (final org.uberfire.java.nio.file.Path path : enumFiles) {
            final String enumDefinition = ioService.readAllString(path);
            dmoBuilder.addEnum(enumDefinition,
                               classLoader);
        }
    }

    private void loadExtensionsForPackage(final PackageDataModelOracleBuilder dmoBuilder,
                                          final Package pkg) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(pkg.getPackageMainResourcesPath());
        List<DataModelExtension> extensions = stream(dataModelExtensionsProvider.spliterator(),
                                                     false)
                .collect(toList());

        for (final DataModelExtension extension : extensions) {
            Filter<org.uberfire.java.nio.file.Path> filter = extension.getFilter();
            final Collection<org.uberfire.java.nio.file.Path> extensionFiles = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                                                                  filter);
            extensionFiles
                    .stream()
                    .map(file -> extension.getExtensions(file,
                                                         ioService.readAllString(file)))
                    .forEach(mappings -> mappings.forEach(mapping -> dmoBuilder.addExtension(mapping.getKind(),
                                                                                             mapping.getValues())));
        }
    }

    private void loadGlobalsForPackage(final PackageDataModelOracleBuilder dmoBuilder,
                                       final Package pkg) {
        final org.uberfire.java.nio.file.Path nioPackagePath = Paths.convert(pkg.getPackageMainResourcesPath());
        final Collection<org.uberfire.java.nio.file.Path> globalFiles = fileDiscoveryService.discoverFiles(nioPackagePath,
                                                                                                           FILTER_GLOBALS);
        for (final org.uberfire.java.nio.file.Path path : globalFiles) {
            final String definition = ioService.readAllString(path);
            dmoBuilder.addGlobals(definition);
        }
    }
}
