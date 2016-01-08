/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.plugin.client.widget.navigator;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.LinkedGroupItem;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.ext.plugin.client.info.PluginsInfo;
import org.uberfire.ext.plugin.event.BaseNewPlugin;
import org.uberfire.ext.plugin.event.PluginAdded;
import org.uberfire.ext.plugin.event.PluginDeleted;
import org.uberfire.ext.plugin.event.PluginRenamed;
import org.uberfire.ext.plugin.model.Activity;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.widgets.common.client.accordion.TriggerWidget;
import org.uberfire.mvp.impl.PathPlaceRequest;

import static org.uberfire.ext.plugin.type.TypeConverterUtil.*;

@Dependent
public class PluginNavList extends Composite {

    interface ViewBinder
            extends
            UiBinder<Widget, PluginNavList> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    private static final Comparator<String> PLUGIN_NAME_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare( final String o1,
                            final String o2 ) {
            return o1.compareToIgnoreCase( o2 );
        }
    };

    @UiField
    PanelGroup pluginsList;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private PluginsInfo pluginsInfo;

    private Map<String, Widget> pluginRef = new HashMap<String, Widget>();

    private final Map<PluginType, LinkedGroup> listGroups = new HashMap<PluginType, LinkedGroup>();

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
        pluginsList.setId( DOM.createUniqueId() );
    }

    public void setup( final Collection<Plugin> plugins ) {
        final Map<ClientResourceType, Set<Activity>> classified = pluginsInfo.getClassifiedPlugins( plugins );

        pluginsList.clear();

        for ( final Map.Entry<ClientResourceType, Set<Activity>> entry : classified.entrySet() ) {
            final LinkedGroup itemsNavList = new LinkedGroup();
            final PluginType type = fromResourceType( entry.getKey() );

            final PanelCollapse collapse = new PanelCollapse();

            listGroups.put( type, itemsNavList );

            //Sort Activities by Name. A TreeMap supports sorting on insertion by natural ordering of its keys
            final Map<String, Activity> activities = new TreeMap<String, Activity>( PLUGIN_NAME_COMPARATOR );
            for ( final Activity item : entry.getValue() ) {
                if ( !thereIsAlreadyAPluginWithSameName( item, activities ) ) {
                    activities.put( item.getName(), item );
                }
            }
            for ( final Activity item : activities.values() ) {
                itemsNavList.add( makeItemNavLink( item ) );
            }

            final PanelBody body = new PanelBody();

            body.add( itemsNavList );
            collapse.add( body );

            pluginsList.add( new Panel() {{
                add( new TriggerWidget( entry.getKey().getIcon(), entry.getKey().getDescription() ) {{
                    setDataToggle( Toggle.COLLAPSE );
                    setDataParent( pluginsList.getId() );
                    setDataTargetWidget( collapse );
                }} );
                add( collapse );
            }} );
        }
    }

    private boolean thereIsAlreadyAPluginWithSameName( Activity item,
                                                       Map<String, Activity> activities ) {
        final Activity activity = activities.get( item.getName() );
        return activity != null && activity instanceof Plugin;
    }

    private Widget makeItemNavLink( final Activity activity ) {

        final Widget nav;
        if ( activity instanceof Plugin ) {
            nav = new LinkedGroupItem() {{
                setText( activity.getName() );
                getElement().getStyle().setProperty( "textDecoration", "underline" );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        placeManager.goTo( new PathPlaceRequest( ( (Plugin) activity ).getPath() ).addParameter( "name", activity.getName() ) );
                    }
                } );
            }};
        } else {
            nav = new ListGroupItem() {{
                setText( activity.getName() );
            }};
        }
        pluginRef.put( activity.getName(), nav );

        return pluginRef.get( activity.getName() );
    }

    public void onPlugInAdded( @Observes final PluginAdded pluginAdded ) {
        addNewPlugin( pluginAdded );
    }

    public void addNewPlugin( final BaseNewPlugin newPlugin ) {
        //Sort Widgets by Plugin Name. A TreeMap supports sorting on insertion by natural ordering of its keys
        final Map<String, Widget> sortedNavList = new TreeMap<String, Widget>( PLUGIN_NAME_COMPARATOR );
        final LinkedGroup navList = listGroups.get( newPlugin.getPlugin().getType() );
        for ( int i = 0; i < navList.getWidgetCount(); i++ ) {
            final Widget w = navList.getWidget( i );
            for ( Map.Entry<String, Widget> e : pluginRef.entrySet() ) {
                if ( e.getValue().equals( w ) ) {
                    sortedNavList.put( e.getKey(), e.getValue() );
                }
            }
        }
        sortedNavList.put( newPlugin.getPlugin().getName(), makeItemNavLink( newPlugin.getPlugin() ) );

        navList.clear();
        for ( Widget w : sortedNavList.values() ) {
            navList.add( w );
        }
    }

    public void onPlugInRenamed( @Observes final PluginRenamed pluginRenamed ) {
        final Widget nav = pluginRef.get( pluginRenamed.getOldPluginName() );
        if ( nav != null ) {
            nav.removeFromParent();
        }
        addNewPlugin( pluginRenamed );
    }

    public void onPlugInDeleted( @Observes final PluginDeleted pluginDeleted ) {
        final Widget nav = pluginRef.get( pluginDeleted.getPluginName() );
        if ( nav != null ) {
            nav.removeFromParent();
        }
    }

}