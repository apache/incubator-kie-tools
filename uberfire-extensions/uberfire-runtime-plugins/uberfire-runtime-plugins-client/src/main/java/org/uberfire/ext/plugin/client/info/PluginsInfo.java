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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.uberfire.client.editor.JSEditorActivity;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.client.perspective.JSWorkbenchPerspectiveActivity;
import org.uberfire.client.screen.JSWorkbenchScreenActivity;
import org.uberfire.client.splash.JSSplashScreenActivity;
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

@ApplicationScoped
public class PluginsInfo {

    private EditorPluginResourceType editorPluginResourceType;

    private PerspectiveLayoutPluginResourceType perspectiveLayoutPluginResourceType;

    private ScreenPluginResourceType screenPluginResourceType;

    private SplashPluginResourceType splashPluginResourceType;

    private DynamicMenuResourceType dynamicMenuResourceType;

    private ActivityBeansInfo activityBeansInfo;

    private ClientTypeRegistry clientTypeRegistry;

    public PluginsInfo() {
    }

    @Inject
    public PluginsInfo( EditorPluginResourceType editorPluginResourceType,
                        PerspectiveLayoutPluginResourceType perspectiveLayoutPluginResourceType,
                        ScreenPluginResourceType screenPluginResourceType,
                        SplashPluginResourceType splashPluginResourceType,
                        DynamicMenuResourceType dynamicMenuResourceType,
                        ActivityBeansInfo activityBeansInfo,
                        ClientTypeRegistry clientTypeRegistry ) {
        this.editorPluginResourceType = editorPluginResourceType;
        this.perspectiveLayoutPluginResourceType = perspectiveLayoutPluginResourceType;
        this.screenPluginResourceType = screenPluginResourceType;
        this.splashPluginResourceType = splashPluginResourceType;
        this.dynamicMenuResourceType = dynamicMenuResourceType;
        this.activityBeansInfo = activityBeansInfo;
        this.clientTypeRegistry = clientTypeRegistry;
    }

    public Set<Activity> getAllPlugins( final Collection<Plugin> plugins ) {
        Set<Activity> activities = new HashSet<Activity>();
        Collection<Set<Activity>> groupedActivities = getClassifiedPlugins( plugins ).values();

        for ( Set<Activity> groupOfActivities : groupedActivities ) {
            activities.addAll( groupOfActivities );
        }

        return activities;
    }

    public Map<ClientResourceType, Set<Activity>> getClassifiedPlugins( final Collection<Plugin> plugins ) {

        final Map<ClientResourceType, Set<Activity>> classified = new LinkedHashMap<ClientResourceType, Set<Activity>>();

        classified.put( perspectiveLayoutPluginResourceType, new HashSet<Activity>() );
        classified.put( screenPluginResourceType, new HashSet<Activity>() );
        classified.put( editorPluginResourceType, new HashSet<Activity>() );
        classified.put( splashPluginResourceType, new HashSet<Activity>() );
        classified.put( dynamicMenuResourceType, new HashSet<Activity>() );

        for ( final String screenId : activityBeansInfo.getAvailableWorkbenchScreensIds() ) {
            classified.get( screenPluginResourceType ).add( new Activity( screenId, PluginType.SCREEN ) );
        }

        for ( final String perspectiveId : activityBeansInfo.getAvailablePerspectivesIds() ) {
            classified.get( perspectiveLayoutPluginResourceType ).add( new Activity( perspectiveId, PluginType.PERSPECTIVE ) );
        }

        for ( final String editorId : activityBeansInfo.getAvailableWorkbenchEditorsIds() ) {
            classified.get( editorPluginResourceType ).add( new Activity( editorId, PluginType.EDITOR ) );
        }

        for ( final String splashId : activityBeansInfo.getAvailableSplashScreensIds() ) {
            classified.get( splashPluginResourceType ).add( new Activity( splashId, PluginType.SPLASH ) );
        }

        for ( final Plugin plugin : plugins ) {
            final ClientResourceType type = clientTypeRegistry.resolve( plugin.getPath() );
            if ( type != null ) {
                classified.get( type ).add( plugin );
            }
        }

        final Collection<SyncBeanDef<JSWorkbenchScreenActivity>> jsscreens = lookupBeans( JSWorkbenchScreenActivity.class );
        for ( final SyncBeanDef<JSWorkbenchScreenActivity> beanDef : jsscreens ) {
            classified.get( screenPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.SCREEN ) );
        }

        final Collection<SyncBeanDef<JSWorkbenchPerspectiveActivity>> jsperspectives = lookupBeans( JSWorkbenchPerspectiveActivity.class );
        for ( final SyncBeanDef<JSWorkbenchPerspectiveActivity> beanDef : jsperspectives ) {
            classified.get( perspectiveLayoutPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.PERSPECTIVE ) );
        }

        final Collection<SyncBeanDef<JSEditorActivity>> jseditors = lookupBeans( JSEditorActivity.class );
        for ( final SyncBeanDef<JSEditorActivity> beanDef : jseditors ) {
            classified.get( editorPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.EDITOR ) );
        }

        final Collection<SyncBeanDef<JSSplashScreenActivity>> jssplashes = lookupBeans( JSSplashScreenActivity.class );
        for ( final SyncBeanDef<JSSplashScreenActivity> beanDef : jssplashes ) {
            classified.get( splashPluginResourceType ).add( new Activity( beanDef.getName(), PluginType.SPLASH ) );
        }

        return classified;
    }

    <T> Collection<SyncBeanDef<T>> lookupBeans(Class<T> clazz) {
        return IOC.getBeanManager().lookupBeans( clazz );
    }
}
