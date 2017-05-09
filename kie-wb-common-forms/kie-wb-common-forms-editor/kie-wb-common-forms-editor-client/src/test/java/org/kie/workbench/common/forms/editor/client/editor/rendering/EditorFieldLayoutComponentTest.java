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
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRenderer;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textArea.type.TextAreaFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropEvent;
import org.uberfire.ext.layout.editor.client.api.ComponentRemovedEvent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EditorFieldLayoutComponentTest {

    public static String ANOTHER_BINDING = "antherField";

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
    protected EventSourceMock<ComponentDropEvent> fieldDroppedEvent;

    @Mock
    protected EventSourceMock<ComponentRemovedEvent> fieldRemovedEvent;

    @Mock
    protected FieldRendererManager fieldRendererManager;

    @Mock
    protected FieldRenderer fieldRenderer;

    @Mock
    protected TranslationService translationService;

    @Mock
    protected FormEditorRenderingContext context;

    @Mock
    protected FormDefinition formDefinition;

    protected FieldDefinition fieldDefinition;

    private LayoutComponent layoutComponent = new LayoutComponent(EditorFieldLayoutComponent.class.getName());

    @GwtMock
    private Widget widget;

    private RenderingContext ctx;

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
                                                                        fieldDroppedEvent,
                                                                        fieldRemovedEvent) {
            {
                fieldRendererManager = EditorFieldLayoutComponentTest.this.fieldRendererManager;
                translationService = EditorFieldLayoutComponentTest.this.translationService;
            }
        });
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

        verify(editorFieldLayoutComponent, times(1)).getEditionContext(layoutComponent.getProperties());

        verify(fieldRequest, times(1)).fire(any());

        verify(editorFieldLayoutComponent, times(1)).init(context,
                                                fieldDefinition);
        verify(fieldRenderer, times(1)).init(context,
                                   fieldDefinition);
        verify(fieldRenderer, times(2)).renderWidget();
    }


    @Test
    public void testReceivingWrongContextResponses() {
        testDroppingNewField();

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse("",
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent, times(1)).init(context,
                                                fieldDefinition);
        verify(fieldRenderer, times(1)).init(context,
                                   fieldDefinition);

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 "",
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent, times(1)).init(context,
                                                          fieldDefinition);
        verify(fieldRenderer, times(1)).init(context,
                                             fieldDefinition);
    }

    @Test
    public void testReceivingResponsesWhenDisabled() {
        editorFieldLayoutComponent.setDisabled(true);

        editorFieldLayoutComponent.onFieldResponse(new FormEditorContextResponse(EditorFieldLayoutComponent.FORM_ID,
                                                                                 EditorFieldLayoutComponent.FIELD_ID,
                                                                                 formEditorHelper));

        verify(editorFieldLayoutComponent, never()).init(context,
                                                          fieldDefinition);
        verify(fieldRenderer, never()).init(context,
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
    public void testSwitchToFieldType() {
        testOpenFieldProperties();

        editorFieldLayoutComponent.switchToFieldType(TextAreaFieldType.NAME);

        verify(formEditorHelper).switchToFieldType(fieldDefinition, TextAreaFieldType.NAME);
        verify(propertiesRenderer, times(2)).render(any());
        verify(fieldRendererManager,
               times(2)).getRendererForField(any());
        verify(fieldRenderer,
               times(2)).renderWidget();
    }

    @Test
    public void testSwitchToDifferentField() {
        testOpenFieldProperties();

        editorFieldLayoutComponent.switchToField(ANOTHER_BINDING);

        verify(formEditorHelper).switchToField(fieldDefinition, ANOTHER_BINDING);

        verify(fieldRemovedEvent).fire(any());
        verify(fieldDroppedEvent).fire(any());
        verify(propertiesRenderer, times(2)).render(any());

        verify(fieldRenderer, times(2)).init(any(), any());
        verify(fieldRenderer,
               times(2)).renderWidget();
    }

    @Test
    public void testSwitchToSameField() {
        testOpenFieldProperties();

        editorFieldLayoutComponent.switchToField(fieldDefinition.getBinding());

        verify(formEditorHelper, never()).switchToField(any(), anyString());

        verify(fieldRemovedEvent, never()).fire(any());
        verify(fieldDroppedEvent,never()).fire(any());
        verify(propertiesRenderer, times(1)).render(any());

        verify(fieldRenderer, times(1)).init(any(), any());
        verify(fieldRenderer,
               times(1)).renderWidget();
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
        assertEquals(2, editorFieldLayoutComponent.getSettingsKeys().length);
        assertEquals(formDefinition.getId(), editorFieldLayoutComponent.getSettingValue(EditorFieldLayoutComponent.FORM_ID));
        assertEquals(fieldDefinition.getId(), editorFieldLayoutComponent.getSettingValue(EditorFieldLayoutComponent.FIELD_ID));

        Map<String, String> settings = editorFieldLayoutComponent.getMapSettings();
        assertEquals(formDefinition.getId(), settings.get(EditorFieldLayoutComponent.FORM_ID));
        assertEquals(fieldDefinition.getId(), settings.get(EditorFieldLayoutComponent.FIELD_ID));
    }
}
