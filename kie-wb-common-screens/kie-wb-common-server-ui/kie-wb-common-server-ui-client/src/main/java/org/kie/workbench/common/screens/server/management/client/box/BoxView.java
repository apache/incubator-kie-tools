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

package org.kie.workbench.common.screens.server.management.client.box;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.ListGroupItem;
import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Small;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.kie.workbench.common.screens.server.management.client.resources.ContainerResources;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;

import static org.kie.workbench.common.screens.server.management.client.util.ContainerStatusUtil.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class BoxView extends ListGroupItem implements BoxPresenter.View {

    interface ContainerViewBinder
            extends
            UiBinder<Widget, BoxView> {

    }

    private static ContainerViewBinder uiBinder = GWT.create( ContainerViewBinder.class );

    @UiField
    Div box;

    @UiField
    Icon notSelected;

    @UiField
    Icon selected;

    @UiField
    Icon status;

    @UiField
    Anchor containerName;

    @UiField
    Small complement;

    @UiField
    UnorderedList listOfServices;

    @UiField
    Button addAction;

    @UiField
    Button openAction;

    private BoxPresenter presenter;

    public BoxView() {
        add( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( BoxPresenter presenter ) {
        this.presenter = checkNotNull( "presenter", presenter );
    }

    @Override
    public void setup( final BoxType type ) {
        selected.setTitle( "Selected" );
        notSelected.setTitle( "Not selected" );
        addAction.setTitle( "New Container" );
        openAction.setTitle( "Open" );

        selected.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                onDeselect();
            }
        } );

        notSelected.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                onSelect();
            }
        } );

        addAction.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                addAction();
            }
        } );

        openAction.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                openAction();
            }
        } );

        if ( type.equals( BoxType.CONTAINER ) ) {
            box.addStyleName( ContainerResources.INSTANCE.CSS().childContainer() );
        } else {
            box.removeStyleName( ContainerResources.INSTANCE.CSS().childContainer() );
        }
    }

    private void openAction() {
        presenter.openBoxInfo();
    }

    private void addAction() {
        presenter.openAddScreen();
    }

    @Override
    public void enableAddAction() {
        addAction.setVisible( true );
    }

    @Override
    public void disableAddAction() {
        addAction.setVisible( false );
    }

    @Override
    public void enableOpenAction() {
        openAction.setVisible( true );
    }

    @Override
    public void disableOpenAction() {
        openAction.setVisible( false );
    }

    @Override
    public void onSelect() {
        notSelected.setVisible( false );
        selected.setVisible( true );
        box.addStyleName( ContainerResources.INSTANCE.CSS().selected() );
        presenter.onSelect();
    }

    @Override
    public void onDeselect() {
        selected.setVisible( false );
        notSelected.setVisible( true );
        box.removeStyleName( ContainerResources.INSTANCE.CSS().selected() );
        presenter.onUnSelect();
    }

    @Override
    public void setStatus( final ContainerStatus status ) {
        setupStatus( this.status, status );
    }

    @Override
    public void show() {
        this.setVisible( true );
    }

    @Override
    public void hide() {
        this.setVisible( false );
        onDeselect();
    }

    @Override
    public void setName( final String value ) {
        containerName.setText( value );
    }

    @Override
    public void setDescription( String value ) {
        complement.setText( value );
    }

}