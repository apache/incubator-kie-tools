/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.TextBoxFieldRenderer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FieldLayoutComponentTest {

    private static String NAME = "textBox";

    private static String PART_1 = "part1";
    private static String PART_2 = "part2";
    private static String PART_3 = "part3";

    private static String UNEXISTING_PART = "unexisting_part";

    @GwtMock
    private FlowPanel content;

    @Mock
    private FieldRendererManager fieldRendererManager;

    @Mock
    private TranslationService translationService;

    @Mock
    private FormDefinition formDefinition;

    @Mock
    private FormRenderingContext context;

    @Mock
    private TextBoxFieldRenderer renderer;

    private Set<String> parts = new TreeSet<>();

    private TextBoxFieldDefinition field;

    private FieldLayoutComponent component;

    @Before
    public void init() {

        when(context.getRootForm()).thenReturn(formDefinition);
        when(fieldRendererManager.getRendererForField(any())).thenReturn(renderer);

        parts.add(PART_1);
        parts.add(PART_2);
        parts.add(PART_3);

        when(renderer.renderWidget()).thenReturn(mock(IsWidget.class));
        when(renderer.getFieldParts()).thenReturn(parts);
        when(renderer.getFieldPartWidget(Mockito.<String>any())).thenAnswer(invocation -> {
            String partId = invocation.getArguments()[0].toString();

            if (parts.contains(partId)) {
                return mock(Widget.class);
            }

            return null;
        });

        component = new FieldLayoutComponent(fieldRendererManager, translationService) {
            {
                this.content = FieldLayoutComponentTest.this.content;
            }
        };

        field = spy(new TextBoxFieldDefinition());

        field.setName(NAME);
        field.setBinding(NAME);
        field.setPlaceHolder(NAME);

        component.init(context, field);

        verify(fieldRendererManager).getRendererForField(eq(field));
        verify(renderer).init(eq(context), eq(field));
    }

    @Test
    public void testBasicChecks() {
        component.getFieldId();
        verify(field).getId();

        assertSame(field, component.getField());

        component.getFormId();
        verify(context).getRootForm();
        verify(formDefinition).getId();

        assertSame(renderer, component.getFieldRenderer());

        component.destroy();

        verify(content).clear();
    }

    @Test
    public void testGetShowWidget() {

        component.getShowWidget(mock(RenderingContext.class));

        verify(renderer).renderWidget();
        verify(content).clear();
        verify(content).add(any(IsWidget.class));
    }

    @Test
    public void testAddExistingComponentPart() {
        LayoutComponent layoutComponent = new LayoutComponent();

        component.addComponentParts(layoutComponent);

        Assertions.assertThat(layoutComponent.getParts())
                .isNotEmpty()
                .hasSize(parts.size())
                .anyMatch(part -> part.getPartId().equals(PART_1))
                .anyMatch(part -> part.getPartId().equals(PART_2))
                .anyMatch(part -> part.getPartId().equals(PART_3));
    }

    @Test
    public void testGetContentPart() {
        RenderingContext renderingContext = mock(RenderingContext.class);

        Assertions.assertThat(component.getContentPart(PART_1, renderingContext))
                .isNotNull()
                .isPresent();

        Assertions.assertThat(component.getContentPart(PART_2, renderingContext))
                .isNotNull()
                .isPresent();

        Assertions.assertThat(component.getContentPart(PART_3, renderingContext))
                .isNotNull()
                .isPresent();

        Assertions.assertThat(component.getContentPart(UNEXISTING_PART, renderingContext))
                .isNotNull()
                .isNotPresent();
    }
}
