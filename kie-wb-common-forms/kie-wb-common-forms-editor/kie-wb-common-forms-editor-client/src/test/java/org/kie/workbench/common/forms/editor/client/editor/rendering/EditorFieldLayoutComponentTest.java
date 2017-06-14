/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.rendering;

import java.util.Map;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererManager;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextRequest;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorContextResponse;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRenderer;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRendererHelper;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.service.FieldManager;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EditorFieldLayoutComponentTest {

    public static final String BINDING_FIRSTNAME = "firstName";

    public static final String BINDING_LASTNAME = "lastName";

    @Mock
    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    @Mock
    protected EventSourceMock<FormEditorContextResponse> responseEvent;

    protected FormModelerContent content;

    private FormEditorHelper formEditorHelper;

    @Mock
    protected FieldPropertiesRenderer.FieldPropertiesRendererView fieldPropertiesRendererView;

    @Mock
    protected FieldPropertiesRenderer propertiesRenderer;

    @Mock
    protected LayoutDragComponentHelper layoutDragComponentHelper;

    @Mock
    protected EventSourceMock<FormEditorContextRequest> fieldRequest;

    @Mock
    protected EventSourceMock<FormEditorSyncPaletteEvent> syncPaletteEvent;

    @Mock
    protected FieldRendererManager fieldRendererManager;

    @Mock
    protected FieldRenderer fieldRenderer;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected FormEditorRenderingContext context;

    @Mock
    protected FieldManager fieldManager;

    @Mock
    protected FormDefinition formDefinition;

    protected FieldDefinition fieldDefinition;

    private LayoutComponent layoutComponent = new LayoutComponent(EditorFieldLayoutComponent.class.getName());

    @GwtMock
    private Widget widget;

    private RenderingContext ctx;

    private FieldPropertiesRendererHelper propertiesRendererHelper;

    private EditorFieldLayoutComponent editorFieldLayoutComponent;

    @Before
    public void init() {

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> {
            final EditorFieldLayoutComponent mocked = mock(EditorFieldLayoutComponent.class);
            return mocked;
        });

        formEditorHelper = spy(new FormEditorHelper(new TestFieldManager(),
                                                    responseEvent,
                                                    editorFieldLayoutComponents));

        fieldDefinition = new TextBoxFieldDefinition();
        fieldDefinition.setId(EditorFieldLayoutComponent.FIELD_ID);
        fieldDefinition.setName(EditorFieldLayoutComponent.FIELD_ID);
        fieldDefinition.setBinding(EditorFieldLayoutComponent.FIELD_ID);

        when(formDefinition.getId()).thenReturn(EditorFieldLayoutComponent.FORM_ID);
        when(formDefinition.getFieldById(anyString())).thenReturn(fieldDefinition);
        when(context.getRootForm()).thenReturn(formDefinition);

        content = new FormModelerContent();
        content.setDefinition(formDefinition);
        content.setOverview(mock(Overview.class));

        formEditorHelper.initHelper(content);

        when(formEditorHelper.getRenderingContext()).thenReturn(context);

        layoutComponent.addProperty(EditorFieldLayoutComponent.FIELD_ID,
                                    EditorFieldLayoutComponent.FIELD_ID);
        layoutComponent.addProperty(EditorFieldLayoutComponent.FORM_ID,
                                    EditorFieldLayoutComponent.FORM_ID);

        ctx = new RenderingContext(layoutComponent,
                                   widget);
        when(fieldRendererManager.getRendererForField(any())).thenReturn(fieldRenderer);
        when(fieldRenderer.renderWidget()).thenReturn(widget);

        when(propertiesRenderer.getView()).thenReturn(fieldPropertiesRendererView);

        editorFieldLayoutComponent = spy(new EditorFieldLayoutComponent(propertiesRenderer,
                                                                        layoutDragComponentHelper,
                                                                        fieldRequest,
                                                                        fieldManager,
                                                                        syncPaletteEvent) {
            {
                fieldRendererManager = EditorFieldLayoutComponentTest.this.fieldRendererManager;
                translationService = EditorFieldLayoutComponentTest.this.translationService;
            }
        });

        editorFieldLayoutComponent.initPropertiesConfig();
        propertiesRendererHelper = spy(editorFieldLayoutComponent.getPropertiesRendererHelper());
    }

    @Test
    public void testDroppingNewField() {
        editorFieldLayoutComponent.getShowWidget(ctx);

        verify(editorFieldLayoutComponent).getEditionContext(layoutComponent.getProperties());

        verify(fieldRequest).fire(any());

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent).init(context,
                                                fieldDefinition);
        verify(fieldRenderer).init(context,
                                   fieldDefinition);
        verify(fieldRenderer).renderWidget();
    }

    @Test
    public void testRenderingExistingField() {
        testDroppingNewField();

        editorFieldLayoutComponent.getShowWidget(ctx);

        verify(editorFieldLayoutComponent,
               times(1)).getEditionContext(layoutComponent.getProperties());

        verify(fieldRequest,
               times(1)).fire(any());

        verify(editorFieldLayoutComponent,
               times(1)).init(context,
                              fieldDefinition);
        verify(fieldRenderer,
               times(1)).init(context,
                              fieldDefinition);
        verify(fieldRenderer,
               times(2)).renderWidget();
    }

    @Test
    public void testReceivingWrongContextResponses() {
        testDroppingNewField();

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse("",
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent,
               times(1)).init(context,
                              fieldDefinition);
        verify(fieldRenderer,
               times(1)).init(context,
                              fieldDefinition);

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 "",
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent,
               times(1)).init(context,
                              fieldDefinition);
        verify(fieldRenderer,
               times(1)).init(context,
                              fieldDefinition);
    }

    @Test
    public void testReceivingResponsesWhenDisabled() {
        editorFieldLayoutComponent.setDisabled(true);

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent,
               never()).init(context,
                             fieldDefinition);
        verify(fieldRenderer,
               never()).init(context,
                             fieldDefinition);
    }

    @Test
    public void testOpenFieldPropertiesBeforeDrop() {
        ModalConfigurationContext modalConfigurationContext = new ModalConfigurationContext(layoutComponent,
                                                                                            mock(Command.class),
                                                                                            mock(Command.class));

        editorFieldLayoutComponent.getConfigurationModal(modalConfigurationContext);

        verify(editorFieldLayoutComponent).getEditionContext(layoutComponent.getProperties());

        verify(fieldRequest).fire(any());

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent).init(context,
                                                fieldDefinition);
        verify(fieldRenderer).init(context,
                                   fieldDefinition);

        verify(propertiesRenderer).render(any());
    }

    @Test
    public void testOpenFieldProperties() {
        testDroppingNewField();

        ModalConfigurationContext modalConfigurationContext = new ModalConfigurationContext(layoutComponent,
                                                                                            mock(Command.class),
                                                                                            mock(Command.class));

        editorFieldLayoutComponent.getConfigurationModal(modalConfigurationContext);

        verify(propertiesRenderer).render(any());
        verify(propertiesRenderer).getView();
        verify(fieldPropertiesRendererView).getPropertiesModal();
    }

    @Test
    public void testHardCodedDragAndDropMethods() {
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FORM_ID,
                                                   formDefinition.getId());
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FIELD_ID,
                                                   fieldDefinition.getId());

        verifyDragAndDropMethods();
    }

    @Test
    public void testDragAndDropMethods() {
        testDroppingNewField();

        verifyDragAndDropMethods();
    }

    protected void verifyDragAndDropMethods() {
        assertEquals(2,
                     editorFieldLayoutComponent.getSettingsKeys().length);
        assertEquals(formDefinition.getId(),
                     editorFieldLayoutComponent.getSettingValue(EditorFieldLayoutComponent.FORM_ID));
        assertEquals(fieldDefinition.getId(),
                     editorFieldLayoutComponent.getSettingValue(EditorFieldLayoutComponent.FIELD_ID));

        Map<String, String> settings = editorFieldLayoutComponent.getMapSettings();
        assertEquals(formDefinition.getId(),
                     settings.get(EditorFieldLayoutComponent.FORM_ID));
        assertEquals(fieldDefinition.getId(),
                     settings.get(EditorFieldLayoutComponent.FIELD_ID));
    }

    @Test
    public void testOnFieldTypeChange() {

        FieldDefinition newField = mock(FieldDefinition.class);
        when(fieldManager.getDefinitionByFieldTypeName(anyString())).thenReturn(newField);
        FieldDefinition result = propertiesRendererHelper.onFieldTypeChange(fieldDefinition,
                                                                            "RadioGroup");

        verify(newField).copyFrom(fieldDefinition);
        verify(newField).setId(fieldDefinition.getId());
        verify(newField).setName(fieldDefinition.getName());
        assertSame(newField,
                   result);
    }

    @Test
    public void testOnFieldBindingChange() {

        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FORM_ID,
                                                   EditorFieldLayoutComponent.FORM_ID);
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FIELD_ID,
                                                   EditorFieldLayoutComponent.FIELD_ID);
        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));
        propertiesRendererHelper.onFieldBindingChange(fieldDefinition,
                                                      BINDING_FIRSTNAME);
        verify(formEditorHelper).switchToField(fieldDefinition,
                                               BINDING_FIRSTNAME);
    }

    @Test
    public void testOnPressOk() {
        testOnPressOk(false,
                      false);
    }

    @Test
    public void testOnPressOkBinded() {
        testOnPressOk(true,
                      false);
    }

    @Test
    public void testOnPressOkWithContext() {
        testOnPressOk(false,
                      true);
    }

    @Test
    public void testOnPressOkBindedWithContext() {
        testOnPressOk(true,
                      true);
    }

    protected void testOnPressOk(boolean binded,
                                 boolean withConfigContext) {
        FieldDefinition fieldCopy = setupFormEditorHelper();
        ModalConfigurationContext ctx = mock(ModalConfigurationContext.class);

        if (binded) {
            when(fieldCopy.getBinding()).thenReturn(BINDING_FIRSTNAME);
        }
        if (withConfigContext) {
            when(ctx.getComponentProperties()).thenReturn(mock(Map.class));
            editorFieldLayoutComponent.getConfigurationModal(ctx);
        }

        editorFieldLayoutComponent.onPressOk(fieldCopy);

        assertSame(fieldCopy,
                   editorFieldLayoutComponent.getField());
        verify(syncPaletteEvent).fire(any());
        verify(formEditorHelper,
               binded ? times(1) : times(0)).removeAvailableField(any());
        verify(ctx,
               withConfigContext ? times(1) : times(0)).configurationFinished();
    }

    @Test
    public void testHelperOnPressOk() {
        FieldDefinition fieldCopy = setupFormEditorHelper();
        propertiesRendererHelper.onPressOk(fieldCopy);
        verify(editorFieldLayoutComponent).onPressOk(fieldCopy);
    }

    protected FieldDefinition setupFormEditorHelper() {
        FieldDefinition newField = mock(FieldDefinition.class);
        when(newField.getId()).thenReturn(EditorFieldLayoutComponent.FIELD_ID);
        when(fieldManager.getDefinitionByFieldTypeName(anyString())).thenReturn(newField);
        FieldDefinition fieldCopy = propertiesRendererHelper.onFieldTypeChange(fieldDefinition,
                                                                               "RadioGroup");
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FORM_ID,
                                                   EditorFieldLayoutComponent.FORM_ID);
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FIELD_ID,
                                                   EditorFieldLayoutComponent.FIELD_ID);
        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));
        return fieldCopy;
    }

    @Test
    public void testHelperGetCurrentField() {
        FieldDefinition result = propertiesRendererHelper.getCurrentField();
        assertSame(editorFieldLayoutComponent.getField(),
                   result);
    }

    @Test
    public void testHelperOnClose() {
        propertiesRendererHelper.onClose();
        assertFalse(editorFieldLayoutComponent.showProperties);
    }

    @Test
    public void testHelperOnCloseWithContext() {
        ModalConfigurationContext ctx = mock(ModalConfigurationContext.class);
        when(ctx.getComponentProperties()).thenReturn(mock(Map.class));
        testDroppingNewField();
        editorFieldLayoutComponent.getConfigurationModal(ctx);

        propertiesRendererHelper.onClose();

        assertFalse(editorFieldLayoutComponent.showProperties);
        verify(ctx).configurationCancelled();
    }

    @Test
    public void testIsBindingChangeBothSame() {
        testIsBindingChange(BINDING_FIRSTNAME,
                            BINDING_FIRSTNAME,
                            true);
    }

    @Test
    public void testIsBindingChangeBothNull() {
        testIsBindingChange(null,
                            null,
                            false);
    }

    @Test
    public void testIsBindingChangeBothEmpty() {
        testIsBindingChange("",
                            "",
                            false);
    }

    @Test
    public void testIsBindingChangeNullEmpty() {
        testIsBindingChange(null,
                            "",
                            false);
    }

    @Test
    public void testIsBindingChangeEmptyNull() {
        testIsBindingChange("",
                            null,
                            false);
    }

    @Test
    public void testIsBindingChangeEmptyPopulated() {
        testIsBindingChange("",
                            BINDING_FIRSTNAME,
                            true);
    }

    @Test
    public void testIsBindingChangeNullPopulated() {
        testIsBindingChange(null,
                            BINDING_FIRSTNAME,
                            true);
    }

    @Test
    public void testIsBindingChangePopulatedEmpty() {
        testIsBindingChange(BINDING_FIRSTNAME,
                            "",
                            true);
    }

    @Test
    public void testIsBindingChangePopulatedNull() {
        testIsBindingChange(BINDING_FIRSTNAME,
                            null,
                            true);
    }

    @Test
    public void testIsBindingChangeBothPopulated() {
        testIsBindingChange(BINDING_FIRSTNAME,
                            BINDING_LASTNAME,
                            true);
    }

    protected void testIsBindingChange(String oldFieldBinding,
                                       String newFieldBinding,
                                       boolean expectedChange) {
        FieldDefinition oldField = new TextBoxFieldDefinition();
        oldField.setBinding(oldFieldBinding);
        FieldDefinition newField = new TextBoxFieldDefinition();
        newField.setBinding(newFieldBinding);

        boolean isChange = editorFieldLayoutComponent.isBindingChange(oldField,
                                                                      newField);
        assertEquals(expectedChange,
                     isChange);
    }
}
