/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.server.management.client.header;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class HeaderView extends Composite
        implements HeaderPresenter.View {

    interface HeaderViewBinder
            extends
            UiBinder<Widget, HeaderView> {

    }

    private static HeaderViewBinder uiBinder = GWT.create( HeaderViewBinder.class );

    @UiField
    Element selectAll;

    @UiField
    Element clearSelection;

    @UiField
    Element registerArea;

    @UiField
    Element refreshArea;

    @UiField
    Element startArea;

    @UiField
    Element stopArea;

    @UiField
    Element deleteArea;

    @UiField
    Element clearFilter;

    @UiField
    InputElement inputFilter;

    @Inject
    private PlaceManager placeManager;

    private ParameterizedCommand<String> onFilterChange = new ParameterizedCommand<String>() {
        @Override
        public void execute( String parameter ) {
        }
    };

    private Command onSelectAll = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onClearSelection = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onRegisterServer = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onRefresh = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onDelete = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onStart = new Command() {
        @Override
        public void execute() {

        }
    };

    private Command onStop = new Command() {
        @Override
        public void execute() {

        }
    };

    public HeaderView() {
        initWidget( uiBinder.createAndBindUi( this ) );
        inputFilter.setPropertyString( "placeholder", "Filter..." );

        Event.sinkEvents( inputFilter, Event.ONKEYUP );
        Event.setEventListener( inputFilter, new EventListener() {

            @Override
            public void onBrowserEvent( Event event ) {
                onFilterChange.execute( inputFilter.getValue() );
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) clearFilter, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) clearFilter, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                clearFilter();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) registerArea, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) registerArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                registerServer();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) refreshArea, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) refreshArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                refresh();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) startArea, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) startArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                startContainer();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) stopArea, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) stopArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                stopContainer();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) deleteArea, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) deleteArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                deleteContainer();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) selectAll, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) selectAll, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                onSelectAll.execute();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) clearSelection, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) clearSelection, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                onClearSelection.execute();
            }
        } );

        startArea.getStyle().setDisplay( Style.Display.NONE );
        stopArea.getStyle().setDisplay( Style.Display.NONE );
        deleteArea.getStyle().setDisplay( Style.Display.NONE );
    }

    private void refresh() {
        onRefresh.execute();
    }

    private void registerServer() {
        onRegisterServer.execute();
    }

    private void deleteContainer() {
        onDelete.execute();
    }

    private void stopContainer() {
        onStop.execute();
    }

    private void startContainer() {
        onStart.execute();
    }

    @Override
    public void displayDeleteContainer() {
        deleteArea.getStyle().clearDisplay();
    }

    @Override
    public void displayStopContainer() {
        stopArea.getStyle().clearDisplay();
    }

    @Override
    public void displayStartContainer() {
        startArea.getStyle().clearDisplay();
    }

    @Override
    public void hideDeleteContainer() {
        deleteArea.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void hideStopContainer() {
        stopArea.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void hideStartContainer() {
        startArea.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void setOnFilterChange( final ParameterizedCommand<String> command ) {
        this.onFilterChange = command;
    }

    @Override
    public void setOnSelectAll( final Command onSelectAll ) {
        this.onSelectAll = onSelectAll;
    }

    @Override
    public void setOnClearSelection( final Command onClearSelection ) {
        this.onClearSelection = onClearSelection;
    }

    @Override
    public void setOnRegisterServer( final Command onRegisterServer ) {
        this.onRegisterServer = onRegisterServer;
    }

    @Override
    public void setOnDelete( final Command onDelete ) {
        this.onDelete = onDelete;
    }

    @Override
    public void setOnStart( final Command onStart ) {
        this.onStart = onStart;
    }

    @Override
    public void setOnStop( final Command onStop ) {
        this.onStop = onStop;
    }

    @Override
    public void setOnRefresh( final Command onRefresh ) {
        this.onRefresh = onRefresh;
    }

    @Override
    public void filter( final String content ) {
        this.inputFilter.setValue( content );
        onFilterChange.execute( "" );
    }

    @Override
    public void clearFilter() {
        inputFilter.setValue( "" );
        onFilterChange.execute( "" );
    }
}