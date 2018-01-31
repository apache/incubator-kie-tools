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

package org.uberfire.ext.layout.editor.client;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayoutComponentPalettePresenterTest {

    public static final String DRAGGABLE_GROUP_NAME = "Draggable group name";
    public static final String DRAGGABLE_COMPONENT_NAME = "Draggable component name";

    @Mock
    private LayoutComponentPalettePresenter.View view;

    @Mock
    private LayoutDragComponentGroupPresenter.View dragComponentGroupView;

    private LayoutDragComponentGroupPresenter dragComponentGroupPresenter;

    private LayoutComponentPalettePresenter presenter;

    @Before
    public void initialize() {
        ManagedInstance<LayoutDragComponentGroupPresenter> instance = mock(ManagedInstance.class);
        dragComponentGroupPresenter = spy(new LayoutDragComponentGroupPresenter(dragComponentGroupView));
        when(instance.get()).thenReturn(dragComponentGroupPresenter);

        presenter = new LayoutComponentPalettePresenter(view, instance);
    }

    @Test
    public void testInitialization() {
        verify(view).init(presenter);
    }

    @Test
    public void testAddDraggableGroups() {
        LayoutDragComponentGroup dragGroup = new LayoutDragComponentGroup(DRAGGABLE_GROUP_NAME, true);
        presenter.addDraggableGroup(dragGroup);
        verify(dragComponentGroupPresenter).init(dragGroup);
        verify(dragComponentGroupPresenter).getView();
        verify(dragComponentGroupView).setExpanded(true);
        verify(view).addDraggableComponentGroup(any());
        assertEquals(1, presenter.getLayoutDragComponentGroups().size());
        assertNotNull(presenter.getLayoutDragComponentGroups().get(DRAGGABLE_GROUP_NAME));

        LayoutDragComponent dragComponent = mock(LayoutDragComponent.class);
        presenter.addDraggableComponent(DRAGGABLE_GROUP_NAME,
                                               DRAGGABLE_COMPONENT_NAME,
                                               dragComponent);
        verify(dragComponentGroupPresenter).addComponent(DRAGGABLE_COMPONENT_NAME,
                                                dragComponent);

        presenter.removeDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME);
        verify(dragComponentGroupPresenter).removeComponent(DRAGGABLE_COMPONENT_NAME);
    }

    @Test
    public void testAddAndRemoveDraggableGroups() {
        testAddDraggableGroups();

        presenter.removeDraggableGroup(DRAGGABLE_GROUP_NAME);
        verify(dragComponentGroupPresenter,
               times(2)).getView();
        verify(view).removeDraggableComponentGroup(any());
        assertEquals(0,
                     presenter.getLayoutDragComponentGroups().size());
        assertNull(presenter.getLayoutDragComponentGroups().get(DRAGGABLE_GROUP_NAME));
    }

    @Test
    public void testClearPalette() {
        testAddDraggableGroups();

        presenter.clear();

        verify(dragComponentGroupPresenter, times(2)).getView();
        verify(view).removeDraggableComponentGroup(any());
        assertEquals(0, presenter.getLayoutDragComponentGroups().size());
        assertNull(presenter.getLayoutDragComponentGroups().get(DRAGGABLE_GROUP_NAME));
    }

    @Test
    public void testHasDraggableGroup() {
        boolean result = presenter.hasDraggableGroup(DRAGGABLE_GROUP_NAME);
        assertFalse(result);

        testAddDraggableGroups();

        result = presenter.hasDraggableGroup(DRAGGABLE_GROUP_NAME);
        assertTrue(result);
    }

    @Test
    public void testHasDraggableComponent() {
        boolean result = presenter.hasDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME);
        assertFalse(result);

        LayoutDragComponentGroup dragGroup = new LayoutDragComponentGroup(DRAGGABLE_GROUP_NAME);
        presenter.addDraggableGroup(dragGroup);

        result = presenter.hasDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME);
        assertFalse(result);

        LayoutDragComponent dragComponent = mock(LayoutDragComponent.class);
        presenter.addDraggableComponent(DRAGGABLE_GROUP_NAME,
                                               DRAGGABLE_COMPONENT_NAME,
                                               dragComponent);
        when(dragComponentGroupPresenter.hasComponent(DRAGGABLE_COMPONENT_NAME)).thenReturn(true);

        result = presenter.hasDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME);
        assertTrue(result);
    }

    @Test
    public void testRemoveComponent() {
        LayoutDragComponentGroup dragGroup = new LayoutDragComponentGroup(DRAGGABLE_GROUP_NAME);
        LayoutDragComponent dragComponent = mock(LayoutDragComponent.class);

        // Add component
        presenter.addDraggableGroup(dragGroup);
        presenter.addDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME, dragComponent);
        verify(dragComponentGroupView, never()).setComponentVisible(anyString(), anyBoolean());

        // Add same component again
        reset(dragComponentGroupView);
        when(dragComponentGroupView.hasComponent(DRAGGABLE_COMPONENT_NAME)).thenReturn(true);
        presenter.addDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME, dragComponent);
        verify(dragComponentGroupView, never()).addComponent(anyString(), any());
        verify(dragComponentGroupView).setComponentVisible(DRAGGABLE_COMPONENT_NAME, true);

        // Remove existing component (the component is not removed but remains hidden)
        presenter.removeDraggableComponent(DRAGGABLE_GROUP_NAME, DRAGGABLE_COMPONENT_NAME);
        verify(dragComponentGroupView).setComponentVisible(DRAGGABLE_COMPONENT_NAME, false);
        verify(dragComponentGroupView, never()).removeComponent(DRAGGABLE_COMPONENT_NAME);
    }
}
