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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;

import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphContent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableEditorService;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableLinkManager;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableGraphResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.common.services.backend.file.DotFileFilter;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Paths;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableGraphEditorServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private CopyService copyService;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private DataModelService dataModelService;

    @Mock
    private WorkItemsEditorService workItemsService;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private VersionRecordService versionRecordService;

    @Mock
    private GuidedDecisionTableEditorService dtableService;

    @Mock
    private GuidedDecisionTableLinkManager dtableLinkManager;

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEvent = new EventSourceMock<>();

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private MetadataServerSideService mockMetaDataService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private org.guvnor.common.services.project.model.Package pkg;

    private GuidedDecisionTableGraphEditorServiceImpl service;

    private GuidedDTableResourceTypeDefinition dtResourceType = new GuidedDTableResourceTypeDefinition(new Decision());
    private GuidedDTableGraphResourceTypeDefinition dtGraphResourceType = new GuidedDTableGraphResourceTypeDefinition(new Decision());
    private DotFileFilter dotFileFilter = new DotFileFilter();

    private final List<org.uberfire.java.nio.file.Path> resolvedPaths = new ArrayList<>();

    @Before
    public void setup() {
        service = new GuidedDecisionTableGraphEditorServiceImpl(ioService,
                                                                copyService,
                                                                deleteService,
                                                                renameService,
                                                                moduleService,
                                                                versionRecordService,
                                                                dtableService,
                                                                dtableLinkManager,
                                                                resourceOpenedEvent,
                                                                commentedOptionFactory,
                                                                dtResourceType,
                                                                dotFileFilter,
                                                                sessionInfo) {
            {
                this.metadataService = mockMetaDataService;
            }
        };

        when(moduleService.resolvePackage(any(Path.class))).thenReturn(pkg);
        when(pkg.getPackageMainResourcesPath()).thenReturn(PathFactory.newPath("project",
                                                                               "file://project/src/main/resources"));

        resolvedPaths.clear();
        when(ioService.newDirectoryStream(any(org.uberfire.java.nio.file.Path.class))).thenReturn(new MockDirectoryStream(resolvedPaths));
    }

    @Test
    public void checkCreate() {
        final Path context = mock(Path.class);
        final String fileName = "filename." + dtGraphResourceType.getSuffix();
        final GuidedDecisionTableEditorGraphModel content = new GuidedDecisionTableEditorGraphModel();
        final String comment = "comment";

        when(context.toURI()).thenReturn("file://project/src/main/resources/mypackage");

        final Path p = service.create(context,
                                      fileName,
                                      content,
                                      comment);

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               any(String.class),
                               any());

        assertTrue(p.toURI().contains("src/main/resources/mypackage/filename." + dtGraphResourceType.getSuffix()));
    }

    @Test
    public void checkLoad() {
        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("file://project/src/main/resources/mypackage/dtable." + dtGraphResourceType.getSuffix());

        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn("");

        final GuidedDecisionTableEditorGraphModel model = service.load(path);

        verify(ioService,
               times(1)).readAllString(any(org.uberfire.java.nio.file.Path.class));
        assertNotNull(model);
    }

    @Test
    public void checkConstructContent() {
        final Path path = mock(Path.class);
        final Overview overview = mock(Overview.class);
        when(path.toURI()).thenReturn("file://project/src/main/resources/mypackage/dtable." + dtGraphResourceType.getSuffix());

        final GuidedDecisionTableEditorGraphContent content = service.constructContent(path,
                                                                                       overview);

        verify(resourceOpenedEvent,
               times(1)).fire(any(ResourceOpenedEvent.class));

        assertNotNull(content.getModel());
        assertEquals(overview,
                     content.getOverview());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSave() {
        final Path path = mock(Path.class);
        final GuidedDecisionTableEditorGraphModel model = new GuidedDecisionTableEditorGraphModel();
        final Metadata metadata = mock(Metadata.class);
        final String comment = "comment";
        when(path.toURI()).thenReturn("file://project/src/main/resources/mypackage/dtable." + dtGraphResourceType.getSuffix());

        service.save(path,
                     model,
                     metadata,
                     comment);

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               any(String.class),
                               any(Map.class),
                               any());
    }

    @Test
    public void checkDelete() {
        final Path path = mock(Path.class);
        final String comment = "comment";

        service.delete(path,
                       comment);

        verify(deleteService,
               times(1)).delete(eq(path),
                                eq(comment));
    }

    @Test
    public void checkRename() {
        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final String comment = "comment";

        service.rename(path,
                       newFileName,
                       comment);

        verify(renameService,
               times(1)).rename(eq(path),
                                eq(newFileName),
                                eq(comment));
    }

    @Test
    public void checkCopy() {
        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final String comment = "comment";

        service.copy(path,
                     newFileName,
                     comment);

        verify(copyService,
               times(1)).copy(eq(path),
                              eq(newFileName),
                              eq(comment));
    }

    @Test
    public void copyCopyToPackage() {
        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Path newPackagePath = mock(Path.class);
        final String comment = "comment";

        service.copy(path,
                     newFileName,
                     newPackagePath,
                     comment);

        verify(copyService,
               times(1)).copy(eq(path),
                              eq(newFileName),
                              eq(newPackagePath),
                              eq(comment));
    }

    @Test
    public void testListDecisionTablesInPackage() {
        final Path path = mock(Path.class);

        resolvedPaths.add(makeNioPath("file://project/src/main/resources/dtable1.gdst"));
        resolvedPaths.add(makeNioPath("file://project/src/main/resources/dtable2.gdst"));
        resolvedPaths.add(makeNioPath("file://project/src/main/resources/dtable3.gdst"));
        resolvedPaths.add(makeNioPath("file://project/src/main/resources/pupa.smurf"));

        final List<Path> paths = service.listDecisionTablesInPackage(path);

        assertNotNull(paths);
        assertEquals(3,
                     paths.size());
        final Set<String> fileNames = new HashSet<>();
        fileNames.addAll(paths.stream().collect(Collectors.mapping(Path::getFileName,
                                                                   Collectors.toSet())));
        assertTrue(fileNames.contains("dtable1.gdst"));
        assertTrue(fileNames.contains("dtable2.gdst"));
        assertTrue(fileNames.contains("dtable3.gdst"));
    }

    @Test
    public void testListDecisionTablesInPackageExcludesDotFiles() {
        final Path path = mock(Path.class);

        resolvedPaths.add(makeNioPath("file://project/src/main/resources/dtable1.gdst"));
        resolvedPaths.add(makeNioPath("file://project/src/main/resources/.dtable1.gdst"));

        final List<Path> paths = service.listDecisionTablesInPackage(path);

        assertNotNull(paths);
        assertEquals(1,
                     paths.size());
        final Set<String> fileNames = new HashSet<>();
        fileNames.addAll(paths.stream().collect(Collectors.mapping(Path::getFileName,
                                                                   Collectors.toSet())));
        assertTrue(fileNames.contains("dtable1.gdst"));
    }

    private org.uberfire.java.nio.file.Path makeNioPath(final String uri) {
        return Paths.get(uri);
    }
}
