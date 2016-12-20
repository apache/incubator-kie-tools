/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.api;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

@ApplicationScoped
public class UncaughtExceptionAlerter implements IsWidget, UncaughtExceptionHandler {

    private static boolean alreadyInitialized;
    private static boolean disabled;

    private final HorizontalPanel panel = new HorizontalPanel();
    private final Label statusLabel = new Label();
    private final TextArea exceptionLog = new TextArea();
    private int uncaughtExceptionCount;

    public UncaughtExceptionAlerter() {
        alreadyInitialized = true;

        if ( disabled ) {
            statusLabel.setText( "Uncaught Exception Alerter Disabled" );

        } else {
            statusLabel.setText( "0 uncaught exceptions" );
            statusLabel.getElement().getStyle().setColor( "green" );
            statusLabel.getElement().setId( "UncaughtExceptionAlerter-statusLabel" );

            // the tests will read the contents programmatically.
            // it's also big enough to click, select all, copy during interactive sessions
            exceptionLog.setPixelSize( 40,  15 );
            exceptionLog.getElement().setId( "UncaughtExceptionAlerter-exceptionLog" );

            GWT.setUncaughtExceptionHandler( this );
        }

        panel.add(statusLabel);
        panel.add(exceptionLog);
    }

    @Override
    public void onUncaughtException( Throwable e ) {
        uncaughtExceptionCount++;
        statusLabel.setText( uncaughtExceptionCount + " uncaught exceptions" );
        statusLabel.getElement().getStyle().setColor( "red" );

        GWT.log( "Uncaught Exception", e );
        StringBuilder newStackTrace = new StringBuilder();
        while ( e != null ) {
            if ( newStackTrace.length() > 0 ) {
                newStackTrace.append( "\nCaused by: " );
            }
            newStackTrace.append( e.toString() );
            for ( StackTraceElement ste : e.getStackTrace() ) {
                newStackTrace.append( "\n   " ).append( ste.toString() );
            }
            e = e.getCause();
        }
        exceptionLog.setText( exceptionLog.getText() + "\n" + newStackTrace );
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    public int getUncaughtExceptionCount() {
        return uncaughtExceptionCount;
    }

    public String getExceptionLog() {
        return exceptionLog.getText();
    }

    public static void disable() {
        if ( alreadyInitialized && !disabled ) {
            throw new IllegalStateException( "Too late. Already initialized." );
        }
        disabled = true;
    }

}
