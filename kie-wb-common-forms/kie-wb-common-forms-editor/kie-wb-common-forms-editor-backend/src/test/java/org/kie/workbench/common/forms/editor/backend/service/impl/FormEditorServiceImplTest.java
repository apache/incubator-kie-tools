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

package org.kie.workbench.common.forms.editor.backend.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.apache.commons.io.IOUtils;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandler;
import org.kie.workbench.common.forms.editor.service.backend.FormModelHandlerManager;
import org.kie.workbench.common.forms.editor.service.backend.SourceFormModelNotFoundException;
import org.kie.workbench.common.forms.editor.service.shared.ModuleFormFinderService;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormEditorServiceImplTest {

    private static final String COMMIT_MESSAGE = "commit message";

    private static final String FORM_NAME = "formName";

    private static final String FULL_FORM_NAME = FORM_NAME + "." + FormResourceTypeDefinition.EXTENSION;

    private static final String NEW_FORM_NAME = "newFormName";

    private static final String COMMIT_COMMENT = "a comment";

    @Mock
    private Overview overview;

    @Mock
    private Path path;

    @Mock
    private IOService ioService;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    private FieldManager fieldManager = new TestFieldManager();

    @Mock
    private FormModelHandler dataObjectFormModelHandler;

    private FormModelHandlerManager modelHandlerManager;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private KieModule module;

    private FormDefinitionSerializer formDefinitionSerializer;

    @Mock
    private ModuleFormFinderService moduleFormFinderService;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private CopyService copyService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    private FormEditorServiceImpl formEditorService;

    @Before
    public void init() {

        Instance<FormModelHandler<? extends FormModel>> instances = mock(Instance.class);

        when(dataObjectFormModelHandler.getModelType()).thenReturn(DataObjectFormModel.class);
        when(dataObjectFormModelHandler.newInstance()).thenReturn(dataObjectFormModelHandler);

        List<FormModelHandler> handlers = Arrays.asList(dataObjectFormModelHandler);

        when(instances.iterator()).then(invocationOnMock -> handlers.iterator());

        modelHandlerManager = spy(new FormModelHandlerManagerImpl(instances));

        formDefinitionSerializer = spy(new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                        new FormModelSerializer(),
                                                                        new TestMetaDataEntryManager()));

        formEditorService = spy(new FormEditorServiceImpl(ioService,
                                                          sessionInfo,
                                                          resourceOpenedEvent,
                                                          fieldManager,
                                                          modelHandlerManager,
                                                          moduleService,
                                                          formDefinitionSerializer,
                                                          moduleFormFinderService,
                                                          deleteService,
                                                          commentedOptionFactory,
                                                          renameService,
                                                          copyService));

        when(path.toURI()).thenReturn("default:///src/main/resources/test.frm");
        when(moduleService.resolveModule(any())).thenReturn(module);
    }

    @Test
    public void testCreateForm() {
        when(formDefinitionSerializer.serialize(any())).thenAnswer(this::verifyNewForm);

        FormModel formModel = mock(FormModel.class);

        Path resultPath = formEditorService.createForm(path, FULL_FORM_NAME, formModel);
        assertNotNull(resultPath);
    }

    @Test
    public void testDeleteForm() {
        formEditorService.delete(path, COMMIT_MESSAGE);

        verify(moduleService).resolveModule(path);

        verify(deleteService).delete(path, COMMIT_MESSAGE);
    }

    @Test
    public void testDeleteFormWrongProject() {
        when(moduleService.resolveModule(any())).thenReturn(null);

        formEditorService.delete(path, COMMIT_MESSAGE);

        verify(moduleService).resolveModule(path);

        verify(deleteService, never()).delete(path, COMMIT_MESSAGE);
    }

    @Test
    public void testDeleteFormWithException() {
        when(moduleService.resolveModule(any())).thenThrow(new IllegalStateException("Testing exception handling"));

        formEditorService.delete(path, COMMIT_MESSAGE);

        verify(moduleService).resolveModule(path);

        verify(deleteService, never()).delete(path, COMMIT_MESSAGE);
    }

    protected String verifyNewForm(InvocationOnMock invocationOnMock) {
        FormDefinition form = (FormDefinition) invocationOnMock.getArguments()[0];
        assertNotNull(form);
        assertNotNull(form.getId());
        assertNotNull(form.getModel());
        assertEquals(FORM_NAME, form.getName());
        assertTrue(form.getFields().isEmpty());
        assertNotNull(form.getLayoutTemplate());
        return "";
    }

    @Test
    public void testCreateFormThatExistsOnVFS() {
        try {
            when(ioService.exists(any())).thenReturn(true);
            FormModel formModel = mock(FormModel.class);
            formEditorService.createForm(path, FULL_FORM_NAME, formModel);
            fail("If form exists we shouldn't be here");
        } catch (FileAlreadyExistsException ex) {

        }
    }

    @Test
    public void testSaveAndRename() {
        testRename(true);
    }

    @Test
    public void testRenameWithoutSaving() {
        testRename(false);
    }

    private void testRename(boolean saveBeforeRenaming) {
        FormModelerContent content = mock(FormModelerContent.class);
        doReturn(path).when(formEditorService).save(any(Path.class), any(FormModelerContent.class), any(Metadata.class), anyString());

        when(content.getDefinition()).thenReturn(mock(FormDefinition.class));
        Metadata metadata = mock(Metadata.class);

        FormModelerContent resultContent = formEditorService.rename(path, NEW_FORM_NAME, COMMIT_COMMENT, saveBeforeRenaming, content, metadata);

        assertSame(content, resultContent);
        verify(formEditorService, saveBeforeRenaming ? times(1) : never()).save(eq(path), eq(content), eq(metadata), eq(COMMIT_COMMENT));
        verify(renameService).rename(eq(path), eq(NEW_FORM_NAME), eq(COMMIT_COMMENT));
    }

    @Test
    public void testCopyAndSave() {
        testCopy(true);
    }

    @Test
    public void testCopyWithoutSaving() {
        testCopy(false);
    }

    private void testCopy(boolean saveBeforeCopy) {
        FormModelerContent content = mock(FormModelerContent.class);
        doReturn(path).when(formEditorService).save(any(Path.class), any(FormModelerContent.class), any(Metadata.class), anyString());

        when(content.getDefinition()).thenReturn(mock(FormDefinition.class));
        Metadata metadata = mock(Metadata.class);

        formEditorService.copy(path, NEW_FORM_NAME, COMMIT_COMMENT, saveBeforeCopy, content, metadata);

        verify(formEditorService, saveBeforeCopy ? times(1) : never()).save(eq(path), eq(content), eq(metadata), eq(COMMIT_COMMENT));
        verify(copyService).copy(eq(path), eq(NEW_FORM_NAME), eq(COMMIT_COMMENT));
    }

    @Test
    public void testConstructContent() throws IOException, SourceFormModelNotFoundException {
        String formContent = IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream("test.frm")));

        when(ioService.readAllString(any())).thenReturn(formContent);

        FormModelerContent content = formEditorService.constructContent(path, overview);

        verify(modelHandlerManager).getFormModelHandler(any());

        verify(dataObjectFormModelHandler).init(any(), any());
        verify(dataObjectFormModelHandler).checkSourceModel();
        verify(dataObjectFormModelHandler).synchronizeFormModel();

        verify(resourceOpenedEvent).fire(any());

        assertNotNull(content);
        assertNull(content.getError());
        assertEquals(RenderMode.READ_ONLY_MODE, content.getRenderingContext().getRenderMode());
    }

    @Test
    public void testConstructContentWithCheckModelFailure() throws IOException, SourceFormModelNotFoundException {
        SourceFormModelNotFoundException exception = new SourceFormModelNotFoundException("exception", null, "exception", null, "model", null);

        doThrow(exception)
                .when(dataObjectFormModelHandler).checkSourceModel();

        String formContent = IOUtils.toString(new InputStreamReader(this.getClass().getResourceAsStream("test.frm")));

        when(ioService.readAllString(any())).thenReturn(formContent);

        FormModelerContent content = formEditorService.constructContent(path, overview);

        verify(modelHandlerManager).getFormModelHandler(any());

        verify(dataObjectFormModelHandler).init(any(), any());
        verify(dataObjectFormModelHandler).checkSourceModel();
        verify(dataObjectFormModelHandler, never()).synchronizeFormModel();

        verify(resourceOpenedEvent).fire(any());

        assertNotNull(content);
        assertNotNull(content.getError());
        assertEquals(exception.getShortKey(), content.getError().getShortKey());
        assertArrayEquals(exception.getShortKeyParams(), content.getError().getShortKeyParams());
        assertEquals(exception.getLongKey(), content.getError().getLongKey());
        assertArrayEquals(exception.getLongKeyParams(), content.getError().getLongKeyParams());
    }

    @Test
    public void testConstructContentWithUnexpectedFailure() {

        FormModelerContent content = formEditorService.constructContent(path, overview);

        verify(modelHandlerManager, never()).getFormModelHandler(any());
        verify(resourceOpenedEvent).fire(any());

        assertNotNull(content);
        assertNotNull(content.getError());
    }
}
