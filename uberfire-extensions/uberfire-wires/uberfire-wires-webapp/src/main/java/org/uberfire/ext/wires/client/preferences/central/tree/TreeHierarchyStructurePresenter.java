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

package org.uberfire.ext.wires.client.preferences.central.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.preferences.client.utils.PreferenceFormBeansInfo;
import org.uberfire.ext.preferences.shared.bean.BasePreference;
import org.uberfire.ext.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.ext.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.ext.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.ext.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.ext.preferences.client.event.HierarchyItemFormInitializationEvent;
import org.uberfire.ext.preferences.client.event.HierarchyItemSelectedEvent;
import org.uberfire.ext.preferences.client.event.PreferencesCentralSaveEvent;
import org.uberfire.ext.wires.client.preferences.central.form.DefaultPreferenceForm;
import org.uberfire.ext.wires.client.preferences.central.hierarchy.HierarchyItemPresenter;
import org.uberfire.ext.wires.client.preferences.central.hierarchy.HierarchyStructurePresenter;
import org.uberfire.ext.wires.client.preferences.central.hierarchy.HierarchyStructureView;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@TreeView
@Dependent
public class TreeHierarchyStructurePresenter implements HierarchyStructurePresenter {

    public interface View extends HierarchyStructureView,
                                  UberElement<TreeHierarchyStructurePresenter> {

    }

    private final View view;

    private final Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller;

    private final ManagedInstance<TreeHierarchyInternalItemPresenter> treeHierarchyInternalItemPresenterProvider;

    private final ManagedInstance<TreeHierarchyLeafItemPresenter> treeHierarchyLeafItemPresenterProvider;

    private final Event<HierarchyItemFormInitializationEvent> hierarchyItemFormInitializationEvent;

    private final PlaceManager placeManager;

    private final PreferenceBeanStore store;

    private final Event<NotificationEvent> notification;

    private final PreferenceFormBeansInfo preferenceFormBeansInfo;

    private List<HierarchyItemPresenter> hierarchyItems;

    private List<PreferenceHierarchyElement<?>> preferencesElements;

    @Inject
    public TreeHierarchyStructurePresenter( final View view,
                                            final Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller,
                                            final ManagedInstance<TreeHierarchyInternalItemPresenter> treeHierarchyInternalItemPresenterProvider,
                                            final ManagedInstance<TreeHierarchyLeafItemPresenter> treeHierarchyLeafItemPresenterProvider,
                                            final Event<HierarchyItemFormInitializationEvent> hierarchyItemFormInitializationEvent,
                                            final PlaceManager placeManager,
                                            final PreferenceBeanStore store,
                                            final Event<NotificationEvent> notification,
                                            final PreferenceFormBeansInfo preferenceFormBeansInfo ) {
        this.view = view;
        this.preferenceBeanServerStoreCaller = preferenceBeanServerStoreCaller;
        this.treeHierarchyInternalItemPresenterProvider = treeHierarchyInternalItemPresenterProvider;
        this.treeHierarchyLeafItemPresenterProvider = treeHierarchyLeafItemPresenterProvider;
        this.hierarchyItemFormInitializationEvent = hierarchyItemFormInitializationEvent;
        this.placeManager = placeManager;
        this.store = store;
        this.notification = notification;
        this.preferenceFormBeansInfo = preferenceFormBeansInfo;
    }

    @PostConstruct
    public void init() {
        final TreeHierarchyStructurePresenter presenter = this;

        preferenceBeanServerStoreCaller.call( new RemoteCallback<List<PreferenceHierarchyElement<?>>>() {
            @Override
            public void callback( final List<PreferenceHierarchyElement<?>> rootPreferences ) {
                hierarchyItems = new ArrayList<>();

                preferencesElements = rootPreferences;

                rootPreferences.forEach( rootPreference -> {
                    HierarchyItemPresenter hierarchyItem;

                    if ( rootPreference.hasChildren() ) {
                        hierarchyItem = treeHierarchyInternalItemPresenterProvider.get();
                    } else {
                        hierarchyItem = treeHierarchyLeafItemPresenterProvider.get();
                    }

                    hierarchyItem.init( rootPreference );
                    hierarchyItems.add( hierarchyItem );
                } );

                view.init( presenter );
            }
        }, ( message, throwable ) -> {
            throw new RuntimeException( throwable );
        } ).buildHierarchyStructure();
    }

    public void hierarchyItemSelectedEvent( @Observes HierarchyItemSelectedEvent hierarchyItemSelectedEvent ) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put( "id", hierarchyItemSelectedEvent.getItemId() );
        parameters.put( "title", hierarchyItemSelectedEvent.getPreference().key() );

        placeManager.goTo( new DefaultPlaceRequest( getPreferenceFormIdentifier( hierarchyItemSelectedEvent.getPreferenceClass() ), parameters ) );
        final HierarchyItemFormInitializationEvent event = new HierarchyItemFormInitializationEvent( hierarchyItemSelectedEvent.getHierarchyElement() );
        hierarchyItemFormInitializationEvent.fire( event );
    }

    public void itemSelectedEvent( @Observes PreferencesCentralSaveEvent event ) {
        List<BasePreferencePortable<? extends BasePreference<?>>> preferences = new ArrayList<>();
        for ( PreferenceHierarchyElement<?> element : preferencesElements ) {
            preferences.add( (BasePreferencePortable<? extends BasePreference<?>>) element.getPortablePreference() );
        }
        store.save( preferences,
                    () -> notification.fire( new NotificationEvent( "Preferences saved successfully!", NotificationEvent.NotificationType.SUCCESS ) ),
                    parameter -> notification.fire( new NotificationEvent( "Unexpected error while saving: " + parameter.getMessage(), NotificationEvent.NotificationType.ERROR ) ) );
    }

    public List<HierarchyItemPresenter> getHierarchyItems() {
        return hierarchyItems;
    }

    public String getPreferenceFormIdentifier( Class<?> preferenceClass ) {
        final String customForm = preferenceFormBeansInfo.getPreferenceFormFor( preferenceClass );
        return customForm != null ? customForm : DefaultPreferenceForm.IDENTIFIER;
    }

    @Override
    public View getView() {
        return view;
    }
}
