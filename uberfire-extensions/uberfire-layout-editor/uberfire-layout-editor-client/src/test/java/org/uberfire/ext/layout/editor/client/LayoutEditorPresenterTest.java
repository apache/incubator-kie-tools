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
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.test.TestLayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LayoutEditorPresenterTest {

    public static final String LAYOUT_NAME = "test layout";
    public static final String EMPTY_TITLE_TEXT = "Empty title text";
    public static final String EMPTY_SUB_TITLE_TEXT = "Empty SubTitle text";
    public static final String DRAGGABLE_GROUP_NAME = "Draggable group name";
    public static final String DRAGGABLE_COMPONENT_NAME = "Draggable component name";

    @Mock
    private Container container;

    @Mock
    private LayoutEditorPresenter.View view;

    @Mock
    private LayoutDragComponentGroupPresenter dragComponentGroupPresenter;

    private LayoutTemplate testTemplate = new LayoutTemplate(LAYOUT_NAME);

    private TestLayoutEditorPresenter presenter;

    @Before
    public void initialize() {
        ManagedInstance<LayoutDragComponentGroupPresenter> instance = mock(ManagedInstance.class);

        when(instance.get()).thenReturn(dragComponentGroupPresenter);

        presenter = new TestLayoutEditorPresenter(view,
                                                  container,
                                                  instance);
    }

    @Test
    public void testInitialization() {

        verify(view).init(presenter);

        presenter.initNew();

        verify(container).getView();
        verify(view).setupContainer(any());
    }

    @Test
    public void testLoadLayout() {
        presenter.loadLayout(testTemplate,
                             EMPTY_TITLE_TEXT,
                             EMPTY_SUB_TITLE_TEXT);
        verify(container).load(testTemplate,
                               EMPTY_TITLE_TEXT,
                               EMPTY_SUB_TITLE_TEXT);
    }

    @Test
    public void testLoadEmptyLayout() {
        presenter.loadEmptyLayout(LAYOUT_NAME,
                                  EMPTY_TITLE_TEXT,
                                  EMPTY_SUB_TITLE_TEXT);
        verify(container).loadEmptyLayout(LAYOUT_NAME,
                                          LayoutTemplate.Style.FLUID,
                                          EMPTY_TITLE_TEXT,
                                          EMPTY_SUB_TITLE_TEXT);
    }

    @Test
    public void testAddDraggableGroups() {
        LayoutDragComponentGroup dragGroup = new LayoutDragComponentGroup(DRAGGABLE_GROUP_NAME);
        presenter.addDraggableComponentGroup(dragGroup);
        verify(dragComponentGroupPresenter).init(dragGroup);
        verify(dragComponentGroupPresenter).getView();
        verify(view).addDraggableComponentGroup(any());
        assertEquals(1,
                     presenter.getLayoutDragComponentGroups().size());
        assertNotNull(presenter.getLayoutDragComponentGroups().get(DRAGGABLE_GROUP_NAME));

        LayoutDragComponent dragComponent = mock(LayoutDragComponent.class);
        presenter.addDraggableComponentToGroup(DRAGGABLE_GROUP_NAME,
                                               DRAGGABLE_COMPONENT_NAME,
                                               dragComponent);
        verify(dragComponentGroupPresenter).add(DRAGGABLE_COMPONENT_NAME,
                                                dragComponent);

        presenter.removeDraggableComponentFromGroup(DRAGGABLE_GROUP_NAME,
                                                    DRAGGABLE_COMPONENT_NAME);
        verify(dragComponentGroupPresenter).removeDraggableComponentFromGroup(DRAGGABLE_COMPONENT_NAME);
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
    public void testLayoutEditorClear() {
        testAddDraggableGroups();

        presenter.clear();

        verify(container).reset();

        verify(dragComponentGroupPresenter,
               times(2)).getView();
        verify(view).removeDraggableComponentGroup(any());
        assertEquals(0,
                     presenter.getLayoutDragComponentGroups().size());
        assertNull(presenter.getLayoutDragComponentGroups().get(DRAGGABLE_GROUP_NAME));
    }
}
