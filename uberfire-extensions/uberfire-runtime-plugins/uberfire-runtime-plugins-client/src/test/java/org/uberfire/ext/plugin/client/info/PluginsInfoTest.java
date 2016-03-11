/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.plugin.client.info;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.client.workbench.type.ClientTypeRegistry;
import org.uberfire.ext.plugin.client.type.DynamicMenuResourceType;
import org.uberfire.ext.plugin.client.type.EditorPluginResourceType;
import org.uberfire.ext.plugin.client.type.PerspectiveLayoutPluginResourceType;
import org.uberfire.ext.plugin.client.type.ScreenPluginResourceType;
import org.uberfire.ext.plugin.client.type.SplashPluginResourceType;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PluginsInfoTest {

    private PluginsInfo pluginsInfo;

    private EditorPluginResourceType editorPluginResourceType;
    private PerspectiveLayoutPluginResourceType perspectiveLayoutPluginResourceType;
    private ScreenPluginResourceType screenPluginResourceType;
    private SplashPluginResourceType splashPluginResourceType;
    private DynamicMenuResourceType dynamicMenuResourceType;
    private ActivityBeansInfo activityBeansInfo;
    private ClientTypeRegistry clientTypeRegistry;

    @Before
    public void setup() {

        editorPluginResourceType = mock( EditorPluginResourceType.class );
        perspectiveLayoutPluginResourceType = mock( PerspectiveLayoutPluginResourceType.class );
        screenPluginResourceType = mock( ScreenPluginResourceType.class );
        splashPluginResourceType = mock( SplashPluginResourceType.class );
        dynamicMenuResourceType = mock( DynamicMenuResourceType.class );
        activityBeansInfo = mock( ActivityBeansInfo.class );
        clientTypeRegistry = mock( ClientTypeRegistry.class );

        when( activityBeansInfo.getAvailableWorkbenchEditorsIds() ).thenReturn( Arrays.asList( new String[]{ "editorId1" } ) );
        when( activityBeansInfo.getAvailablePerspectivesIds() ).thenReturn( Arrays.asList( new String[]{ "perspectiveId1", "perspectiveId2" } ) );
        when( activityBeansInfo.getAvailableWorkbenchScreensIds() ).thenReturn( Arrays.asList( new String[]{ "screenId1", "screenId2", "screenId3" } ) );
        when( activityBeansInfo.getAvailableSplashScreensIds() ).thenReturn( Arrays.asList( new String[]{ "splashScreenId1", "splashScreenId2", "splashScreenId3", "splashScreenId4" } ) );

        when( clientTypeRegistry.resolve( any( Path.class ) ) ).thenReturn( dynamicMenuResourceType );

        pluginsInfo = spy( new PluginsInfo( editorPluginResourceType, perspectiveLayoutPluginResourceType, screenPluginResourceType, splashPluginResourceType, dynamicMenuResourceType, activityBeansInfo, clientTypeRegistry ) );
        doReturn( Collections.emptyList() ).when( pluginsInfo ).lookupBeans( any( Class.class ) );
    }

    @Test
    public void getAllPluginsTest() {
        Set<Activity> allPlugins = pluginsInfo.getAllPlugins( getPlugins() );

        assertEquals( 15, allPlugins.size() );
    }

    @Test
    public void getClassifiedPluginsTest() {
        final Map<ClientResourceType, Set<Activity>> classifiedPlugins = pluginsInfo.getClassifiedPlugins( getPlugins() );

        assertEquals( 5, classifiedPlugins.keySet().size() );
        assertEquals( 1, classifiedPlugins.get( editorPluginResourceType ).size() );
        assertEquals( 2, classifiedPlugins.get( perspectiveLayoutPluginResourceType ).size() );
        assertEquals( 3, classifiedPlugins.get( screenPluginResourceType ).size() );
        assertEquals( 4, classifiedPlugins.get( splashPluginResourceType ).size() );
        assertEquals( 5, classifiedPlugins.get( dynamicMenuResourceType ).size() );
    }

    private Set<Plugin> getPlugins() {
        Set<Plugin> plugins = new HashSet<Plugin>();
        plugins.add( new Plugin( "existingPerspectiveLayout", PluginType.PERSPECTIVE_LAYOUT, PathFactory.newPath( "test1", "/tmp/test1" ) ) );
        plugins.add( new Plugin( "existingScreen", PluginType.SCREEN, PathFactory.newPath( "test2", "/tmp/test2" ) ) );
        plugins.add( new Plugin( "existingEditor", PluginType.EDITOR, PathFactory.newPath( "test3", "/tmp/test3" ) ) );
        plugins.add( new Plugin( "existingSplashScreen", PluginType.SPLASH, PathFactory.newPath( "test4", "/tmp/test4" ) ) );
        plugins.add( new Plugin( "existingDynamicMenu", PluginType.DYNAMIC_MENU, PathFactory.newPath( "test5", "/tmp/test5" ) ) );

        return plugins;
    }
}
