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

    private final HorizontalPanel panel = new HorizontalPanel();
    private final Label statusLabel = new Label();
    private final TextArea exceptionLog = new TextArea();
    private int uncaughtExceptionCount;

    public UncaughtExceptionAlerter() {
        statusLabel.setText( "0 uncaught exceptions" );
        statusLabel.getElement().getStyle().setColor( "green" );
        statusLabel.getElement().setId( "UncaughtExceptionAlerter-statusLabel" );

        // the tests will read the contents programmatically.
        // it's also big enough to click, select all, copy during interactive sessions
        exceptionLog.setPixelSize( 40,  15 );
        exceptionLog.getElement().setId( "UncaughtExceptionAlerter-exceptionLog" );

        panel.add(statusLabel);
        panel.add(exceptionLog);

        GWT.setUncaughtExceptionHandler( this );
    }

    @Override
    public void onUncaughtException( Throwable e ) {
        uncaughtExceptionCount++;
        statusLabel.setText( uncaughtExceptionCount + " uncaught exceptions" );
        statusLabel.getElement().getStyle().setColor( "red" );

        StringBuilder newStackTrace = new StringBuilder();
        newStackTrace.append( e.toString() );
        for ( StackTraceElement ste : e.getStackTrace() ) {
            newStackTrace.append( "\n   ").append( ste.toString() );
        }
        exceptionLog.setText( exceptionLog.getText() + "\n" + newStackTrace );
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
