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

@Dependent
public class HeaderView extends Composite
        implements HeaderPresenter.View {

    private HeaderPresenter presenter;

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
    Element updateStatusArea;

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

    public HeaderView() {
        initWidget( uiBinder.createAndBindUi( this ) );
        inputFilter.setPropertyString( "placeholder", "Filter..." );

        Event.sinkEvents( inputFilter, Event.ONKEYUP );
        Event.setEventListener( inputFilter, new EventListener() {

            @Override
            public void onBrowserEvent( Event event ) {
                presenter.filter( inputFilter.getValue() );
            }
        } );

        DOM.sinkEvents( clearFilter, Event.ONCLICK );
        DOM.setEventListener( clearFilter, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                inputFilter.setValue( "" );
                presenter.filter( "" );
            }
        } );

        DOM.sinkEvents( registerArea, Event.ONCLICK );
        DOM.setEventListener( registerArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.registerServer();
            }
        } );

        DOM.sinkEvents( updateStatusArea, Event.ONCLICK );
        DOM.setEventListener( updateStatusArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.updateServerStatus();
            }
        } );

        DOM.sinkEvents( refreshArea, Event.ONCLICK );
        DOM.setEventListener( refreshArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.refresh();
            }
        } );

        DOM.sinkEvents( startArea, Event.ONCLICK );
        DOM.setEventListener( startArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.start();
            }
        } );

        DOM.sinkEvents( stopArea, Event.ONCLICK );
        DOM.setEventListener( stopArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.stopContainer();
            }
        } );

        DOM.sinkEvents( deleteArea, Event.ONCLICK );
        DOM.setEventListener( deleteArea, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.delete();
            }
        } );

        DOM.sinkEvents( selectAll, Event.ONCLICK );
        DOM.setEventListener( selectAll, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.selectAll();
            }
        } );

        DOM.sinkEvents( clearSelection, Event.ONCLICK );
        DOM.setEventListener( clearSelection, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                presenter.clearSelection();
            }
        } );

        startArea.getStyle().setDisplay( Style.Display.NONE );
        stopArea.getStyle().setDisplay( Style.Display.NONE );
        deleteArea.getStyle().setDisplay( Style.Display.NONE );
        updateStatusArea.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void init( final HeaderPresenter presenter ) {
        this.presenter = presenter;
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
    public void displayUpdateStatus() {
        updateStatusArea.getStyle().clearDisplay();
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
    public void hideUpdateStatus() {
        updateStatusArea.getStyle().setDisplay( Style.Display.NONE );
    }
}