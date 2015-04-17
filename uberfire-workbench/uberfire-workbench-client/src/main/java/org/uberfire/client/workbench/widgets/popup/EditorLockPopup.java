package org.uberfire.client.workbench.widgets.popup;

import javax.enterprise.context.Dependent;

import org.uberfire.client.resources.i18n.WorkbenchConstants;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Displays a {@link PopupPanel} in response to a failed lock attempt.
 */
@Dependent
public class EditorLockPopup {

    private PopupPanel popup;

    public void show( final Element parent,
                      final String lockedBy ) {

        if ( popup != null ) {
            popup.hide();
        }

        popup = new PopupPanel( true );
        popup.add( new Label( WorkbenchConstants.INSTANCE.lockedMessage( lockedBy ) ) );
        popup.setStylePrimaryName( "uf-lock-hint" );
        popup.setPopupPositionAndShow( new PopupPanel.PositionCallback() {

            public void setPosition( int offsetWidth,
                                     int offsetHeight ) {

                int left = parent.getAbsoluteLeft() + (parent.getClientWidth() - offsetWidth) - 10;
                int top = parent.getAbsoluteTop() + 5;
                popup.setPopupPosition( left,
                                        top );
            }
        } );
        popup.show();

        new Timer() {

            @Override
            public void run() {
                popup.hide();
            }
        }.schedule( 7500 );
    }
}
