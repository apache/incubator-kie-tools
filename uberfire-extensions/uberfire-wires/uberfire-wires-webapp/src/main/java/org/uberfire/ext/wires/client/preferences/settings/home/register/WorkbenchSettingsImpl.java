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

package org.uberfire.ext.wires.client.preferences.settings.home.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.ext.preferences.shared.bean.PreferenceRootElement;
import org.uberfire.ext.wires.client.preferences.central.PreferencesCentralPerspective;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@EntryPoint
public class WorkbenchSettingsImpl implements WorkbenchSettings {

    private Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller;

    private PlaceManager placeManager;

    private TranslationService translationService;

    private Map<String, List<SettingShortcut>> settingsByCategory;

    public WorkbenchSettingsImpl() {
    }

    @Inject
    public WorkbenchSettingsImpl( final Caller<PreferenceBeanServerStore> preferenceBeanServerStoreCaller,
                                  final PlaceManager placeManager,
                                  final TranslationService translationService ) {
        this.preferenceBeanServerStoreCaller = preferenceBeanServerStoreCaller;
        this.placeManager = placeManager;
        this.translationService = translationService;
        this.settingsByCategory = new HashMap<>();
    }

    @AfterInitialization
    public void includeAllPreferences() {
        preferenceBeanServerStoreCaller.call( new RemoteCallback<Map<String, List<PreferenceRootElement>>>() {
            @Override
            public void callback( final Map<String, List<PreferenceRootElement>> preferencesByCategory ) {
                preferencesByCategory.forEach( ( category, preferences ) -> {
                    preferences.forEach( preference -> {
                        final String title = translationService.format( preference.getBundleKey() );

                        addItem( preference.getIdentifier(),
                                 title,
                                 preference.getIconCss(),
                                 preference.getCategory(),
                                 () -> {
                                     Map<String, String> parameters = new HashMap<>();
                                     parameters.put( "identifier", preference.getIdentifier() );
                                     parameters.put( "title", title );
                                     placeManager.goTo( new DefaultPlaceRequest( PreferencesCentralPerspective.IDENTIFIER, parameters ) );
                                 } );
                    } );
                } );
            }
        }, ( message, throwable ) -> {
            throw new RuntimeException( throwable );
        } ).buildCategoryStructure();
    }

    @Override
    public void addItem( final String title,
                         final String iconCss,
                         final String category,
                         final Command command ) {
        addItem( null, title, iconCss, category, command );
    }

    private void addItem( final String identifier,
                          final String title,
                          final String iconCss,
                          final String category,
                          final Command command ) {
        if ( category == null || category.isEmpty()) {
            throw new RuntimeException( "The category must be not empty." );
        }

        List<SettingShortcut> settings = settingsByCategory.get( category );

        if ( settings == null ) {
            settings = new ArrayList<>();
            settingsByCategory.put( category, settings );
        }

        SettingShortcut setting = new SettingShortcut( identifier, title, iconCss, category, command );
        settings.add( setting );
    }

    @Override
    public Map<String, List<SettingShortcut>> getSettingsByCategory() {
        settingsByCategory.forEach( ( category, settings ) -> {
            settings.sort( ( o1, o2 ) -> o1.getTitle().compareTo( o2.getTitle() ) );
        } );

        return this.settingsByCategory;
    }
}
