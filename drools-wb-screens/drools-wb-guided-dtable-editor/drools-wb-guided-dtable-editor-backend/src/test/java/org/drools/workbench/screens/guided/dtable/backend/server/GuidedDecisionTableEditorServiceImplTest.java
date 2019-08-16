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

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.backend.server.conversion.DecisionTableGuidedToDecisionTableXLSConverter;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorContent;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableGraphResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.drools.workbench.screens.workitems.service.WorkItemsEditorService;
import org.guvnor.common.services.backend.file.FileExtensionFilter;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.project.categories.Decision;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.source.SourceService;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.backend.version.VersionRecordService;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.ext.editor.commons.version.impl.PortableVersionRecord;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
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
public class GuidedDecisionTableEditorServiceImplTest {

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
    private GuidedDecisionTableGraphEditorService dtableGraphService;

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEvent = new EventSourceMock<>();

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private SourceServices mockSourceServices;

    @Mock
    private MetadataServerSideService mockMetaDataService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private SaveAndRenameServiceImpl<GuidedDecisionTable52, Metadata> saveAndRenameService;

    @Mock
    private org.guvnor.common.services.project.model.Package pkg;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private FileSystemProvider fileSystemProvider;

    @Mock
    private BasicFileAttributes basicFileAttributes;

    private GuidedDTableResourceTypeDefinition dtType = new GuidedDTableResourceTypeDefinition(new Decision());
    private GuidedDTableGraphResourceTypeDefinition dtGraphType = new GuidedDTableGraphResourceTypeDefinition(new Decision());
    private GuidedDecisionTableEditorServiceImpl service;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        service = new GuidedDecisionTableEditorServiceImpl(ioService,
                                                           copyService,
                                                           deleteService,
                                                           renameService,
                                                           dataModelService,
                                                           workItemsService,
                                                           moduleService,
                                                           versionRecordService,
                                                           dtableGraphService,
                                                           mock(DecisionTableGuidedToDecisionTableXLSConverter.class),
                                                           dtGraphType,
                                                           resourceOpenedEvent,
                                                           genericValidator,
                                                           commentedOptionFactory,
                                                           saveAndRenameService,
                                                           sessionInfo) {
            {
                this.sourceServices = mockSourceServices;
                this.metadataService = mockMetaDataService;
            }
        };

        when(moduleService.resolvePackage(any(Path.class))).thenReturn(pkg);
        when(pkg.getPackageName()).thenReturn("mypackage");
        when(pkg.getPackageMainResourcesPath()).thenReturn(PathFactory.newPath("mypackage",
                                                                               "default://project/src/main/resources"));

        when(fileSystem.provider()).thenReturn(fileSystemProvider);
        when(fileSystemProvider.readAttributes(any(org.uberfire.java.nio.file.Path.class),
                                               any(Class.class))).thenReturn(basicFileAttributes);
        when(basicFileAttributes.isRegularFile()).thenReturn(true);
    }

    @Test
    public void checkCreate() {
        final Path context = mock(Path.class);
        final String fileName = "filename." + dtType.getSuffix();
        final GuidedDecisionTable52 content = new GuidedDecisionTable52();
        final String comment = "comment";

        when(context.toURI()).thenReturn("default://project/src/main/resources/mypackage");

        final Path p = service.create(context,
                                      fileName,
                                      content,
                                      comment);

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               any(String.class),
                               any(CommentedOption.class));

        assertTrue(p.toURI().contains("src/main/resources/mypackage/filename." + dtType.getSuffix()));
        assertEquals("mypackage",
                     content.getPackageName());
    }

    @Test
    public void checkLoad() {
        final Path path = mock(Path.class);
        when(path.toURI()).thenReturn("default://project/src/main/resources/mypackage/dtable.gdst");

        when(ioService.readAllString(any(org.uberfire.java.nio.file.Path.class))).thenReturn("");

        final GuidedDecisionTable52 model = service.load(path);

        verify(ioService,
               times(1)).readAllString(any(org.uberfire.java.nio.file.Path.class));
        assertNotNull(model);
    }

    @Test
    public void checkConstructContent() {
        final Path path = mock(Path.class);
        final Overview overview = mock(Overview.class);
        final PackageDataModelOracle oracle = mock(PackageDataModelOracle.class);
        when(oracle.getModuleCollectionTypes()).thenReturn(new HashMap<String, Boolean>() {{
            put("java.util.List",
                true);
            put("java.util.Set",
                true);
            put("java.util.Collection",
                true);
            put("java.util.UnknownCollection",
                false);
        }});
        final Set<PortableWorkDefinition> workItemDefinitions = new HashSet<>();
        when(path.toURI()).thenReturn("default://project/src/main/resources/mypackage/dtable.gdst");
        when(dataModelService.getDataModel(eq(path))).thenReturn(oracle);
        when(workItemsService.loadWorkItemDefinitions(eq(path))).thenReturn(workItemDefinitions);

        final GuidedDecisionTableEditorContent content = service.constructContent(path,
                                                                                  overview);

        verify(resourceOpenedEvent,
               times(1)).fire(any(ResourceOpenedEvent.class));

        assertNotNull(content.getModel());
        assertNotNull(content.getDataModel());
        assertNotNull(content.getWorkItemDefinitions());
        assertEquals(overview,
                     content.getOverview());
        assertEquals(3,
                     content.getDataModel().getCollectionTypes().size());
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.Collection"));
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.List"));
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.Set"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSave() {
        final Path path = mock(Path.class);
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final Metadata metadata = mock(Metadata.class);
        final String comment = "comment";
        when(path.toURI()).thenReturn("default://project/src/main/resources/mypackage/dtable.gdst");

        service.save(path,
                     model,
                     metadata,
                     comment);

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               any(String.class),
                               any(Map.class),
                               any(CommentedOption.class));

        assertEquals("mypackage",
                     model.getPackageName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSaveAndUpdateGraphEntries() {
        //Setup Decision Table
        final Path path = mock(Path.class);
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final Metadata metadata = mock(Metadata.class);
        final String comment = "comment";
        final String headPathUri = "default://project/src/main/resources/mypackage/dtable.gdst";
        final String versionPathUri = "default://0123456789@project/src/main/resources/mypackage/dtable.gdst";
        when(path.toURI()).thenReturn(headPathUri);
        when(path.getFileName()).thenReturn("dtable.gdst");

        //Setup Decision Table versions
        final List<VersionRecord> versions = new ArrayList<>();
        versions.add(new PortableVersionRecord("0123456789",
                                               "manstis",
                                               "manstis@email.com",
                                               "comment",
                                               Calendar.getInstance().getTime(),
                                               versionPathUri));
        when(versionRecordService.load(any(org.uberfire.java.nio.file.Path.class))).thenReturn(versions);

        //Setup Decision Table Graph
        final URI dtGraphPathUri = URI.create("default://project/src/main/resources/mypackage/graph1.gdst-set");
        final org.uberfire.java.nio.file.Path dtGraphPath = mock(org.uberfire.java.nio.file.Path.class);
        when(dtGraphPath.toUri()).thenReturn(dtGraphPathUri);
        when(dtGraphPath.getFileName()).thenReturn(dtGraphPath);
        when(dtGraphPath.getFileSystem()).thenReturn(fileSystem);

        final List<org.uberfire.java.nio.file.Path> dtGraphPaths = new ArrayList<>();
        dtGraphPaths.add(dtGraphPath);

        when(ioService.newDirectoryStream(any(org.uberfire.java.nio.file.Path.class),
                                          any(FileExtensionFilter.class))).thenReturn(new MockDirectoryStream(dtGraphPaths));

        final GuidedDecisionTableEditorGraphModel dtGraphModel = new GuidedDecisionTableEditorGraphModel();
        dtGraphModel.getEntries().add(new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry(path,
                                                                                                            path));
        when(dtableGraphService.load(any(Path.class))).thenReturn(dtGraphModel);

        //Test save
        service.saveAndUpdateGraphEntries(path,
                                          model,
                                          metadata,
                                          comment);

        verify(ioService,
               times(1)).startBatch(any(FileSystem.class));

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               any(String.class),
                               any(Map.class),
                               any(CommentedOption.class));

        verify(ioService,
               times(1)).endBatch();

        assertEquals("mypackage",
                     model.getPackageName());
        assertEquals(1,
                     dtGraphModel.getEntries().size());
        assertEquals(versions.get(0).uri(),
                     dtGraphModel.getEntries().iterator().next().getPathVersion().toURI());
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
    @SuppressWarnings("unchecked")
    public void checkToSource() {
        final Path path = mock(Path.class);
        final GuidedDecisionTable52 model = new GuidedDecisionTable52();
        final SourceService mockSourceService = mock(SourceService.class);

        when(path.toURI()).thenReturn("default://project/src/main/resources/mypackage");
        when(mockSourceServices.getServiceFor(any(org.uberfire.java.nio.file.Path.class))).thenReturn(mockSourceService);

        service.toSource(path,
                         model);

        verify(mockSourceServices,
               times(1)).getServiceFor(any(org.uberfire.java.nio.file.Path.class));
        verify(mockSourceService,
               times(1)).getSource(any(org.uberfire.java.nio.file.Path.class),
                                   eq(model));
    }

    @Test
    public void testInit() throws Exception {
        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final GuidedDecisionTable52 content = mock(GuidedDecisionTable52.class);
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }

    @Test
    public void checkValidate() {
//        service.validate(  )
    }
}
