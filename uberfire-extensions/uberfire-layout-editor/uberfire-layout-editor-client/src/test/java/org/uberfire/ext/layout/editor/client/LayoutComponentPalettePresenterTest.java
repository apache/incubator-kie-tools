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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.test.group1.Group1LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.layout.editor.client.test.group1.Group1LayoutDragComponent1;
import org.uberfire.ext.layout.editor.client.test.group1.Group1LayoutDragComponent2;
import org.uberfire.ext.layout.editor.client.test.group1.Group1LayoutDragComponent3;
import org.uberfire.ext.layout.editor.client.test.group2.Group2LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.layout.editor.client.test.group2.Group2LayoutDragComponent1;
import org.uberfire.ext.layout.editor.client.test.group2.Group2LayoutDragComponent2;
import org.uberfire.ext.layout.editor.client.test.group3.Group3LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.layout.editor.client.test.group3.Group3LayoutDragComponent1;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPalettePresenter;
import org.uberfire.ext.layout.editor.client.widgets.LayoutDragComponentGroupPresenter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayoutComponentPalettePresenterTest {

    public static final String DRAGGABLE_COMPONENT_NAME = "Draggable component name";

    @Mock
    private LayoutComponentPalettePresenter.View view;

    @Mock
    private LayoutDragComponentGroupPresenter.View dragComponentGroupView;

    @Mock
    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;

    private LayoutDragComponentGroupPresenter dragComponentGroupPresenter;

    private LayoutComponentPalettePresenter presenter;

    private List<String> disabledExperimentalFeatures = new ArrayList<>();

    private List<String> currentDragComponents = new ArrayList<>();

    @Before
    public void initialize() {

        when(experimentalFeaturesRegistryService.isFeatureEnabled(anyString())).thenAnswer((Answer<Boolean>) invocationOnMock -> !disabledExperimentalFeatures.contains(invocationOnMock.getArguments()[0]));

        ManagedInstance<LayoutDragComponentGroupPresenter> instance = mock(ManagedInstance.class);

        dragComponentGroupPresenter = spy(new LayoutDragComponentGroupPresenter(dragComponentGroupView));
        when(instance.get()).thenReturn(dragComponentGroupPresenter);

        doAnswer((Answer<Void>) invocationOnMock -> {
            invocationOnMock.callRealMethod();

            LayoutDragComponentGroup group = (LayoutDragComponentGroup) invocationOnMock.getArguments()[0];

            currentDragComponents.addAll(group.getComponents().keySet());

            return null;
        }).when(dragComponentGroupPresenter).init(any());

        when(dragComponentGroupView.hasComponent(anyString())).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return currentDragComponents.contains(invocationOnMock.getArguments()[0]);
            }
        });

        presenter = new LayoutComponentPalettePresenter(view, instance, experimentalFeaturesRegistryService);
    }

    @Test
    public void testInitialization() {
        verify(view).init(presenter);
    }

    @Test
    public void testAddAllDraggableGroups() {

        presenter.addDraggableGroups(Arrays.asList(new Group1LayoutComponentPaletteGroupProvider(true), new Group2LayoutComponentPaletteGroupProvider(), new Group3LayoutComponentPaletteGroupProvider()));

        verify(dragComponentGroupPresenter, times(3)).init(any());
        verify(dragComponentGroupPresenter, times(3)).getView();
        verify(dragComponentGroupView).setExpanded(true);
        verify(dragComponentGroupView, times(2)).setExpanded(false);
        verify(view, times(3)).addDraggableComponentGroup(any());

        assertEquals(3, presenter.getLayoutDragComponentGroups().size());

        assertNotNull(presenter.getLayoutDragComponentGroups().get(Group1LayoutComponentPaletteGroupProvider.ID));
        assertTrue(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent1.ID));
        assertTrue(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent2.ID));
        assertTrue(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent3.ID));

        assertNotNull(presenter.getLayoutDragComponentGroups().get(Group2LayoutComponentPaletteGroupProvider.ID));
        assertTrue(presenter.hasDraggableComponent(Group2LayoutComponentPaletteGroupProvider.ID, Group2LayoutDragComponent1.ID));
        assertTrue(presenter.hasDraggableComponent(Group2LayoutComponentPaletteGroupProvider.ID, Group2LayoutDragComponent2.ID));

        assertNotNull(presenter.getLayoutDragComponentGroups().get(Group3LayoutComponentPaletteGroupProvider.ID));
        assertTrue(presenter.hasDraggableComponent(Group3LayoutComponentPaletteGroupProvider.ID, Group3LayoutDragComponent1.ID));

        LayoutDragComponent dragComponent = mock(LayoutDragComponent.class);

        presenter.addDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, DRAGGABLE_COMPONENT_NAME, dragComponent);
        verify(dragComponentGroupPresenter).addComponent(DRAGGABLE_COMPONENT_NAME, dragComponent);

        presenter.removeDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, DRAGGABLE_COMPONENT_NAME);
        verify(dragComponentGroupPresenter).removeComponent(DRAGGABLE_COMPONENT_NAME);
    }

    @Test
    public void testAddDraggableGroupsWithExperimental() {
        disabledExperimentalFeatures.add(Group1LayoutDragComponent1.class.getName());
        disabledExperimentalFeatures.add(Group1LayoutDragComponent3.class.getName());
        disabledExperimentalFeatures.add(Group2LayoutComponentPaletteGroupProvider.class.getName());
        disabledExperimentalFeatures.add(Group3LayoutDragComponent1.class.getName());

        presenter.addDraggableGroups(Arrays.asList(new Group1LayoutComponentPaletteGroupProvider(true), new Group2LayoutComponentPaletteGroupProvider(), new Group3LayoutComponentPaletteGroupProvider()));

        verify(dragComponentGroupPresenter, times(2)).init(any());
        verify(dragComponentGroupPresenter, times(2)).getView();
        verify(dragComponentGroupView).setExpanded(true);
        verify(dragComponentGroupView).setExpanded(false);
        verify(view, times(2)).addDraggableComponentGroup(any());

        assertEquals(2, presenter.getLayoutDragComponentGroups().size());

        assertNotNull(presenter.getLayoutDragComponentGroups().get(Group1LayoutComponentPaletteGroupProvider.ID));
        assertFalse(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent1.ID));
        assertTrue(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent2.ID));
        assertFalse(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent3.ID));

        assertNull(presenter.getLayoutDragComponentGroups().get(Group2LayoutComponentPaletteGroupProvider.ID));
        assertFalse(presenter.hasDraggableComponent(Group2LayoutComponentPaletteGroupProvider.ID, Group2LayoutDragComponent1.ID));
        assertFalse(presenter.hasDraggableComponent(Group2LayoutComponentPaletteGroupProvider.ID, Group2LayoutDragComponent2.ID));

        assertNotNull(presenter.getLayoutDragComponentGroups().get(Group3LayoutComponentPaletteGroupProvider.ID));
        assertFalse(presenter.hasDraggableComponent(Group3LayoutComponentPaletteGroupProvider.ID, Group3LayoutDragComponent1.ID));
    }

    @Test
    public void testAddAndRemoveDraggableGroups() {
        testAddAllDraggableGroups();

        presenter.removeDraggableGroup(Group1LayoutComponentPaletteGroupProvider.ID);

        verify(dragComponentGroupPresenter, times(4)).getView();

        verify(view).removeDraggableComponentGroup(any());

        assertEquals(2, presenter.getLayoutDragComponentGroups().size());

        assertNull(presenter.getLayoutDragComponentGroups().get(Group1LayoutComponentPaletteGroupProvider.ID));
    }

    @Test
    public void testClearPalette() {
        testAddAllDraggableGroups();

        presenter.clear();

        verify(dragComponentGroupPresenter, times(6)).getView();

        verify(view, times(3)).removeDraggableComponentGroup(any());

        assertEquals(0, presenter.getLayoutDragComponentGroups().size());

        assertNull(presenter.getLayoutDragComponentGroups().get(Group1LayoutComponentPaletteGroupProvider.ID));
    }

    @Test
    public void testHasDraggableGroup() {
        assertFalse(presenter.hasDraggableGroup(Group1LayoutComponentPaletteGroupProvider.ID));

        testAddAllDraggableGroups();

        assertTrue(presenter.hasDraggableGroup(Group1LayoutComponentPaletteGroupProvider.ID));
    }

    @Test
    public void testHasDraggableComponent() {
        assertFalse(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent1.ID));

        testAddAllDraggableGroups();

        assertTrue(presenter.hasDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent1.ID));
    }

    @Test
    public void testRemoveComponent() {
        testAddAllDraggableGroups();

        presenter.removeDraggableComponent(Group1LayoutComponentPaletteGroupProvider.ID, Group1LayoutDragComponent1.ID);
        verify(dragComponentGroupView).setComponentVisible(Group1LayoutDragComponent1.ID, false);
        verify(dragComponentGroupView, never()).removeComponent(Group1LayoutDragComponent1.ID);
    }
}
