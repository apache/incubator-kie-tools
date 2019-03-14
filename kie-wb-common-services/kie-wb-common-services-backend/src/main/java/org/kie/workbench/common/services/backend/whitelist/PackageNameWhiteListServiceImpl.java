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
package org.kie.workbench.common.services.backend.whitelist;

import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.services.shared.whitelist.PackageNameWhiteListService;
import org.kie.workbench.common.services.shared.whitelist.WhiteList;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

/**
 * Represents a "white list" of permitted package names for use with authoring
 */
@Service
@ApplicationScoped
public class PackageNameWhiteListServiceImpl
        implements PackageNameWhiteListService {

    private IOService ioService;
    private KieModuleService moduleService;
    private PackageNameWhiteListLoader loader;
    private PackageNameWhiteListSaver saver;

    public PackageNameWhiteListServiceImpl() {
    }

    @Inject
    public PackageNameWhiteListServiceImpl(final @Named("ioStrategy") IOService ioService,
                                           final KieModuleService moduleService,
                                           final PackageNameWhiteListLoader loader,
                                           final PackageNameWhiteListSaver saver) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.loader = loader;
        this.saver = saver;
    }

    public void createModuleWhiteList(final Path packageNamesWhiteListPath,
                                                            final String initialContent) {
        if (ioService.exists(Paths.convert(packageNamesWhiteListPath))) {
            throw new FileAlreadyExistsException(packageNamesWhiteListPath.toString());
        } else {
            ioService.write(Paths.convert(packageNamesWhiteListPath),
                                                           initialContent);
        }
    }

    /**
     * Filter the provided Package names by the Module's white list
     * @param module Module for which to filter Package names
     * @param packageNames All Package names in the Module
     * @return A filtered collection of Package names
     */
    @Override
    public WhiteList filterPackageNames(final Module module,
                                        final Collection<String> packageNames) {
        if (packageNames == null) {
            return new WhiteList();
        } else if (module instanceof KieModule) {

            final WhiteList whiteList = load(((KieModule) module).getPackageNamesWhiteListPath());

            if (whiteList.isEmpty()) {
                return new WhiteList(packageNames);
            } else {
                for (Package aPackage : moduleService.resolvePackages(module)) {
                    whiteList.add(aPackage.getPackageName());
                }

                return new PackageNameWhiteListFilter(packageNames,
                                                      whiteList).getFilteredPackageNames();
            }
        } else {
            return new WhiteList(packageNames);
        }
    }

    @Override
    public WhiteList load(final Path packageNamesWhiteListPath) {
        return loader.load(packageNamesWhiteListPath);
    }

    @Override
    public Path save(final Path path,
                     final WhiteList content,
                     final Metadata metadata,
                     final String comment) {
        return saver.save(path,
                          content,
                          metadata,
                          comment);
    }
}

