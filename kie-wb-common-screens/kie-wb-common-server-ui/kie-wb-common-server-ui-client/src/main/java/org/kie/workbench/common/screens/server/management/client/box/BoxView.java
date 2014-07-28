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
import org.kie.workbench.common.screens.server.management.client.util.ContainerStatusUtil;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;

import static org.kie.workbench.common.screens.server.management.client.util.ContainerStatusUtil.setupStatus;
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

    public void setup( final BoxPresenter presenter ) {
        this.presenter = checkNotNull( "presenter", presenter );

        selected.setTitle( "Selected" );
        notSelected.setTitle( "Not selected" );
        addAction.setTitle( "New Container" );
        openAction.setTitle( "Open" );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) selected, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) selected, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                unSelect();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) notSelected, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) notSelected, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                select();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) addAction, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) addAction, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                addAction();
            }
        } );

        DOM.sinkEvents( (com.google.gwt.user.client.Element) openAction, Event.ONCLICK );
        DOM.setEventListener( (com.google.gwt.user.client.Element) openAction, new EventListener() {
            public void onBrowserEvent( final Event event ) {
                openAction();
            }
        } );

        containerName.setInnerText( presenter.getName() );
        if ( presenter.getType().equals( BoxType.CONTAINER ) ) {
            box.addClassName( ContainerResources.INSTANCE.CSS().childContainer() );
        } else {
            box.removeClassName( ContainerResources.INSTANCE.CSS().childContainer() );
        }

        complement.setInnerText( presenter.getDescription() );

        setStatus( presenter.getStatus() );

        if ( presenter.getStatus().equals( ContainerStatus.STARTED ) && presenter.getOnAddAction() != null ) {
            enableAddAction();
        } else {
            disableAddAction();
        }

        if ( presenter.getOnOpenAction() != null ) {
            enableOpenAction();
        } else {
            disableOpenAction();
        }

    }

    private void openAction() {
        presenter.getOnOpenAction().execute();
    }

    private void addAction() {
        presenter.getOnAddAction().execute();
    }

    public void enableAddAction() {
        addAction.getStyle().clearDisplay();
    }

    public void disableAddAction() {
        addAction.getStyle().setDisplay( Style.Display.NONE );
    }

    public void enableOpenAction() {
        openAction.getStyle().clearDisplay();
    }

    public void disableOpenAction() {
        openAction.getStyle().setDisplay( Style.Display.NONE );
    }

    @Override
    public void select() {
        notSelected.getStyle().setDisplay( Style.Display.NONE );
        selected.getStyle().clearDisplay();
        box.addClassName( ContainerResources.INSTANCE.CSS().selected() );
        presenter.onSelect();
    }

    @Override
    public void unSelect() {
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
        unSelect();
    }

    @Override
    public boolean isVisible() {
        return !this.box.getStyle().getDisplay().equals( Style.Display.NONE );
    }

}