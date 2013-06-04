package org.kie.workbench.common.screens.home.client.widgets.sections;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A Section positioning it's children vertically
 */
public class VerticalSectionWidget extends VerticalPanel {

    private HeaderWidget header = new HeaderWidget();

    public VerticalSectionWidget() {
        add( header );
    }

    public void setHeaderText( final String headerText ) {
        this.header.setText( headerText );
    }



}
