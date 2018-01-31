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
package org.dashbuilder.client.cms.widget;

import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.plugin.client.security.PluginController;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mvp.ParameterizedCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PerspectivesExplorerTest {

    @Mock
    PerspectivesExplorer.View viewM;

    @Mock
    PerspectivePluginManager perspectivePluginManagerM;

    @Mock
    PlaceManager placeManagerM;

    @Mock
    PluginController pluginControllerM;

    @Mock
    ContentManagerI18n i18nM;

    PerspectivesExplorer perspectivesExplorer;
    Plugin a = mkPlugin("a"), b = mkPlugin("B"), c = mkPlugin("cEE");
    List<Plugin> pluginList = Arrays.asList(b, c, a);

    private Plugin mkPlugin(String name) {
        return new Plugin(name, PluginType.PERSPECTIVE, null);
    }

    @Before
    public void setUp() {
        doAnswer(invocationOnMock -> {
            ParameterizedCommand<Collection<Plugin>> callback = invocationOnMock.getArgumentAt(0, ParameterizedCommand.class);
            callback.execute(pluginList);
            return null;
        }).when(perspectivePluginManagerM).getPerspectivePlugins(any());

        perspectivesExplorer = new PerspectivesExplorer(viewM, perspectivePluginManagerM, pluginControllerM, placeManagerM, i18nM);
    }

    @Test
    public void testPerspectiveListEmpty() {
        pluginList = new ArrayList<>();
        perspectivesExplorer.show();

        verify(viewM).clear();
        verify(viewM).showEmpty(anyString());
    }

    @Test
    public void testPerspectivesAvailable() {
        when(pluginControllerM.canRead(any())).thenReturn(true);

        perspectivesExplorer.show();

        InOrder inOrder = inOrder(viewM);
        inOrder.verify(viewM).clear();
        inOrder.verify(viewM).addPerspective(eq("a"), any());
        inOrder.verify(viewM).addPerspective(eq("B"), any());
        inOrder.verify(viewM).addPerspective(eq("cEE"), any());
    }

    @Test
    public void testPerspectiveReadAccessDenied() {
        when(pluginControllerM.canRead(a)).thenReturn(true);
        when(pluginControllerM.canRead(b)).thenReturn(true);

        perspectivesExplorer.show();

        InOrder inOrder = inOrder(viewM);
        inOrder.verify(viewM).clear();
        inOrder.verify(viewM).addPerspective(eq("a"), any());
        inOrder.verify(viewM).addPerspective(eq("B"), any());
        inOrder.verify(viewM, never()).addPerspective(eq("cEE"), any());
    }
}