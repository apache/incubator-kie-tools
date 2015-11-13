/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.screens.miscfeatures;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.*;

/**
 * A stand-alone (i.e. devoid of Workbench dependencies) View
 */
public class MiscFeaturesView extends Composite
        implements
        RequiresResize,
        MiscFeaturesPresenter.View {

    interface ViewBinder
            extends
            UiBinder<Widget, MiscFeaturesView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    public HTMLPanel panel;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private PlaceManager placeManager;

    private MiscFeaturesPresenter presenter;

    @UiField
    Button editor;

    @Inject
    private Caller<VFSService> vfsServices;

    @Override
    public void init( final MiscFeaturesPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        int width = getParent().getOffsetWidth();
        panel.setPixelSize( width,
                height );
    }

    @UiHandler( "notificationDefaultButton" )
    public void onClickNotificationButton( final ClickEvent event ) {
        notification.fire( new NotificationEvent( "Something happened" ) );
    }

    @UiHandler( "notificationErrorButton" )
    public void onClickNotificationErrButton( final ClickEvent event ) {
        notification.fire( new NotificationEvent( "Something happened", ERROR ) );
    }

    @UiHandler( "notificationSuccessButton" )
    public void onClickNotificationSuccessButton( final ClickEvent event ) {
        notification.fire( new NotificationEvent( "Something happened", SUCCESS ) );
    }

    @UiHandler( "notificationInfoButton" )
    public void onClickNotificationInfoButton( final ClickEvent event ) {
        notification.fire( new NotificationEvent( "Something happened", INFO ) );
    }

    @UiHandler( "notificationWarnButton" )
    public void onClickNotificationWarnButton( final ClickEvent event ) {
        notification.fire( new NotificationEvent( "Something happened", WARNING ) );
    }

    @UiHandler( "launchUnknownPlace" )
    public void onClickLaunchUnknownPlace( final ClickEvent event ) {
        placeManager.goTo( new DefaultPlaceRequest( "MyTestPopUp" ) );
    }

    @UiHandler( "setNewTitleButton" )
    public void onSetNewTitleButtonClick( final ClickEvent event ) {
        presenter.setNewTitle( "NewCoolTitle" );
    }

    @UiHandler( "activityNotFound" )
    public void onClickActivityNotFound( final ClickEvent event ) {
        placeManager.goTo( "some.place.does.not.exists.please!" );
    }

    @UiHandler( "editor" )
    void handleEditor( ClickEvent e ) {
        vfsServices.call( new RemoteCallback<Path>() {
            @Override
            public void callback( final Path o ) {
                placeManager.goTo( o );
            }
        } ).get( "default://uf-playground/todo.md" );

    }

}