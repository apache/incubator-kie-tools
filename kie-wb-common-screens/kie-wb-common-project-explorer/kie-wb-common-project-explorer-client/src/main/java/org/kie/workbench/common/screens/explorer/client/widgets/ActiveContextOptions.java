/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.explorer.service.ActiveOptions;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;
import org.kie.workbench.common.screens.explorer.service.Option;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

@ApplicationScoped
public class ActiveContextOptions {

    private ActiveOptions options = new ActiveOptions();

    private Caller<ExplorerService> explorerService;

    private Event<ActiveOptionsChangedEvent> activeContextOptionsChangedEvent;
    private PlaceRequest placeRequest;

    @Inject
    public ActiveContextOptions( final Caller<ExplorerService> explorerService,
                                 final Event<ActiveOptionsChangedEvent> activeContextOptionsChangedEvent ) {
        this.explorerService = explorerService;
        this.activeContextOptionsChangedEvent = activeContextOptionsChangedEvent;
    }

    public ActiveOptions getOptions() {
        return options;
    }

    public boolean canShowTag() {
        return options.contains( Option.SHOW_TAG_FILTER );
    }

    public boolean isBusinessViewActive() {
        return options.contains( Option.BUSINESS_CONTENT );
    }

    public boolean isTechnicalViewActive() {
        return options.contains( Option.TECHNICAL_CONTENT );
    }

    public boolean isTreeNavigatorVisible() {
        return options.contains( Option.TREE_NAVIGATOR );
    }

    public void init( final PlaceRequest placeRequest,
                      final Command completeCommand ) {
        this.placeRequest = placeRequest;
        Set<Option> optionsFromModeParameter = getOptionsFromModeParameter( placeRequest.getParameter( "mode",
                                                                                                       "" ) );

        if ( optionsFromModeParameter.isEmpty() ) {
            options.addAll( getOptionsFromModeParameter( getWindowParameter( "explorer_mode" ) ) );
        } else {
            options.addAll( optionsFromModeParameter );
        }


        if ( options.isEmpty() ) {
            load( completeCommand );
        } else {
            completeCommand.execute();
        }
    }

    private Set<Option> getOptionsFromModeParameter( final String explorerMode ) {
        Set<Option> result = new HashSet<Option>();

        if ( explorerMode == null ) {
            return result;
        } else if ( explorerMode.equalsIgnoreCase( "business_tree" ) ) {
            result.add( Option.BUSINESS_CONTENT );
            result.add( Option.TREE_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "business_explorer" ) ) {
            result.add( Option.BUSINESS_CONTENT );
            result.add( Option.BREADCRUMB_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "tech_tree" ) ) {
            result.add( Option.TECHNICAL_CONTENT );
            result.add( Option.TREE_NAVIGATOR );
        } else if ( explorerMode.equalsIgnoreCase( "tech_explorer" ) ) {
            result.add( Option.TECHNICAL_CONTENT );
            result.add( Option.BREADCRUMB_NAVIGATOR );
        }

        return result;
    }

    public void load( final Command configCommand ) {
        explorerService.call( getLoadSuccessCallback( configCommand ),
                              getLoadErrorCallback( configCommand ) )
                .getLastUserOptions();
    }

    private RemoteCallback<Set<Option>> getLoadSuccessCallback( final Command configCommand ) {
        return new RemoteCallback<Set<Option>>() {
            @Override
            public void callback( Set<Option> optionsResult ) {
                if ( optionsResult != null && !optionsResult.isEmpty() ) {
                    options.clear();
                    options.addAll( optionsResult );
                } else if ( options.isEmpty() ) {
                    addDefaultOptions();
                }
                configCommand.execute();
            }
        };
    }

    private ErrorCallback<Object> getLoadErrorCallback( final Command configCommand ) {
        return new ErrorCallback<Object>() {
            @Override
            public boolean error( Object o,
                                  Throwable throwable ) {
                if ( options.isEmpty() ) {
                    addDefaultOptions();
                }
                configCommand.execute();
                return false;
            }
        };
    }

    private void addDefaultOptions() {
        options.addAll( Option.BUSINESS_CONTENT,
                        Option.BREADCRUMB_NAVIGATOR,
                        Option.EXCLUDE_HIDDEN_ITEMS );
    }

    public void activateBusinessView() {
        options.add( Option.BUSINESS_CONTENT );
        options.remove( Option.TECHNICAL_CONTENT );

        activeContextOptionsChangedEvent.fire( new ActiveOptionsChangedEvent() );
    }

    public void activateTechView() {
        options.remove( Option.BUSINESS_CONTENT );
        options.add( Option.TECHNICAL_CONTENT );

        activeContextOptionsChangedEvent.fire( new ActiveOptionsChangedEvent() );
    }

    public boolean isBreadCrumbNavigationVisible() {
        return options.contains( Option.BREADCRUMB_NAVIGATOR );
    }

    public void activateBreadCrumbNavigation() {
        options.add( Option.BREADCRUMB_NAVIGATOR );
        options.remove( Option.TREE_NAVIGATOR );

        activeContextOptionsChangedEvent.fire( new ActiveOptionsChangedEvent() );
    }

    public void activateTreeViewNavigation() {
        options.remove( Option.BREADCRUMB_NAVIGATOR );
        options.add( Option.TREE_NAVIGATOR );

        activeContextOptionsChangedEvent.fire( new ActiveOptionsChangedEvent() );
    }

    public void activateTagFiltering() {
        options.add( Option.SHOW_TAG_FILTER );

        activeContextOptionsChangedEvent.fire( new ActiveOptionsChangedEvent() );
    }

    public void disableTagFiltering() {
        options.remove( Option.SHOW_TAG_FILTER );

        activeContextOptionsChangedEvent.fire( new ActiveOptionsChangedEvent() );
    }

    public boolean areHiddenFilesVisible() {
        return options.contains( Option.INCLUDE_HIDDEN_ITEMS );
    }

    public boolean isHeaderNavigationHidden() {
        final boolean noContextNavigationOption = doWindowParametersContain( "no_context_navigation" );
        final boolean noContext = placeRequest.getParameterNames()
                .contains( "no_context" );

        return noContext || noContextNavigationOption;
    }

    protected String getWindowParameter( final String parameterName ) {
        if ( doWindowParametersContain( parameterName ) ) {
            return Window.Location.getParameterMap()
                    .get( parameterName )
                    .get( 0 )
                    .trim();
        } else {
            return "";
        }
    }

    private boolean doWindowParametersContain( final String parameterName ) {
        return Window.Location.getParameterMap()
                .containsKey( parameterName );
    }
}
