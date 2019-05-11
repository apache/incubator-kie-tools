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

package org.kie.workbench.common.forms.editor.client.editor.rendering;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.kie.workbench.common.forms.editor.client.editor.FormEditorContext;
import org.kie.workbench.common.forms.editor.client.editor.FormEditorHelper;
import org.kie.workbench.common.forms.editor.client.editor.events.FormEditorSyncPaletteEvent;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRenderer;
import org.kie.workbench.common.forms.editor.client.editor.properties.FieldPropertiesRendererHelper;
import org.kie.workbench.common.forms.editor.model.FormModelerContent;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors.radioGroup.type.RadioGroupFieldType;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.fields.test.TestFieldManager;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.service.shared.FieldManager;
import org.mockito.Mock;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class EditorFieldLayoutComponentTest {

    public static final String BINDING_FIRSTNAME = "firstName";

    public static final String BINDING_LASTNAME = "lastName";

    @Mock
    private ManagedInstance<EditorFieldLayoutComponent> editorFieldLayoutComponents;

    private FormModelerContent content;

    private FormEditorHelper formEditorHelper;

    @Mock
    private FieldPropertiesRenderer.FieldPropertiesRendererView fieldPropertiesRendererView;

    @Mock
    private FieldPropertiesRenderer propertiesRenderer;

    @Mock
    private FormEditorContext formEditorContext;

    @Mock
    private LayoutDragComponentHelper layoutDragComponentHelper;

    @Mock
    private EventSourceMock<FormEditorSyncPaletteEvent> syncPaletteEvent;

    @Mock
    private FieldRendererManager fieldRendererManager;

    @Mock
    private FieldRenderer fieldRenderer;

    @Mock
    private TranslationService translationService;

    @Mock
    private FormEditorRenderingContext context;

    private FieldManager fieldManager;

    @Mock
    private FormDefinition formDefinition;

    @Mock
    private FormModel formModel;

    private FieldDefinition fieldDefinition;

    private LayoutComponent layoutComponent = new LayoutComponent(EditorFieldLayoutComponent.class.getName());

    @GwtMock
    private Widget widget;

    private RenderingContext ctx;

    private FieldPropertiesRendererHelper propertiesRendererHelper;

    private EditorFieldLayoutComponent editorFieldLayoutComponent;

    @Before
    public void init() {
        fieldManager = spy(new TestFieldManager());

        when(editorFieldLayoutComponents.get()).thenAnswer(invocationOnMock -> {
            final EditorFieldLayoutComponent mocked = mock(EditorFieldLayoutComponent.class);
            return mocked;
        });

        formEditorHelper = spy(new FormEditorHelper(new TestFieldManager(),
                                                    editorFieldLayoutComponents));

        fieldDefinition = new TextBoxFieldDefinition();
        fieldDefinition.setId(EditorFieldLayoutComponent.FIELD_ID);
        fieldDefinition.setName(EditorFieldLayoutComponent.FIELD_ID);
        fieldDefinition.setBinding(EditorFieldLayoutComponent.FIELD_ID);

        when(formDefinition.getId()).thenReturn(EditorFieldLayoutComponent.FORM_ID);
        when(formDefinition.getFieldById(anyString())).thenReturn(fieldDefinition);
        when(formDefinition.getModel()).thenReturn(formModel);
        when(context.getRootForm()).thenReturn(formDefinition);

        content = new FormModelerContent();
        content.setDefinition(formDefinition);
        content.setOverview(mock(Overview.class));

        formEditorHelper.initHelper(content);

        when(formEditorHelper.getRenderingContext()).thenReturn(context);
        when(formEditorHelper.getFormField(any())).thenReturn(fieldDefinition);

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
                                                                        fieldManager,
                                                                        formEditorContext,
                                                                        syncPaletteEvent) {
            {
                fieldRendererManager = EditorFieldLayoutComponentTest.this.fieldRendererManager;
                translationService = EditorFieldLayoutComponentTest.this.translationService;
            }

            @Override
            protected FormEditorHelper getHelperInstance() {
                return formEditorHelper;
            }
        });

        editorFieldLayoutComponent.initPropertiesConfig();
        propertiesRendererHelper = spy(editorFieldLayoutComponent.getPropertiesRendererHelper());
    }

    @Test
    public void testDroppingNewField() {
        editorFieldLayoutComponent.getShowWidget(ctx);

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

        verify(editorFieldLayoutComponent,
               times(1)).init(context,
                              fieldDefinition);
        verify(fieldRenderer,
               times(1)).init(context,
                              fieldDefinition);

        verify(editorFieldLayoutComponent,
               times(1)).init(context,
                              fieldDefinition);
        verify(fieldRenderer,
               times(1)).init(context,
                              fieldDefinition);
    }

    @Test
    public void testReceivingResponsesWhenDisabled() {

        verify(editorFieldLayoutComponent,
               never()).init(context,
                             fieldDefinition);
        verify(fieldRenderer,
               never()).init(context,
                             fieldDefinition);
    }

    @Test
    public void testOpenFieldPropertiesBeforeDrop() {
        ModalConfigurationContext modalConfigurationContext = mock(ModalConfigurationContext.class);

        when(modalConfigurationContext.getComponentProperties()).thenReturn(layoutComponent.getProperties());

        editorFieldLayoutComponent.getConfigurationModal(modalConfigurationContext);

        verify(editorFieldLayoutComponent).init(context,
                                                fieldDefinition);
        verify(fieldRenderer).init(context,
                                   fieldDefinition);

        verify(propertiesRenderer).render(any());
    }

    @Test
    public void testOpenFieldProperties() {
        testDroppingNewField();

        ModalConfigurationContext modalConfigurationContext = mock(ModalConfigurationContext.class);

        when(modalConfigurationContext.getComponentProperties()).thenReturn(layoutComponent.getProperties());
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

        FieldDefinition result = propertiesRendererHelper.onFieldTypeChange(fieldDefinition,
                                                                            RadioGroupFieldType.NAME);

        verify(fieldManager).getFieldFromProvider(RadioGroupFieldType.NAME,
                                                  fieldDefinition.getFieldTypeInfo());

        assertEquals(fieldDefinition.getId(),
                     result.getId());
        assertEquals(fieldDefinition.getName(),
                     result.getName());
        assertEquals(fieldDefinition.getBinding(),
                     result.getBinding());
        assertNotEquals(fieldDefinition.getClass(),
                        result.getClass());
    }

    @Test
    public void testOnFieldBindingChange() {

        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FORM_ID,
                                                   EditorFieldLayoutComponent.FORM_ID);
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FIELD_ID,
                                                   EditorFieldLayoutComponent.FIELD_ID);

        editorFieldLayoutComponent.generateContent(ctx);

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
    public void testOnPressOkBound() {
        testOnPressOk(true,
                      false);
    }

    @Test
    public void testOnPressOkWithContext() {
        testOnPressOk(false,
                      true);
    }

    @Test
    public void testOnPressOkBoundWithContext() {
        testOnPressOk(true,
                      true);
    }

    protected void testOnPressOk(boolean bound,
                                 boolean withConfigContext) {
        FieldDefinition fieldCopy = setupFormEditorHelper();
        ModalConfigurationContext ctx = mock(ModalConfigurationContext.class);
        
       

        if (bound) {
            when(fieldCopy.getBinding()).thenReturn(BINDING_FIRSTNAME);
        }
        if (withConfigContext) {
            when(ctx.getComponentProperties()).thenReturn(mock(Map.class));
            when(ctx.getLayoutComponent()).thenReturn(new LayoutComponent());
            editorFieldLayoutComponent.getConfigurationModal(ctx);
        }

        editorFieldLayoutComponent.onPressOk(fieldCopy);

        assertSame(fieldCopy,
                   editorFieldLayoutComponent.getField());
        verify(syncPaletteEvent).fire(any());
        verify(formEditorHelper,
               bound ? times(1) : times(0)).removeAvailableField(any());
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
        doReturn(newField).when(fieldManager).getFieldFromProvider(anyString(),
                                                                   any(TypeInfo.class));

        FieldDefinition fieldCopy = propertiesRendererHelper.onFieldTypeChange(fieldDefinition,
                                                                               RadioGroupFieldType.NAME);
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FORM_ID,
                                                   EditorFieldLayoutComponent.FORM_ID);
        editorFieldLayoutComponent.setSettingValue(EditorFieldLayoutComponent.FIELD_ID,
                                                   EditorFieldLayoutComponent.FIELD_ID);

        editorFieldLayoutComponent.generateContent(ctx);
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
    public void testFieldPartsAdded() {
        Set<String> parts = Stream.of("p1","p2").collect(Collectors.toSet());
        when(fieldRenderer.getFieldParts()).thenReturn(parts);
        editorFieldLayoutComponent.generateContent(ctx);
        Set<String> expectedParts = layoutComponent.getParts().stream().map(p -> p.getPartId()).collect(Collectors.toSet());
        parts = Stream.of("p1","p3").collect(Collectors.toSet());
        when(fieldRenderer.getFieldParts()).thenReturn(parts);
        editorFieldLayoutComponent.generateContent(ctx);
        expectedParts = layoutComponent.getParts().stream().map(p -> p.getPartId()).collect(Collectors.toSet());
        assertEquals(parts, expectedParts);
    }
    
}
