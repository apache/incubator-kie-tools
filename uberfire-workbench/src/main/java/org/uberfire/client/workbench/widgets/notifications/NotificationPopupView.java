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

package org.uberfire.client.workbench.widgets.notifications;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import org.uberfire.client.workbench.animations.LinearFadeInAnimation;
import org.uberfire.client.workbench.animations.Pause;
import org.uberfire.client.workbench.animations.Sequencer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A notification message
 */
@Dependent
public class NotificationPopupView extends DecoratedPopupPanel {

    interface NotificationPopupViewBinder
        extends
        UiBinder<Panel, NotificationPopupView> {
    }

    private static NotificationPopupViewBinder uiBinder = GWT.create( NotificationPopupViewBinder.class );

    @UiField
    public Label                               notification;

    @UiField
    public SimplePanel                         container;

    @PostConstruct
    public void init() {
        setWidget( uiBinder.createAndBindUi( this ) );
    }

    /**
     * Set the text to display
     * 
     * @param text
     */
    public void setNotification(final String text) {
        notification.setText( text );
    }

    /**
     * Set the width of the Notification pop-up
     * 
     * @param width
     */
    public void setNotificationWidth(String width) {
        //Setting the width of the DecoratedPopupPanel causes it to be rendered incorrectly.
        //We therefore set the size of an internal element that holds the actual content.
        container.setWidth( width );
    }

    /**
     * Show the Notification pop-up. This consists of fading the pop-up into
     * view and pausing. Once complete the onCompleteCommand will be executed.
     * 
     * @param onCompleteCommand
     */
    public void show(final Command onCompleteCommand) {

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
        Sequencer s = new Sequencer();
        s.add( fadeInAnimation,
               250 );
        s.add( pauseAnimation,
               2000 );
        s.run();
    }

}