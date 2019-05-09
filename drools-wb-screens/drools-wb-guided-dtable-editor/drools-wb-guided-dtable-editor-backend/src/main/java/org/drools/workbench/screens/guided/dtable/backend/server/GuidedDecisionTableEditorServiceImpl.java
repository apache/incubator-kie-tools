/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.backend.server;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.backend.GuidedDTXMLPersistence;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableGraphResourceTypeDefinition;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.FileExtensionFilter;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.Files;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class GuidedDecisionTableEditorServiceImpl
        extends KieService<GuidedDecisionTableEditorContent>
        implements GuidedDecisionTableEditorService {

    private IOService ioService;
    private CopyService copyService;
    private DeleteService deleteService;
    private RenameService renameService;
    private DataModelService dataModelService;
    private WorkItemsEditorService workItemsService;
    private KieModuleService moduleService;
    private VersionRecordService versionRecordService;
    private GuidedDecisionTableGraphEditorService dtableGraphService;
    private GuidedDTableGraphResourceTypeDefinition dtableGraphType;
    private Event<ResourceOpenedEvent> resourceOpenedEvent;
    private GenericValidator genericValidator;
    private CommentedOptionFactory commentedOptionFactory;
    private SafeSessionInfo safeSessionInfo;
    private SaveAndRenameServiceImpl<GuidedDecisionTable52, Metadata> saveAndRenameService;

    public GuidedDecisionTableEditorServiceImpl() {
        //Zero parameter constructor for CDI
    }

    @Inject
    public GuidedDecisionTableEditorServiceImpl(final @Named("ioStrategy") IOService ioService,
                                                final CopyService copyService,
                                                final DeleteService deleteService,
                                                final RenameService renameService,
                                                final DataModelService dataModelService,
                                                final WorkItemsEditorService workItemsService,
                                                final KieModuleService moduleService,
                                                final VersionRecordService versionRecordService,
                                                final GuidedDecisionTableGraphEditorService dtableGraphService,
                                                final GuidedDTableGraphResourceTypeDefinition dtableGraphType,
                                                final Event<ResourceOpenedEvent> resourceOpenedEvent,
                                                final GenericValidator genericValidator,
                                                final CommentedOptionFactory commentedOptionFactory,
                                                final SaveAndRenameServiceImpl<GuidedDecisionTable52, Metadata> saveAndRenameService,
                                                final SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.copyService = copyService;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.dataModelService = dataModelService;
        this.workItemsService = workItemsService;
        this.moduleService = moduleService;
        this.versionRecordService = versionRecordService;
        this.dtableGraphService = dtableGraphService;
        this.dtableGraphType = dtableGraphType;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.genericValidator = genericValidator;
        this.commentedOptionFactory = commentedOptionFactory;
        this.saveAndRenameService = saveAndRenameService;
        this.safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public Path create(final Path context,
                       final String fileName,
                       final GuidedDecisionTable52 content,
                       final String comment) {
        try {
            final Package pkg = moduleService.resolvePackage(context);
            final String packageName = (pkg == null ? null : pkg.getPackageName());
            content.setPackageName(packageName);

            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(context).resolve(fileName);
            final Path newPath = Paths.convert(nioPath);

            if (ioService.exists(nioPath)) {
                throw new FileAlreadyExistsException(nioPath.toString());
            }

            ioService.write(nioPath,
                            GuidedDTXMLPersistence.getInstance().marshal(content),
                            commentedOptionFactory.makeCommentedOption(comment));

            return newPath;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public GuidedDecisionTable52 load(final Path path) {
        try {
            final String content = ioService.readAllString(Paths.convert(path));

            return GuidedDTXMLPersistence.getInstance().unmarshal(content);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public GuidedDecisionTableEditorContent loadContent(final Path path) {
        return super.loadContent(path);
    }

    @Override
    protected GuidedDecisionTableEditorContent constructContent(Path path,
                                                                Overview overview) {
        final GuidedDecisionTable52 model = load(path);

        final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
        final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();

        //Get FQCN's used by model
        final GuidedDecisionTableModelVisitor visitor = new GuidedDecisionTableModelVisitor(model);
        final Set<String> consumedFQCNs = visitor.getConsumedModelClasses();

        //Get FQCN's used by Globals
        consumedFQCNs.addAll(oracle.getPackageGlobals().values());

        //Get FQCN's of collections defined in project settings
        //they can be used in From Collect expressions
        consumedFQCNs.addAll(oracle.getModuleCollectionTypes()
                                     .entrySet()
                                     .stream()
                                     .filter(entry -> entry.getValue())
                                     .map(entry -> entry.getKey())
                                     .collect(Collectors.toSet()));

        DataModelOracleUtilities.populateDataModel(oracle,
                                                   dataModel,
                                                   consumedFQCNs);

        final Set<PortableWorkDefinition> workItemDefinitions = workItemsService.loadWorkItemDefinitions(path);

        //Signal opening to interested parties
        resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                         safeSessionInfo));

        return new GuidedDecisionTableEditorContent(model,
                                                    workItemDefinitions,
                                                    overview,
                                                    dataModel);
    }

    @Override
    public PackageDataModelOracleBaselinePayload loadDataModel(final Path path) {
        try {
            final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
            final PackageDataModelOracleBaselinePayload dataModel = new PackageDataModelOracleBaselinePayload();
            //There are no classes to pre-load into the DMO when requesting a new Data Model only
            DataModelOracleUtilities.populateDataModel(oracle,
                                                       dataModel,
                                                       new HashSet<String>());

            return dataModel;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path save(final Path resource,
                     final GuidedDecisionTable52 model,
                     final Metadata metadata,
                     final String comment) {
        try {
            final Package pkg = moduleService.resolvePackage(resource);
            final String packageName = (pkg == null ? null : pkg.getPackageName());
            model.setPackageName(packageName);

            ioService.write(Paths.convert(resource),
                            GuidedDTXMLPersistence.getInstance().marshal(model),
                            metadataService.setUpAttributes(resource,
                                                            metadata),
                            commentedOptionFactory.makeCommentedOption(comment));

            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path saveAndUpdateGraphEntries(final Path resource,
                                          final GuidedDecisionTable52 model,
                                          final Metadata metadata,
                                          final String comment) {
        try {
            ioService.startBatch(Paths.convert(resource).getFileSystem());

            save(resource,
                 model,
                 metadata,
                 comment);

            updateGraphElementPaths(resource,
                                    getLatestVersionPath(resource));

            return resource;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        } finally {
            ioService.endBatch();
        }
    }

    private Path getLatestVersionPath(final Path path) {
        final List<VersionRecord> versions = versionRecordService.load(Paths.convert(path));
        final String versionUri = versions.get(versions.size() - 1).uri();
        return PathFactory.newPathBasedOn(path.getFileName(),
                                          versionUri,
                                          path);
    }

    private void updateGraphElementPaths(final Path source,
                                         final Path destination) {
        try (final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream =
                     ioService.newDirectoryStream(getParentFolder(source),
                                                  new FileExtensionFilter(dtableGraphType.getSuffix()))) {
            directoryStream.forEach((path) -> updateGraphElementPath(source,
                                                                     destination,
                                                                     Paths.convert(path)));
        }
    }

    private org.uberfire.java.nio.file.Path getParentFolder(final Path path) {
        org.uberfire.java.nio.file.Path nioFolderPath = Paths.convert(path);
        return Files.isDirectory(nioFolderPath) ? nioFolderPath : nioFolderPath.getParent();
    }

    private void updateGraphElementPath(final Path source,
                                        final Path destination,
                                        final Path graphPath) {
        final GuidedDecisionTableEditorGraphModel dtGraphModel = dtableGraphService.load(graphPath);
        final Set<GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry> dtGraphEntries = dtGraphModel.getEntries();
        dtGraphEntries.stream().filter((e) -> e.getPathHead().equals(source)).forEach((e) -> e.setPathVersion(destination));
        ioService.write(Paths.convert(graphPath),
                        GuidedDTGraphXMLPersistence.getInstance().marshal(dtGraphModel),
                        commentedOptionFactory.makeCommentedOption("Updated Path version for [" + source.toURI() + "] to [" + destination.toURI() + "]."));
    }

    @Override
    public void delete(final Path path,
                       final String comment) {
        try {
            deleteService.delete(path,
                                 comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        try {
            return renameService.rename(path,
                                        newName,
                                        comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path copy(final Path path,
                     final String newName,
                     final Path targetDirectory,
                     final String comment) {
        try {
            return copyService.copy(path,
                                    newName,
                                    targetDirectory,
                                    comment);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toSource(final Path path,
                           final GuidedDecisionTable52 model) {
        return sourceServices.getServiceFor(Paths.convert(path)).getSource(Paths.convert(path),
                                                                           model);
    }

    @Override
    public List<ValidationMessage> validate(final Path path,
                                            final GuidedDecisionTable52 content) {
        try {
            return genericValidator.validate(path,
                                             GuidedDTXMLPersistence.getInstance().marshal(content));
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final GuidedDecisionTable52 content,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, content, comment);
    }
}
