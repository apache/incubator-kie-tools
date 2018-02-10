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

package org.drools.workbench.screens.workitems.backend.server;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemsEditorServiceImplTest {

    @Mock
    Path path;

    @Mock
    SaveAndRenameServiceImpl<String, Metadata> saveAndRenameService;

    @InjectMocks
    WorkItemsEditorServiceImpl service = spy(new WorkItemsEditorServiceImpl());

    private String loadFile(String fileName) throws Exception {
        URL fileURL = getClass().getResource(fileName);
        return new String(Files.readAllBytes(Paths.get(fileURL.toURI())));
    }

    @Test
    public void testValidWid() throws Exception {
        assertEquals(0,
                     service.validate(path,
                                      loadFile("validWorkItemDefinition.wid")).size());
    }

    @Test
    public void testMissingCustomDataType() throws Exception {
        List<ValidationMessage> messages = service.validate(path,
                                                            loadFile("missingCustomDataTypeDefinition.wid"));
        assertEquals(1,
                     messages.size());
        assertTrue("Expected error about missing import",
                   messages.get(0).getText().contains("Error: could not resolve class: MyCustomDataType"));
    }

    @Test
    public void testSetupWorkItemDefinitionElements() throws Exception {

        final HashMap<String, String> expectedDefinitionElements = new HashMap<>();

        doReturn(expectedDefinitionElements).when(service).loadWorkItemDefinitionElements();

        service.setupWorkItemDefinitionElements();

        final Map<String, String> actualDefinitionElements = service.loadDefinitionElements().getDefinitionElements();

        verify(saveAndRenameService).init(service);
        assertEquals(expectedDefinitionElements, actualDefinitionElements);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final String content = "content";
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }
}
