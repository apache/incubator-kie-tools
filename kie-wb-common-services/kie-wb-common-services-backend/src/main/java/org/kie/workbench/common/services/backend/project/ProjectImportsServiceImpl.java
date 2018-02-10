/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.backend.project;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.project.backend.server.ProjectConfigurationContentHandler;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.ProjectImportsContent;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

@Service
@ApplicationScoped
public class ProjectImportsServiceImpl
        extends KieService<ProjectImportsContent>
        implements ProjectImportsService {

    protected ProjectConfigurationContentHandler projectConfigurationContentHandler;

    private RenameService renameService;

    private SaveAndRenameServiceImpl<ProjectImports, Metadata> saveAndRenameService;

    public ProjectImportsServiceImpl() {
    }

    @Inject
    public ProjectImportsServiceImpl(final @Named("ioStrategy") IOService ioService,
                                     final ProjectConfigurationContentHandler projectConfigurationContentHandler,
                                     final RenameService renameService,
                                     final SaveAndRenameServiceImpl<ProjectImports, Metadata> saveAndRenameService) {

        this.ioService = ioService;
        this.projectConfigurationContentHandler = projectConfigurationContentHandler;
        this.renameService = renameService;
        this.saveAndRenameService = saveAndRenameService;
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    public void saveProjectImports(final Path path) {
        if (ioService.exists(Paths.convert(path))) {
            throw new FileAlreadyExistsException(path.toString());
        } else {
            ioService.write(Paths.convert(path),
                            projectConfigurationContentHandler.toString(createProjectImports()));
        }
    }

    private ProjectImports createProjectImports() {
        ProjectImports projectImports = new ProjectImports();
        final Imports imports = projectImports.getImports();

        imports.addImport(new Import(java.lang.Number.class.getName()));
        imports.addImport(new Import(java.lang.Boolean.class.getName()));
        imports.addImport(new Import(java.lang.String.class.getName()));
        imports.addImport(new Import(java.lang.Integer.class.getName()));
        imports.addImport(new Import(java.lang.Double.class.getName()));
        imports.addImport(new Import(java.util.List.class.getName()));
        imports.addImport(new Import(java.util.Collection.class.getName()));
        imports.addImport(new Import(java.util.ArrayList.class.getName()));

        return projectImports;
    }

    @Override
    public ProjectImportsContent loadContent(Path path) {
        return super.loadContent(path);
    }

    @Override
    protected ProjectImportsContent constructContent(Path path,
                                                     Overview overview) {
        return new ProjectImportsContent(load(path),
                                         overview);
    }

    @Override
    public ProjectImports load(final Path path) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(path);
            if (!ioService.exists(nioPath)) {
                saveProjectImports(path);
            }
            final String content = ioService.readAllString(Paths.convert(path));
            final ProjectImports projectImports = projectConfigurationContentHandler.toModel(content);

            // java.lang.Number imported by default in new guided rule
            // include it into project imports if not present already
            final Import javaLangNumber = new Import(Number.class);
            if (projectImports.getImports().getImports().stream().noneMatch(anImport -> Objects.equals(anImport, javaLangNumber))) {
                projectImports.getImports().addImport(javaLangNumber);
            }
            return projectImports;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path save(final Path resource,
                     final ProjectImports projectImports,
                     final Metadata metadata,
                     final String comment) {
        try {
            ioService.write(Paths.convert(resource),
                            projectConfigurationContentHandler.toString(projectImports),
                            metadataService.setUpAttributes(resource,
                                                            metadata));

            //The pom.xml, kmodule.xml and project.imports are all saved from ProjectScreenPresenter
            //We only raise InvalidateDMOProjectCacheEvent and ResourceUpdatedEvent(pom.xml) events once
            //in POMService.save to avoid duplicating events (and re-construction of DMO).

            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        return renameService.rename(path, newName, comment);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final ProjectImports content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }
}
