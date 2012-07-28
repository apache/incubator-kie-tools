/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.widgets;

import org.drools.guvnor.client.widgets.ShowMessageEvent.MessageType;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class MessageWidget extends Composite {

    interface MessageWidgetBinder
        extends
        UiBinder<Widget, MessageWidget> {
    }

    private static MessageWidgetBinder uiBinder = GWT.create( MessageWidgetBinder.class );

    @UiField
    SimplePanel                        messageContainer;

    @UiField
    Label                              lblMessage;

    public MessageWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
        setWidth( "100%" );
    }

    public void showMessage(String message,
                            MessageType messageType) {
        lblMessage.setText( message );
        messageContainer.setVisible( true );

        Timer timer = new Timer() {
            public void run() {
                HideMessageAnimation anim = new HideMessageAnimation( messageContainer );
                anim.run( 250 );
            }
        };
        timer.schedule( 1500 );
    }

    private static class HideMessageAnimation extends Animation {

        private final SimplePanel container;

        private HideMessageAnimation(SimplePanel container) {
            this.container = container;
        }

        @Override
        protected void onUpdate(double progress) {
            this.container.getElement().getStyle().setOpacity( 1.0 - progress );
        }

        @Override
        protected void onComplete() {
            this.container.setVisible( false );
            this.container.getElement().getStyle().setOpacity( 1.0 );
        }

    }
}
