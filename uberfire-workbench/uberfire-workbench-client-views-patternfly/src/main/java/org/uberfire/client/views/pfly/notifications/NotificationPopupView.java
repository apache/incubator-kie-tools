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

package org.uberfire.client.views.pfly.notifications;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.uberfire.client.resources.WorkbenchResources;
import org.uberfire.client.workbench.widgets.animations.LinearFadeInAnimation;
import org.uberfire.client.workbench.widgets.animations.Pause;
import org.uberfire.client.workbench.widgets.animations.Sequencer;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * The view for an individual newly created notification message.
 */
public class NotificationPopupView extends DecoratedPopupPanel {

    private final Alert notification = new Alert();

    public NotificationPopupView() {
        setStyleName( WorkbenchResources.INSTANCE.CSS().notification() );
        setWidget( notification );
        notification.setDismissable( true );
    }

    /**
     * Set the text to display
     * @param text
     */
    public void setNotification( final String text ) {
        notification.setText( text );

    }

    public void setType( final NotificationEvent.NotificationType type ) {
        AlertType bs3Type;
        switch ( type ) {
            case ERROR:
                bs3Type = AlertType.DANGER;
                break;
            case DEFAULT:
                bs3Type = AlertType.INFO;
                break;
            default:
                bs3Type = AlertType.valueOf( type.toString() );
                break;
        }
        notification.setType( bs3Type );
    }

    /**
     * Set the width of the Notification pop-up
     * @param width
     */
    public void setNotificationWidth( final String width ) {
        //Setting the width of the DecoratedPopupPanel causes it to be rendered incorrectly.
        //We therefore set the size of an internal element that holds the actual content.
        notification.setWidth( width );
    }

    /**
     * Show the Notification pop-up. This consists of fading the pop-up into
     * view and pausing. Once complete the onCompleteCommand will be executed.
     * @param onCompleteCommand
     */
    public void show( final Command onCompleteCommand ) {

        //Fade in the notification message
        final LinearFadeInAnimation fadeInAnimation = new LinearFadeInAnimation( this ) {

            @Override
            public void onStart() {
                super.onStart();
                NotificationPopupView.this.show();
            }

        };

        //Pause. Removal is handled by the NotificationPopupsManager
        final Pause pauseAnimation = new Pause() {

            @Override
            public void onComplete() {
                super.onComplete();
                onCompleteCommand.execute();
            }

        };
        final Sequencer s = new Sequencer();
        s.add( fadeInAnimation, 250 );
        s.add( pauseAnimation, 2000 );
        s.run();
    }

}