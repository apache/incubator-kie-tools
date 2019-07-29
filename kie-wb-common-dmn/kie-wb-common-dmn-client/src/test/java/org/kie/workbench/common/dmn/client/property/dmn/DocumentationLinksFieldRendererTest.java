/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.property.dmn;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.client.editors.documentation.DocumentationLinksWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DocumentationLinksFieldRendererTest {

    @Mock
    private DocumentationLinksWidget widget;

    private DocumentationLinksFieldRenderer renderer;

    @Mock
    private FormRenderingContext renderingContext;

    @Mock
    private DRGElement model;

    @Mock
    private DocumentationLinksFieldDefinition field;

    @Before
    public void setup() {
        renderer = spy(new DocumentationLinksFieldRenderer(widget));
    }

    @Test
    public void testInit() {

        when(renderingContext.getModel()).thenReturn(model);
        doNothing().when(renderer).superInit(renderingContext, field);

        renderer.init(renderingContext, field);

        verify(widget).setDMNModel(model);
        verify(renderer).superInit(renderingContext, field);
    }

    @Test
    public void testGetFormGroupEditMode() {
        testGetFormGroup(RenderMode.EDIT_MODE, true);
    }

    @Test
    public void testGetFormGroupPrettyMode() {
        testGetFormGroup(RenderMode.PRETTY_MODE, false);
    }

    @Test
    public void testGetFormGroupReadOnlyMode() {
        testGetFormGroup(RenderMode.READ_ONLY_MODE, false);
    }

    public void testGetFormGroup(final RenderMode renderMode, final boolean enableWidget) {

        renderer.init(renderingContext, field);

        final DefaultFormGroup defaultFormGroup = mock(DefaultFormGroup.class);
        doReturn(defaultFormGroup).when(renderer).getFormGroupInstance();

        renderer.getFormGroup(renderMode);

        verify(widget).setEnabled(enableWidget);
        verify(defaultFormGroup).render(widget, field);
    }

    @Test
    public void testSetReadOnly() {
        renderer.setReadOnly(true);
        verify(widget).setEnabled(false);

        renderer.setReadOnly(false);
        verify(widget).setEnabled(true);
    }

    @Test
    public void testGetName() {
        assertEquals(DocumentationLinksFieldDefinition.FIELD_TYPE.getTypeName(),
                     renderer.getName());
    }

    @Test
    public void testGetSupportedCode() {
        assertEquals(DocumentationLinksFieldDefinition.FIELD_TYPE.getTypeName(),
                     renderer.getSupportedCode());
    }
}