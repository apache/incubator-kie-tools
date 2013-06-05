package org.kie.workbench.common.screens.home.client.widgets.sections;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Section positioning it's children vertically
 */
public class VerticalSectionWidget extends Composite {

    private HeaderWidget header = new HeaderWidget();
    private VerticalPanel container = new VerticalPanel();
    private VerticalPanel contents = new VerticalPanel();

    public VerticalSectionWidget() {
        initWidget( container );
        container.add( header );
        container.add( contents );
    }

    public void setHeaderText( final String headerText ) {
        this.header.setText( headerText );
    }

    public void add( final Widget w ) {
        contents.add( w );
    }

    public void clear() {
        contents.clear();
    }

}
