package org.kie.workbench.common.screens.home.client.sections;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A Section laying it's children vertically
 */
public class VerticalSection extends VerticalPanel {

    private Header header = new Header();

    public VerticalSection() {
        add( header );
    }

    public void setHeaderText( final String headerText ) {
        this.header.setText( headerText );
    }



}
