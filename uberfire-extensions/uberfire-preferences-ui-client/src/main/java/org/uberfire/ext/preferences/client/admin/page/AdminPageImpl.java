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

package org.uberfire.ext.preferences.client.admin.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.preferences.client.central.PreferencesCentralPerspective;
import org.uberfire.ext.preferences.client.event.PreferencesCentralInitializationEvent;
import org.uberfire.preferences.shared.impl.PreferenceScopeResolutionStrategyInfo;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class AdminPageImpl implements AdminPage {

    private PlaceManager placeManager;

    private Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent;

    private Map<String, String> screenTitleByIdentifier;

    private Map<String, Map<String, List<AdminTool>>> toolsByCategoryByScreen;

    private String defaultScreen;

    public AdminPageImpl() {
        this( null, null );
    }

    @Inject
    public AdminPageImpl( final PlaceManager placeManager,
                          final Event<PreferencesCentralInitializationEvent> preferencesCentralInitializationEvent ) {
        this.placeManager = placeManager;
        this.preferencesCentralInitializationEvent = preferencesCentralInitializationEvent;
        this.toolsByCategoryByScreen = new HashMap<>();
        this.screenTitleByIdentifier = new HashMap<>();
    }

    @Override
    public void addScreen( final String identifier,
                           final String title ) {
        if ( identifier == null || identifier.isEmpty() ) {
            throw new RuntimeException( "The screen identifier must be not empty." );
        }

        screenTitleByIdentifier.put( identifier, title );
        toolsByCategoryByScreen.put( identifier, new LinkedHashMap<>() );
    }

    @Override
    public void addTool( final String screen,
                         final String title,
                         final String iconCss,
                         final String category,
                         final Command command,
                         final ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand ) {
        if ( screen == null || screen.isEmpty() ) {
            throw new RuntimeException( "The screen identifier must be not empty." );
        }

        if ( screenTitleByIdentifier.get( screen ) == null ) {
            throw new RuntimeException( "The screen must be added before it is used." );
        }

        if ( category == null || category.isEmpty() ) {
            throw new RuntimeException( "The category identifier must be not empty." );
        }

        Map<String, List<AdminTool>> toolsByCategory = toolsByCategoryByScreen.get( screen );
        List<AdminTool> tools = toolsByCategory.get( category );

        if ( tools == null ) {
            tools = new ArrayList<>();
            toolsByCategory.put( category, tools );
        }

        AdminTool tool = new AdminTool( title, iconCss, category, command, counterCommand );
        tools.add( tool );
    }

    @Override
    public void addTool( final String screen,
                         final String title,
                         final String iconCss,
                         final String category,
                         final Command command ) {
        addTool( screen, title, iconCss, category, command, null );
    }

    @Override
    public void addPreference( final String screen,
                               final String identifier,
                               final String title,
                               final String iconCss,
                               final String category ) {
        addPreference( screen, identifier, title, iconCss, category, null );
    }

    @Override
    public void addPreference( final String screen,
                               final String identifier,
                               final String title,
                               final String iconCss,
                               final String category,
                               final Supplier<PreferenceScopeResolutionStrategyInfo> customScopeResolutionStrategySupplier ) {

        addTool( screen,
                 title,
                 iconCss,
                 category,
                 () -> {
                     final PreferenceScopeResolutionStrategyInfo customScopeResolutionStrategy = customScopeResolutionStrategySupplier != null ? customScopeResolutionStrategySupplier.get() : null;
                     final PreferencesCentralInitializationEvent initEvent = new PreferencesCentralInitializationEvent( identifier,
                                                                                                                        customScopeResolutionStrategy );
                     placeManager.goTo( new DefaultPlaceRequest( PreferencesCentralPerspective.IDENTIFIER ) );
                     preferencesCentralInitializationEvent.fire( initEvent );
                 } );
    }

    @Override
    public Map<String, List<AdminTool>> getToolsByCategory( final String screen ) {
        return toolsByCategoryByScreen.get( screen );
    }

    @Override
    public String getScreenTitle( final String screen ) {
        return screenTitleByIdentifier.get( screen );
    }

    @Override
    public void setDefaultScreen( final String defaultScreen ) {
        this.defaultScreen = defaultScreen;
    }

    @Override
    public String getDefaultScreen() {
        return defaultScreen;
    }
}
