/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.backend.version.PathResolver;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.FileAlreadyExistsException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioSimulationServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private SaveAndRenameServiceImpl<ScenarioSimulationModel, Metadata> saveAndRenameService;

    @Mock
    private PathResolver pathResolver;

    @Mock
    protected KieServiceOverviewLoader overviewLoader;

    @Mock
    protected MetadataServerSideService metadataService;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private CopyService copyService;

    @InjectMocks
    private ScenarioSimulationServiceImpl service = new ScenarioSimulationServiceImpl(mock(SafeSessionInfo.class));

    private Path path = PathFactory.newPath("contextpath", "file:///contextpath");

    @Test
    public void init() throws Exception {
        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void delete() throws Exception {
        service.delete(path,
                       "Removing this");
        verify(deleteService).delete(path,
                                     "Removing this");
    }

    @Test
    public void rename() throws Exception {
        service.rename(path,
                       "newName.scesim",
                       "comment");
        verify(renameService).rename(path,
                                     "newName.scesim",
                                     "comment");
    }

    @Test
    public void copy() throws Exception {
        service.copy(path,
                     "newName.scesim",
                     "comment");
        verify(copyService).copy(path,
                                 "newName.scesim",
                                 "comment");
    }

    @Test
    public void copyToDirectory() throws Exception {
        final Path folder = mock(Path.class);
        service.copy(path,
                     "newName.scesim",
                     folder,
                     "comment");
        verify(copyService).copy(path,
                                 "newName.scesim",
                                 folder,
                                 "comment");
    }

    @Test
    public void saveAndRename() throws Exception {
        final Metadata metadata = mock(Metadata.class);
        final ScenarioSimulationModel model = new ScenarioSimulationModel();
        service.saveAndRename(path,
                              "newName.scesim",
                              metadata,
                              model,
                              "comment");
        verify(saveAndRenameService).saveAndRename(path,
                                                   "newName.scesim",
                                                   metadata,
                                                   model,
                                                   "comment");
    }

    @Test
    public void save() throws Exception {

        final Path returnPath = service.save(this.path,
                                             new ScenarioSimulationModel(),
                                             new Metadata(),
                                             "Commit comment");

        assertNotNull(returnPath);
        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                anyString(),
                                anyMap(),
                                any(CommentedOption.class));
    }

    @Test
    public void create() throws Exception {
        doReturn(false).when(ioService).exists(any());

        final Path returnPath = service.create(this.path,
                                               "test.scesim",
                                               new ScenarioSimulationModel(),
                                               "Commit comment");

        assertNotNull(returnPath);
        verify(ioService).write(any(org.uberfire.java.nio.file.Path.class),
                                anyString(),
                                any(CommentedOption.class));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createFileExists() throws Exception {
        doReturn(true).when(ioService).exists(any());

        service.create(this.path,
                       "test.scesim",
                       new ScenarioSimulationModel(),
                       "Commit comment");
    }
}