/*
 *   Copyright 2015 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.header;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Input;

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
    Button selectAll;

    @UiField
    Button clearSelection;

    @UiField
    Button registerArea;

    @UiField
    Button updateStatusArea;

    @UiField
    Button refreshArea;

    @UiField
    Button startArea;

    @UiField
    Button stopArea;

    @UiField
    Button deleteArea;

    @UiField
    Icon clearFilter;

    @UiField
    Input inputFilter;

    public HeaderView() {
        initWidget( uiBinder.createAndBindUi( this ) );
        inputFilter.setPlaceholder( "Filter..." );

        inputFilter.addDomHandler( new KeyUpHandler() {
            @Override
            public void onKeyUp( KeyUpEvent event ) {
                presenter.filter( inputFilter.getValue() );
            }
        }, KeyUpEvent.getType() );

        clearFilter.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                inputFilter.setValue( "" );
                presenter.filter( "" );
            }
        } );

        registerArea.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.registerServer();
            }
        } );

        refreshArea.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.refresh();
            }
        } );

        startArea.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.start();
            }
        } );

        stopArea.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.stopContainer();
            }
        } );

        deleteArea.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.delete();
            }
        } );

        selectAll.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.selectAll();
            }
        } );

        clearSelection.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.clearSelection();
            }
        } );

        hideStartContainer();
        hideStopContainer();
        hideDeleteContainer();
    }

    @Override
    public void init( final HeaderPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void displayDeleteContainer() {
        deleteArea.setVisible( true );
    }

    @Override
    public void displayStopContainer() {
        stopArea.setVisible( true );
    }

    @Override
    public void displayStartContainer() {
        startArea.setVisible( true );
    }

    @Override
    public void displayUpdateStatus() {
        updateStatusArea.setVisible( true );
    }

    @Override
    public void hideDeleteContainer() {
        deleteArea.setVisible( false );
    }

    @Override
    public void hideStopContainer() {
        stopArea.setVisible( false );
    }

    @Override
    public void hideStartContainer() {
        startArea.setVisible( false );
    }

    @Override
    public void hideUpdateStatus() {
        updateStatusArea.setVisible( false );
    }
}