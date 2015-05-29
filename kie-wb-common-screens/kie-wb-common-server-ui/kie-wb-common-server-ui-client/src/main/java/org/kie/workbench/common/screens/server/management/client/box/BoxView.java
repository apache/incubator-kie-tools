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
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.screens.server.management.client.resources.ContainerResources;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;

import static org.kie.workbench.common.screens.server.management.client.util.ContainerStatusUtil.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class BoxView extends Composite implements BoxPresenter.View {

    interface ContainerViewBinder
            extends
            UiBinder<Widget, BoxView> {

    }

    private static ContainerViewBinder uiBinder = GWT.create( ContainerViewBinder.class );

    @UiField
    DivElement box;

    @UiField
    Element notSelected;

    @UiField
    Element selected;

    @UiField
    Element status;

    @UiField
    AnchorElement containerName;

    @UiField
    SpanElement complement;

    @UiField
    UListElement listOfServices;

    @UiField
    DivElement actions;

    @UiField
    Element addAction;

    @UiField
    Element openAction;

    private BoxPresenter presenter;

    public BoxView() {
        initWidget( uiBinder.createAndBindUi( this ) );
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

        DOM.sinkEvents( selected, Event.ONCLICK );
        DOM.setEventListener( selected, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                onDeselect();
            }
        } );

        DOM.sinkEvents( notSelected, Event.ONCLICK );
        DOM.setEventListener( notSelected, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                onSelect();
            }
        } );

        DOM.sinkEvents( addAction, Event.ONCLICK );
        DOM.setEventListener( addAction, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                addAction();
            }
        } );

        DOM.sinkEvents( openAction, Event.ONCLICK );
        DOM.setEventListener( openAction, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                openAction();
            }
        } );

        if ( type.equals( BoxType.CONTAINER ) ) {
            box.addClassName( ContainerResources.INSTANCE.CSS().childContainer() );
        } else {
            box.removeClassName( ContainerResources.INSTANCE.CSS().childContainer() );
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
        addAction.getStyle().clearDisplay();
    }

    @Override
    public void disableAddAction() {
        addAction.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void enableOpenAction() {
        openAction.getStyle().clearDisplay();
    }

    @Override
    public void disableOpenAction() {
        openAction.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void onSelect() {
        notSelected.getStyle().setDisplay( Style.Display.NONE );
        selected.getStyle().clearDisplay();
        box.addClassName( ContainerResources.INSTANCE.CSS().selected() );
        presenter.onSelect();
    }

    @Override
    public void onDeselect() {
        selected.getStyle().setDisplay( Style.Display.NONE );
        notSelected.getStyle().clearDisplay();
        box.removeClassName( ContainerResources.INSTANCE.CSS().selected() );
        presenter.onUnSelect();
    }

    @Override
    public void setStatus( final ContainerStatus status ) {
        setupStatus( this.status, status );
    }

    @Override
    public void show() {
        this.box.getStyle().clearDisplay();
    }

    @Override
    public void hide() {
        this.box.getStyle().setDisplay( Style.Display.NONE );
        onDeselect();
    }

    @Override
    public void setName( final String value ) {
        containerName.setInnerText( value );
    }

    @Override
    public void setDescription( String value ) {
        complement.setInnerText( value );
    }

}