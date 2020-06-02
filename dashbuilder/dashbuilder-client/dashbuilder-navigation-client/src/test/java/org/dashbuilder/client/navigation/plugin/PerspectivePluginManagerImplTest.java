/*
 * Copyright 2017 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.navigation.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dashbuilder.navigation.workbench.NavWorkbenchCtx.perspective;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.service.PerspectivePluginServices;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.event.PluginSaved;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.category.Others;

@RunWith(MockitoJUnitRunner.class)
public class PerspectivePluginManagerImplTest {

    private static final String PERSPECTIVE_ID = "Persp1";
    private static final NavTree TEST_NAV_TREE = new NavTreeBuilder()
            .item(PERSPECTIVE_ID, "name1", "description1", true, perspective(PERSPECTIVE_ID))
            .build();
    @Mock
    private NavigationManager navigationManager;

    @Mock
    private ClientTypeRegistry clientTypeRegistry;

    @Mock
    private PerspectivePluginServices pluginServices;

    @Mock
    private Event<PerspectivePluginsChangedEvent> perspectiveChangedEvent;

    private PluginAdded pluginAddedEvent;
    private PluginSaved pluginSavedEvent;
    private PluginRenamed pluginRenamedEvent;
    private PluginDeleted pluginDeletedEvent;
    private Plugin perspectivePlugin;
    private Plugin perspectiveRenamedPlugin;
    private PerspectivePluginManagerImpl testedPluginManager;

    @Before
    public void setUp() {
        when(clientTypeRegistry.resolve(any())).thenReturn(new PerspectiveLayoutPluginResourceType(new Others()));

        perspectivePlugin = new Plugin(PERSPECTIVE_ID, PluginType.PERSPECTIVE_LAYOUT, null);
        perspectiveRenamedPlugin = new Plugin("newName", PluginType.PERSPECTIVE_LAYOUT, null);

        pluginAddedEvent = new PluginAdded(perspectivePlugin, null);
        pluginSavedEvent = new PluginSaved(perspectivePlugin, null);
        pluginRenamedEvent = new PluginRenamed(PERSPECTIVE_ID, perspectiveRenamedPlugin, null);
        pluginDeletedEvent = new PluginDeleted(perspectivePlugin, null);

        when(pluginServices.listPlugins()).thenReturn(Collections.emptyList());

        testedPluginManager = new PerspectivePluginManagerImpl(clientTypeRegistry, null, navigationManager, new CallerMock<>(pluginServices), perspectiveChangedEvent);
        testedPluginManager.getPerspectivePlugins(plugins -> {});
    }

    @Test
    public void testPluginAdded() {
        testedPluginManager.getPerspectivePlugins(plugins -> assertThat(plugins).isEmpty());

        testedPluginManager.onPlugInAdded(pluginAddedEvent);
        verify(perspectiveChangedEvent).fire(anyObject());

        testedPluginManager.getPerspectivePlugins(plugins -> assertThat(plugins).hasSize(1));
    }

    @Test
    public void testPluginSaved() {
        assertThat(testedPluginManager.existsPerspectivePlugin(PERSPECTIVE_ID)).isFalse();

        testedPluginManager.onPlugInSaved(pluginSavedEvent);
        verify(perspectiveChangedEvent, times(1)).fire(anyObject());

        assertThat(testedPluginManager.existsPerspectivePlugin(PERSPECTIVE_ID)).isTrue();
    }

    @Test
    public void testPluginRenamed() {
        NavTree tree = TEST_NAV_TREE.cloneTree();
        List<NavItem> items = tree.searchItems(NavWorkbenchCtx.perspective(PERSPECTIVE_ID));

        assertThat((items).get(0).getName()).isEqualTo("name1");
        assertThat((items).get(0).getContext()).contains("resourceId=" + PERSPECTIVE_ID);

        when(navigationManager.getNavTree()).thenReturn(tree);
        testedPluginManager.onPlugInRenamed(pluginRenamedEvent);

        assertThat(tree.searchItems(NavWorkbenchCtx.perspective(PERSPECTIVE_ID))).isEmpty();
        assertThat(tree.searchItems(NavWorkbenchCtx.perspective(perspectiveRenamedPlugin.getName())).get(0).getContext()).contains("resourceId=" + perspectiveRenamedPlugin.getName());

        ArgumentCaptor<Command> argumentCaptor = ArgumentCaptor.forClass(Command.class);

        verify(navigationManager, times(1)).saveNavTree(anyObject(), argumentCaptor.capture());
        verify(perspectiveChangedEvent).fire(anyObject());
    }

    @Test
    public void testPluginDeleted() {
        NavTree testTree = TEST_NAV_TREE.cloneTree();

        assertThat(testTree.getItemById(PERSPECTIVE_ID)).isNotNull();

        when(navigationManager.getNavTree()).thenReturn(testTree);
        testedPluginManager.onPlugInDeleted(pluginDeletedEvent);

        assertThat(testTree.getItemById(PERSPECTIVE_ID)).isNull();
        verify(navigationManager).saveNavTree(anyObject(), eq(null));
        verify(perspectiveChangedEvent).fire(anyObject());
    }
}
