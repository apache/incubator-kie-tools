/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.generator;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.editor.*;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayoutGeneratorTest {

    @Mock
    private HTMLElement containerElement;

    @Mock
    private DOMTokenList containerClassList;

    @Mock
    private HTMLElement rowElement;

    @Mock
    private DOMTokenList rowClassList;

    @Mock
    private HTMLElement columnElement;

    @Mock
    private DOMTokenList columnClassList;

    @Mock
    private IsWidget componentWidget;

    private LayoutGeneratorDriver driver = new LayoutGeneratorDriver() {

        @Override
        public HTMLElement createContainer() {
            return containerElement;
        }

        @Override
        public HTMLElement createRow(LayoutRow layoutRow) {
            return rowElement;
        }

        @Override
        public HTMLElement createColumn(LayoutColumn layoutColumn) {
            return columnElement;
        }

        @Override
        public IsWidget createComponent(HTMLElement column, LayoutComponent layoutComponent) {
            return componentWidget;
        }
    };

    private LayoutGenerator generator = new AbstractLayoutGenerator() {

        @Override
        protected void generateComponents(LayoutTemplate layoutTemplate,
                LayoutInstance layoutInstance,
                LayoutGeneratorDriver driver,
                LayoutColumn layoutColumn,
                HTMLElement column) {
        }

        @Override
        public LayoutInstance build(LayoutTemplate layoutTemplate) {
            return super.build(layoutTemplate, driver);
        }
    };

    private LayoutTemplate template = new LayoutTemplate("layout");
    private LayoutRow row1 = new LayoutRow();
    private LayoutColumn column1 = new LayoutColumn("12");
    private LayoutComponent component1 = new LayoutComponent("");

    @Before
    public void initialize() {
        component1.addPartIfAbsent("PART");
        component1.addPartProperty("PART", "PROP", "PROP_VALUE");
        column1.add(component1);
        row1.add(column1);
        template.addRow(row1);

        when(containerElement.getClassList()).thenReturn(containerClassList);
        when(rowElement.getClassList()).thenReturn(rowClassList);
        when(columnElement.getClassList()).thenReturn(columnClassList);
    }

    @Test
    public void testContainerGeneration() {
        generator.build(template);

        verify(containerElement).setId(AbstractLayoutGenerator.CONTAINER_ID);
        
    }
}
