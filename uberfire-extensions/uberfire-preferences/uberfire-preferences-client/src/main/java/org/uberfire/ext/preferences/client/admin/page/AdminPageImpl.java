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
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@ApplicationScoped
public class AdminPageImpl implements AdminPage {

    private PlaceManager placeManager;

    private Map<String, List<AdminTool>> toolsByCategory;

    public AdminPageImpl() {
        this( null );
    }

    @Inject
    public AdminPageImpl( final PlaceManager placeManager ) {
        this.placeManager = placeManager;

        this.toolsByCategory = new HashMap<>();
    }

    @Override
    public void addTool( final String title,
                         final String iconCss,
                         final String category,
                         final Command command,
                         final ParameterizedCommand<ParameterizedCommand<Integer>> counterCommand ) {
        if ( category == null || category.isEmpty() ) {
            throw new RuntimeException( "The category must be not empty." );
        }

        List<AdminTool> tools = toolsByCategory.get( category );

        if ( tools == null ) {
            tools = new ArrayList<>();
            toolsByCategory.put( category, tools );
        }

        AdminTool tool = new AdminTool( title, iconCss, category, command, counterCommand );
        tools.add( tool );
    }

    @Override
    public void addTool( final String title,
                         final String iconCss,
                         final String category,
                         final Command command ) {
        addTool( title, iconCss, category, command, null );
    }

    @Override
    public void addPreference( final String identifier,
                               final String title,
                               final String iconCss,
                               final String category ) {
        addTool( title,
                 iconCss,
                 category,
                 () -> {
                     Map<String, String> parameters = new HashMap<>();
                     parameters.put( "identifier", identifier );
                     parameters.put( "title", title );
                     placeManager.goTo( new DefaultPlaceRequest( "PreferencesCentralPerspective", parameters ) );
                 } );
    }

    @Override
    public Map<String, List<AdminTool>> getToolsByCategory() {
        toolsByCategory.forEach( ( category, tools ) -> {
            tools.sort( ( o1, o2 ) -> o1.getTitle().compareTo( o2.getTitle() ) );
        } );

        return this.toolsByCategory;
    }
}
