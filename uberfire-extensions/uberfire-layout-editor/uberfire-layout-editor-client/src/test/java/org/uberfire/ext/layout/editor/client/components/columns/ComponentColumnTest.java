/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.columns;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponentPart;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.AbstractLayoutEditorTest;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.infra.ColumnDrop;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;
import org.uberfire.mvp.ParameterizedCommand;

public class ComponentColumnTest extends AbstractLayoutEditorTest {

    @Mock
    private Event<LockRequiredEvent> lockRequiredEvent;

    @Spy
    @InjectMocks
    private ComponentColumn componentColumn;

    @Mock
    private LayoutEditorElement parent;

    @Mock
    private LayoutComponent layoutComponent;

    @Mock
    private ParameterizedCommand<ColumnDrop> dropCommand;

    @Mock
    private ParameterizedCommand<Column> removeCommand;

    @Mock
    private Supplier<LayoutTemplate> currentLayoutTemplateSupplier;

    @Mock
    private Supplier<Boolean> lockSupplier;

    @Mock
    private ComponentColumn.View view;

    @Test
    public void assertThereIsNoGWTDepInComponentColumn() throws Exception {
        loadLayout(SINGLE_ROW_TWO_COMPONENTS_LAYOUT);
    }
    
    @Test
    public void emptyPartsWontBreakTest() throws Exception {
        LayoutTemplate layout = loadLayout(SINGLE_ROW_COMPONENT_LAYOUT);
        List<LayoutComponentPart> parts = layout.getRows().get(0)
                                               .getLayoutColumns().get(0)
                                               .getLayoutComponents().get(0)
                                               .getParts();
        assertTrue(parts.isEmpty());
    }
    
    @Test
    public void partsLoadingTest() throws Exception {
        LayoutTemplate layout = loadLayout(SINGLE_ROW_COMPONENT_LAYOUT_WITH_PARTS);
        List<LayoutComponentPart> parts = layout.getRows().get(0)
                                               .getLayoutColumns().get(0)
                                               .getLayoutComponents().get(0)
                                               .getParts();
        assertEquals(2, parts.size());
        Optional<LayoutComponentPart> part1Op = parts.stream()
                                                     .filter(p -> p.getPartId().equals("PART1"))
                                                     .findFirst();
        assertTrue(part1Op.isPresent());
        LayoutComponentPart part1 = part1Op.get();
        assertTrue(part1.getCssProperties().containsKey("PROP1"));
        assertEquals("PROP1_VAL", part1.getCssProperties().get("PROP1"));
        part1.clearCssProperties();
        assertTrue(part1.getCssProperties().isEmpty());
        part1.addCssProperty("NEW_PROP", "NEW_VALUE");
        assertEquals("NEW_VALUE", part1.getCssProperties().get("NEW_PROP"));
    }

    @Test
    public void testOnDrop() {
        componentColumn.onDrop(ColumnDrop.Orientation.UP,"this-is-a-requirement-to-firefox-html5dnd");
        verify(lockRequiredEvent,
               times(1)).fire(any(LockRequiredEvent.class));
    }

    @Test
    public void testRequiredLock() {
        componentColumn.requiredLock();
        verify(lockRequiredEvent,
               times(1)).fire(any(LockRequiredEvent.class));
    }

    @Test
    public void onDragEnd() {
        componentColumn.onDragEnd(new DragComponentEndEvent());
        verify(view, times(1)).notifyDragEnd();
        verify(lockRequiredEvent,
               times(1)).fire(any(LockRequiredEvent.class));
    }

    @Test
    public void testConfigComponentSupplier() {
        when(lockSupplier.get()).thenReturn(true);
        componentColumn.configComponent(true);
        verify(view, times(0)).hasModalConfiguration();
    }

    @Test
    public void testConfigComponent() {
        when(lockSupplier.get()).thenReturn(false);
        when(view.hasModalConfiguration()).thenReturn(true);
        componentColumn.configComponent(true);
        verify(view,times(1)).hasModalConfiguration();
    }
}
