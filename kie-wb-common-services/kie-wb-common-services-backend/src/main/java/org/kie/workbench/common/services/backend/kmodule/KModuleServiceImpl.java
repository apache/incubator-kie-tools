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

package org.kie.workbench.common.services.backend.kmodule;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

@Service
@ApplicationScoped
public class KModuleServiceImpl
        implements KModuleService {

    private IOService ioService;
    private KieModuleService moduleService;
    private MetadataService metadataService;
    private KModuleContentHandler moduleContentHandler;

    public KModuleServiceImpl() {
        // Weld needs this for proxying.
    }

    @Inject
    public KModuleServiceImpl(final @Named("ioStrategy") IOService ioService,
                              final KieModuleService moduleService,
                              final MetadataService metadataService,
                              final KModuleContentHandler moduleContentHandler) {
        this.ioService = ioService;
        this.moduleService = moduleService;
        this.metadataService = metadataService;
        this.moduleContentHandler = moduleContentHandler;
    }

    protected void setModuleService(final KieModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @Override
    public boolean isKModule(final Path resource) {
        try {
            //Null resource paths cannot resolve to a Module
            if (resource == null) {
                return false;
            }

            //Check if path equals kmodule.xml
            final KieModule module = moduleService.resolveModule(resource, false);
            //It's possible that the Incremental Build attempts to act on a Module file before the module has been fully created.
            //This should be a short-term issue that will be resolved when saving a module batches pom.xml, kmodule.xml and project.imports
            //etc into a single git-batch. At present they are saved individually leading to multiple Incremental Build requests.
            if (module == null) {
                return false;
            }

            final org.uberfire.java.nio.file.Path path = Paths.convert(resource).normalize();
            final org.uberfire.java.nio.file.Path kmoduleFilePath = Paths.convert(module.getKModuleXMLPath());
            return path.startsWith(kmoduleFilePath);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path setUpKModule(final Path path) {
        try {
            final org.uberfire.java.nio.file.Path pathToKModuleXML = Paths.convert(path);
            if (ioService.exists(pathToKModuleXML)) {
                throw new FileAlreadyExistsException(pathToKModuleXML.toString());
            } else {
                ioService.write(pathToKModuleXML,
                                moduleContentHandler.toString(new KModuleModel()));

                //Don't raise a NewResourceAdded event as this is handled at the Module level in ModuleServices

                return Paths.convert(pathToKModuleXML);
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public KModuleModel load(final Path path) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
            final String content = ioService.readAllString(nioPath);

            return moduleContentHandler.toModel(content);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path save(final Path path,
                     final KModuleModel content,
                     final Metadata metadata,
                     final String comment) {
        try {
            if (metadata == null) {
                ioService.write(Paths.convert(path),
                                moduleContentHandler.toString(content));
            } else {
                ioService.write(
                        Paths.convert(path),
                        moduleContentHandler.toString(content),
                        metadataService.setUpAttributes(path,
                                                        metadata));
            }

            //The pom.xml, kmodule.xml and project.imports are all saved from ModuleScreenPresenter
            //We only raise InvalidateDMOModuleCacheEvent and ResourceUpdatedEvent(pom.xml) events once
            //in POMService.save to avoid duplicating events (and re-construction of DMO).

            return path;
        } catch (Exception e) {
            e.printStackTrace();
            throw ExceptionUtilities.handleException(e);
        }
    }
}