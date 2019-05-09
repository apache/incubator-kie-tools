/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphContent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.services.backend.service.KieService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

@Service
@ApplicationScoped
public class GuidedDecisionTableGraphEditorServiceImpl
        extends KieService<GuidedDecisionTableEditorGraphContent>
        implements GuidedDecisionTableGraphEditorService {

    private IOService ioService;
    private CopyService copyService;
    private DeleteService deleteService;
    private RenameService renameService;
    private KieModuleService moduleService;
    private VersionRecordService versionRecordService;
    private GuidedDecisionTableEditorService dtableService;
    private GuidedDecisionTableLinkManager dtableLinkManager;
    private Event<ResourceOpenedEvent> resourceOpenedEvent;
    private CommentedOptionFactory commentedOptionFactory;
    private GuidedDTableResourceTypeDefinition resourceType;
    private DotFileFilter dotFileFilter;
    private SafeSessionInfo safeSessionInfo;

    public GuidedDecisionTableGraphEditorServiceImpl() {
        //Zero parameter constructor for CDI
    }

    @Inject
    public GuidedDecisionTableGraphEditorServiceImpl(final @Named("ioStrategy") IOService ioService,
                                                     final CopyService copyService,
                                                     final DeleteService deleteService,
                                                     final RenameService renameService,
                                                     final KieModuleService moduleService,
                                                     final VersionRecordService versionRecordService,
                                                     final GuidedDecisionTableEditorService dtableService,
                                                     final GuidedDecisionTableLinkManager dtableLinkManager,
                                                     final Event<ResourceOpenedEvent> resourceOpenedEvent,
                                                     final CommentedOptionFactory commentedOptionFactory,
                                                     final GuidedDTableResourceTypeDefinition resourceType,
                                                     final DotFileFilter dotFileFilter,
                                                     final SessionInfo sessionInfo) {
        this.ioService = ioService;
        this.copyService = copyService;
        this.deleteService = deleteService;
        this.renameService = renameService;
        this.moduleService = moduleService;
        this.versionRecordService = versionRecordService;
        this.dtableService = dtableService;
        this.dtableLinkManager = dtableLinkManager;
        this.resourceOpenedEvent = resourceOpenedEvent;
        this.commentedOptionFactory = commentedOptionFactory;
        this.resourceType = resourceType;
        this.dotFileFilter = dotFileFilter;
        this.safeSessionInfo = new SafeSessionInfo(sessionInfo);
    }

    @Override
    public Path create(final Path context,
                       final String fileName,
                       final GuidedDecisionTableEditorGraphModel model,
                       final String comment) {
        try {
            final org.uberfire.java.nio.file.Path nioPath = Paths.convert(context).resolve(fileName);
            if (ioService.exists(nioPath)) {
                throw new FileAlreadyExistsException(nioPath.toString());
            }

            final Set<Path> paths = getLinkedDecisionTablesInPackage(context);
            paths.forEach((path) -> model.getEntries().add(new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry(path,
                                                                                                                                 getLatestVersionPath(path))));

            ioService.write(nioPath,
                            GuidedDTGraphXMLPersistence.getInstance().marshal(model),
                            commentedOptionFactory.makeCommentedOption(comment));

            final Path newPath = Paths.convert(nioPath);
            return newPath;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public GuidedDecisionTableEditorGraphModel load(final Path path) {
        try {
            final String content = ioService.readAllString(Paths.convert(path));

            return GuidedDTGraphXMLPersistence.getInstance().unmarshal(content);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public GuidedDecisionTableEditorGraphContent loadContent(final Path path) {
        return super.loadContent(path);
    }

    @Override
    protected GuidedDecisionTableEditorGraphContent constructContent(final Path path,
                                                                     final Overview overview) {
        final GuidedDecisionTableEditorGraphModel model = load(path);

        //Signal opening to interested parties
        resourceOpenedEvent.fire(new ResourceOpenedEvent(path,
                                                         safeSessionInfo));

        return new GuidedDecisionTableEditorGraphContent(model,
                                                         overview);
    }

    @Override
    public Path save(final Path path,
                     final GuidedDecisionTableEditorGraphModel model,
                     final Metadata metadata,
                     final String comment) {
        try {
            versionEntriesPaths(model);

            ioService.write(Paths.convert(path),
                            GuidedDTGraphXMLPersistence.getInstance().marshal(model),
                            metadataService.setUpAttributes(path,
                                                            metadata),
                            commentedOptionFactory.makeCommentedOption(comment));

            return path;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private void versionEntriesPaths(final GuidedDecisionTableEditorGraphModel model) {
        for (GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry entry : model.getEntries()) {
            entry.setPathVersion(getLatestVersionPath(entry.getPathHead()));
        }
    }

    private Path getLatestVersionPath(final Path path) {
        final List<VersionRecord> versions = versionRecordService.load(Paths.convert(path));
        final String versionUri = versions.get(versions.size() - 1).uri();
        return PathFactory.newPathBasedOn(path.getFileName(),
                                          versionUri,
                                          path);
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
    public List<Path> listDecisionTablesInPackage(final Path path) {
        try {
            final Package pkg = moduleService.resolvePackage(path);
            if (pkg == null) {
                return Collections.emptyList();
            }

            final Path pkgPath = pkg.getPackageMainResourcesPath();
            final org.uberfire.java.nio.file.Path nioPkgPath = Paths.convert(pkgPath);

            final List<Path> paths = findDecisionTables(nioPkgPath);
            return paths;
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    private List<Path> findDecisionTables(final org.uberfire.java.nio.file.Path nioRootPath) {
        final List<Path> paths = new ArrayList<>();
        try (final DirectoryStream<org.uberfire.java.nio.file.Path> directoryStream = ioService.newDirectoryStream(nioRootPath)) {
            for (org.uberfire.java.nio.file.Path nioPath : directoryStream) {
                final Path path = Paths.convert(nioPath);
                if (!dotFileFilter.accept(nioPath) && resourceType.accept(path)) {
                    paths.add(path);
                }
            }
        }
        return paths;
    }

    private Set<Path> getLinkedDecisionTablesInPackage(final Path context) {
        final Set<Path> linkedDecisionTablePaths = new HashSet<>();
        final List<Path> allDecisionTablePathsInPackage = listDecisionTablesInPackage(context);
        final List<Pair<Path, GuidedDecisionTable52>> allDecisionTablesInPackage = new ArrayList<>();
        allDecisionTablePathsInPackage.forEach((path) -> allDecisionTablesInPackage.add(new Pair<>(path,
                                                                                                   dtableService.load(path))));
        allDecisionTablesInPackage.forEach((source) -> {
            final List<Pair<Path, GuidedDecisionTable52>> otherDecisionTablesInPackage = new ArrayList<>();
            otherDecisionTablesInPackage.addAll(allDecisionTablesInPackage);
            otherDecisionTablesInPackage.remove(source);
            otherDecisionTablesInPackage.forEach((target) -> dtableLinkManager.link(source.getK2(),
                                                                                    target.getK2(),
                                                                                    (s, t) -> {
                                                                                        linkedDecisionTablePaths.add(source.getK1());
                                                                                        linkedDecisionTablePaths.add(target.getK1());
                                                                                    }));
        });

        return linkedDecisionTablePaths;
    }
}
