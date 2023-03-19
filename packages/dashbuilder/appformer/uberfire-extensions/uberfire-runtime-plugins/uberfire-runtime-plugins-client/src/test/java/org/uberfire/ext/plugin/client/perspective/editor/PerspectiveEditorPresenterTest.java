/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.plugin.client.perspective.editor;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.LayoutEditorPresenter;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentGroup;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponentPalette;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorPlugin;
import org.uberfire.ext.layout.editor.client.widgets.LayoutComponentPaletteGroupProvider;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorComponentGroupProvider;
import org.uberfire.ext.plugin.client.perspective.editor.events.PerspectiveEditorFocusEvent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.PerspectiveEditorSettings;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PerspectiveEditorPresenterTest {

    @Mock
    PerspectiveEditorPresenter.View view;

    @Mock
    ObservablePath observablePath;

    @Mock
    PlaceRequest placeRequest;

    @Mock
    BasicFileMenuBuilder menuBuilder;

    @Mock
    LayoutEditorPlugin layoutEditorPlugin;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    PerspectiveEditorSettings settings;

    @Mock
    SyncBeanDef<PerspectiveEditorComponentGroupProvider> perspectiveEditorGroupBeanA;

    @Mock
    SyncBeanDef<PerspectiveEditorComponentGroupProvider> perspectiveEditorGroupBeanB;

    @Mock
    LayoutEditorPresenter layoutEditorPresenter;

    @Mock
    LayoutDragComponentPalette layoutDragComponentPalette;

    @Mock
    EventSourceMock<PerspectiveEditorFocusEvent> perspectiveEditorFocusEvent;

    @Mock
    Caller<PerspectiveServices> perspectiveServices;

    @Spy
    SyncPromises promises;

    @InjectMocks
    PerspectiveEditorPresenter presenter;

    @Captor
    private ArgumentCaptor<Collection<LayoutComponentPaletteGroupProvider>> providersCaptor;

    PerspectiveEditorComponentGroupProvider perspectiveEditorGroupA;
    PerspectiveEditorComponentGroupProvider perspectiveEditorGroupB;
    LayoutDragComponentGroup dragComponentGroupA;
    LayoutDragComponentGroup dragComponentGroupB;

    public static final String COMPONENT_GROUP_A = "A";
    public static final String COMPONENT_GROUP_B = "B";

    class PerspectiveEditorTestGroupProvider implements PerspectiveEditorComponentGroupProvider {

        private String name;
        private LayoutDragComponentGroup componentGroup;

        public PerspectiveEditorTestGroupProvider(String name, LayoutDragComponentGroup componentGroup) {
            this.name = name;
            this.componentGroup = componentGroup;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public LayoutDragComponentGroup getComponentGroup() {
            return componentGroup;
        }
    }

    @Before
    public void setUp() {
        presenter.perspectiveEditorFocusEvent = perspectiveEditorFocusEvent;

        dragComponentGroupA = new LayoutDragComponentGroup(COMPONENT_GROUP_A);
        dragComponentGroupA.addLayoutDragComponent("fd", mock(LayoutDragComponent.class));
        
        perspectiveEditorGroupA = new PerspectiveEditorTestGroupProvider(COMPONENT_GROUP_A, dragComponentGroupA);
        when(perspectiveEditorGroupBeanA.getInstance()).thenReturn(perspectiveEditorGroupA);
        
        dragComponentGroupB = new LayoutDragComponentGroup(COMPONENT_GROUP_B);
        dragComponentGroupB.addLayoutDragComponent("fd", mock(LayoutDragComponent.class));

        perspectiveEditorGroupB = new PerspectiveEditorTestGroupProvider(COMPONENT_GROUP_B, dragComponentGroupB);
        when(perspectiveEditorGroupBeanB.getInstance()).thenReturn(perspectiveEditorGroupB);

        when(beanManager.lookupBeans(PerspectiveEditorComponentGroupProvider.class))
                .thenReturn(Arrays.asList(perspectiveEditorGroupBeanB, perspectiveEditorGroupBeanA));

    }

    @Test
    public void testInitLayoutEditor() {
        presenter.onStartup(observablePath, placeRequest);

        verify(layoutEditorPlugin).init(any(), anyString(), anyString(), eq(LayoutTemplate.Style.PAGE));
        verify(layoutEditorPlugin).setPreviewEnabled(true);
        verify(layoutEditorPlugin).setElementSelectionEnabled(true);
    }

    @Test
    public void testInitDragComponentGroups() {
        presenter.onStartup(observablePath, placeRequest);

        verify(layoutDragComponentPalette).clear();

        // The component groups are grouped by name
        verify(layoutDragComponentPalette).addDraggableGroups(providersCaptor.capture());

        Collection<LayoutComponentPaletteGroupProvider> providers = providersCaptor.getValue();

        Assertions.assertThat(providers)
                .hasSize(2)
                .containsExactly(perspectiveEditorGroupA, perspectiveEditorGroupB);
    }

    @Test
    public void testTagsDisabledByDefault() {
        presenter.onStartup(observablePath, placeRequest);

        verify(menuBuilder).addSave(any(Command.class));
        verify(menuBuilder).addCopy(Mockito.<Path>any(), any(), any(Caller.class));
        verify(menuBuilder).addDelete(Mockito.<Path>any(), any(Caller.class));
        verify(menuBuilder).addDelete(Mockito.<Path>any(), any(Caller.class));
        verify(menuBuilder, never()).addNewTopLevelMenu(any());
    }

    @Test
    public void testTagsEnabled() {
        when(settings.isTagsEnabled()).thenReturn(true);
        presenter.onStartup(observablePath, placeRequest);

        verify(menuBuilder).addSave(any(Command.class));
        verify(menuBuilder).addCopy(Mockito.<Path>any(), any(), any(Caller.class));
        verify(menuBuilder).addDelete(Mockito.<Path>any(), any(Caller.class));
        verify(menuBuilder).addDelete(Mockito.<Path>any(), any(Caller.class));
        verify(menuBuilder).addNewTopLevelMenu(any());
    }

    @Test
    public void testGetContentSupplier() {

        final LayoutTemplate layoutTemplate = mock(LayoutTemplate.class);

        doReturn(layoutTemplate).when(layoutEditorPlugin).getLayout();

        final Supplier<LayoutTemplate> contentSupplier = presenter.getContentSupplier();

        assertEquals(layoutTemplate, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() {
        assertEquals(perspectiveServices, presenter.getSaveAndRenameServiceCaller());
    }

}