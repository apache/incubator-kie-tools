/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.globals.backend.server;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.screens.globals.model.GlobalsModel;
import org.drools.workbench.screens.globals.service.GlobalsEditorService;
import org.guvnor.common.services.backend.metadata.MetadataServerSideService;
import org.guvnor.common.services.backend.metadata.attribute.GeneratedAttributesView;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlobalsEditorServiceTest {

    @Mock
    private KieModuleService kieModuleService;

    @Mock
    private IOService ioService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private MetadataServerSideService metadataService;

    @Mock
    private SaveAndRenameServiceImpl<GlobalsModel, Metadata> saveAndRenameService;

    @InjectMocks
    private GlobalsEditorService globalsEditorService = new GlobalsEditorServiceImpl() {
        {
            moduleService = GlobalsEditorServiceTest.this.kieModuleService;
            ioService = GlobalsEditorServiceTest.this.ioService;
            commentedOptionFactory = GlobalsEditorServiceTest.this.commentedOptionFactory;
            metadataService = GlobalsEditorServiceTest.this.metadataService;
        }
    };

    @Test
    public void save() {
        Path path = PathFactory.newPath("test",
                                        "file:///test");
        GlobalsModel globalsModel = mock(GlobalsModel.class);

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);

        globalsEditorService.create(path,
                                    "test",
                                    globalsModel,
                                    "comment");

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               anyString(),
                               any());
    }

    @Test
    public void generate() {
        Path path = PathFactory.newPath("test",
                                        "file:///test");
        GlobalsModel globalsModel = mock(GlobalsModel.class);

        when(ioService.exists(any(org.uberfire.java.nio.file.Path.class))).thenReturn(false);

        Map<String, Object> metadataMap = new HashMap<String, Object>() {
            {
                put(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME,
                    true);
            }
        };
        when(metadataService.configAttrs(anyMapOf(String.class,
                                                  Object.class),
                                         any(Metadata.class))).thenReturn(metadataMap);

        globalsEditorService.generate(path,
                                      "test",
                                      globalsModel,
                                      "comment");

        ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        verify(ioService,
               times(1)).write(any(org.uberfire.java.nio.file.Path.class),
                               anyString(),
                               mapArgumentCaptor.capture(),
                               any());

        Map capturedMap = mapArgumentCaptor.getValue();
        assertEquals(metadataMap,
                     capturedMap);

        Object generatedAttribute = capturedMap.get(GeneratedAttributesView.GENERATED_ATTRIBUTE_NAME);
        assertNotNull(generatedAttribute);
        assertTrue(Boolean.parseBoolean(generatedAttribute.toString()));
    }

    @Test
    public void testInit() throws Exception {

        final GlobalsEditorServiceImpl service = (GlobalsEditorServiceImpl) globalsEditorService;

        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final GlobalsEditorServiceImpl service = (GlobalsEditorServiceImpl) globalsEditorService;
        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final GlobalsModel content = mock(GlobalsModel.class);
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }
}
