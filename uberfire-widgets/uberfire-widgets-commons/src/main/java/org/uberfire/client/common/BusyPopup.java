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

package org.uberfire.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.animations.LinearFadeInAnimation;
import org.uberfire.client.animations.LinearFadeOutAnimation;

/**
 * A simple pop-up to show messages while "long running" activities take place
 */
public class BusyPopup extends DecoratedPopupPanel {

    interface LoadingViewBinder
            extends
            UiBinder<Widget, BusyPopup> {

    }

    private static LoadingViewBinder uiBinder = GWT.create( LoadingViewBinder.class );

    private static Timer deferredShowTimer = new Timer() {
        @Override
        public void run() {
            fadeInAnimation.run( 250 );
        }
    };

    private static MessageState state = MessageState.HIDDEN;

    @UiField
    Label message;

    private static final BusyPopup INSTANCE = new BusyPopup();

    private static final LinearFadeInAnimation fadeInAnimation = new LinearFadeInAnimation( INSTANCE ) {

        @Override
        public void onStart() {
            state = MessageState.SHOWING;
            super.onStart();
            INSTANCE.center();
        }

        @Override
        public void onComplete() {
            state = MessageState.VISIBLE;
            super.onComplete();
        }
    };

    private static final LinearFadeOutAnimation fadeOutAnimation = new LinearFadeOutAnimation( INSTANCE ) {

        @Override
        public void onStart() {
            state = MessageState.HIDING;
            super.onStart();
        }

        @Override
        public void onComplete() {
            super.onComplete();
            INSTANCE.hide();
            state = MessageState.HIDDEN;
        }
    };

    private BusyPopup() {
        setWidget( uiBinder.createAndBindUi( this ) );

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );
        setGlassEnabled( true );
    }

    public static void showMessage( final String message ) {
        if ( state == MessageState.SHOWING || state == MessageState.VISIBLE ) {
            return;
        }
        INSTANCE.message.setText( message );
        deferredShowTimer.schedule( 250 );
    }

    public static void close() {
        if ( state == MessageState.HIDING || state == MessageState.HIDDEN ) {
            return;
        }
        deferredShowTimer.cancel();
        if ( state == MessageState.VISIBLE || state == MessageState.SHOWING ) {
            fadeOutAnimation.run( 250 );
        }
    }

    private enum MessageState {
        SHOWING,
        VISIBLE,
        HIDING,
        HIDDEN
    }

}
