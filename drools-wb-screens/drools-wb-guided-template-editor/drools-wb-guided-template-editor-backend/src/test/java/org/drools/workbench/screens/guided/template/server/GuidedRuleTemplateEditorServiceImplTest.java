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

package org.drools.workbench.screens.guided.template.server;

import java.util.HashMap;

import javax.enterprise.event.Event;

import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.drools.workbench.screens.guided.template.model.GuidedTemplateEditorContent;
import org.drools.workbench.screens.guided.template.type.GuidedRuleTemplateResourceTypeDefinition;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedRuleTemplateEditorServiceImplTest {

    @Mock
    IOService ioService;

    @Mock
    private CopyService copyService;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Mock
    private DataModelService dataModelService;

    @Mock
    private GenericValidator genericValidator;

    @Mock
    private GuidedRuleTemplateResourceTypeDefinition resourceTypeDefinition;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private SessionInfo sessionInfo;
    @InjectMocks
    GuidedRuleTemplateEditorServiceImpl service = spy(new GuidedRuleTemplateEditorServiceImpl(sessionInfo));
    @Mock
    private SaveAndRenameServiceImpl<TemplateModel, Metadata> saveAndRenameService;

    @Test
    public void checkConstructContentPopulateProjectCollectionTypes() {
        final Path path = mock(Path.class);
        final Overview overview = mock(Overview.class);
        final PackageDataModelOracle oracle = mock(PackageDataModelOracle.class);
        when(path.toURI()).thenReturn("file://project/src/main/resources/mypackage/rule.template");
        when(dataModelService.getDataModel(any())).thenReturn(oracle);
        when(oracle.getPackageGlobals()).thenReturn(new HashMap<String, String>() {{
            put("number",
                "java.lang.Number");
            put("collection",
                "java.util.Collection");
        }});
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

        final GuidedTemplateEditorContent content = service.constructContent(path,
                                                                             overview);
        assertEquals(3,
                     content.getDataModel().getCollectionTypes().size());
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.Collection"));
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.List"));
        assertTrue(content.getDataModel().getCollectionTypes().containsKey("java.util.Set"));
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
        final TemplateModel content = mock(TemplateModel.class);
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }
}
