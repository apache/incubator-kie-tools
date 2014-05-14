/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.mvp;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.widgets.popup.PopupView;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Implementation of behaviour common to all popup activities. Concrete implementations are typically not written by
 * hand; rather, they are generated from classes annotated with {@link WorkbenchPopup}.
 */
public abstract class AbstractPopupActivity extends AbstractActivity implements PopupActivity {

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    private final PopupView popup = new PopupView();

    public AbstractPopupActivity( final PlaceManager placeManager ) {
        super( placeManager );
        popup.addCloseHandler( new CloseHandler<PopupView>() {
            @Override
            public void onClose( CloseEvent<PopupView> event ) {
                closePlaceEvent.fire( new BeforeClosePlaceEvent( place ) );
            }
        } );
    }

    @Override
    public void launch( final PlaceRequest place,
                        final Command callback ) {
        super.launch( place,
                callback );

        onStartup( place );

        final IsWidget widget = getWidget();

        popup.setContent( widget );
        popup.setTitle( getTitle() );
        popup.show();

        onOpen();
    }

    @Override
    public abstract String getTitle();

    @Override
    public IsWidget getTitleDecoration() {
        return null;
    }

    @Override
    public abstract IsWidget getWidget();

    @Override
    public void onStartup() {
        //Do nothing.
    }

    @Override
    public void onStartup( final PlaceRequest place ) {
        //Do nothing.
    }

    @Override
    public boolean onMayClose() {
        return true;
    }

    @Override
    public void onClose() {
        //Do nothing.
    }

    @Override
    public void onShutdown() {
        //Do nothing.
    }

    @SuppressWarnings("unused")
    private void onClose( @Observes ClosePlaceEvent event ) {
        final PlaceRequest place = event.getPlace();
        if ( place.equals( this.place ) ) {
            popup.hide();
        }
    }

}
