/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.client.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.html.Span;
import org.uberfire.ext.widgets.common.client.animations.LinearFadeInAnimation;
import org.uberfire.ext.widgets.common.client.animations.LinearFadeOutAnimation;

public class CustomBusyPopup extends DecoratedPopupPanel {

    private static CustomBusyPopup.LoadingViewBinder uiBinder = GWT.create(CustomBusyPopup.LoadingViewBinder.class);
    private static final CustomBusyPopup INSTANCE = new CustomBusyPopup();

    private static Timer deferredShowTimer = new Timer() {
        @Override
        public void run() {
            fadeInAnimation.run(250);
        }
    };

    private static CustomBusyPopup.MessageState state = CustomBusyPopup.MessageState.DORMANT;
    private static final LinearFadeInAnimation fadeInAnimation = new LinearFadeInAnimation(INSTANCE) {

        @Override
        public void onStart() {
            state = CustomBusyPopup.MessageState.SHOWING;
            INSTANCE.center();
            super.onStart();
        }

        @Override
        public void onComplete() {
            state = CustomBusyPopup.MessageState.VISIBLE;
            super.onComplete();
        }
    };
    private static final LinearFadeOutAnimation fadeOutAnimation = new LinearFadeOutAnimation(INSTANCE) {

        @Override
        public void onStart() {
            state = CustomBusyPopup.MessageState.HIDING;
            super.onStart();
        }

        @Override
        public void onComplete() {
            state = CustomBusyPopup.MessageState.DORMANT;
            INSTANCE.hide();
            super.onComplete();
        }
    };
    @UiField
    Span message;

    private CustomBusyPopup() {
        setWidget(uiBinder.createAndBindUi(this));

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex(Integer.MAX_VALUE);
        setGlassEnabled(true);
    }

    public static void showMessage(final String message) {
        switch (state) {
            case DORMANT:
                INSTANCE.message.setText(message);
                deferredShowTimer.schedule(250);
                state = CustomBusyPopup.MessageState.PENDING;
                break;
            case PENDING:
            case SHOWING:
            case VISIBLE:
                INSTANCE.message.setText(message);
                break;
            case HIDING:
                fadeOutAnimation.cancel();
                INSTANCE.message.setText(message);
                fadeInAnimation.onComplete();
        }
    }

    public static void close() {
        switch (state) {
            case DORMANT:
                break;
            case PENDING:
                deferredShowTimer.cancel();
                state = CustomBusyPopup.MessageState.DORMANT;
                break;
            case SHOWING:
                fadeInAnimation.cancel();
                fadeOutAnimation.run(250);
                break;
            case VISIBLE:
                fadeOutAnimation.run(250);
                break;
            case HIDING:
                break;
        }
    }

    private enum MessageState {
        DORMANT,
        PENDING,
        SHOWING,
        VISIBLE,
        HIDING
    }

    interface LoadingViewBinder
            extends
            UiBinder<Widget, CustomBusyPopup> {

    }
}
