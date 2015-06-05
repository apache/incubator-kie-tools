package org.uberfire.client.views.pfly.menu;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.html.Text;

/**
 * Denotes that the target class or producer of type {@link NavbarBrand} provides the main branding link that appears above
 * the application's menu bar. Each application that uses PatternFly should have one such type or producer.
 * <p>
 * To fit with the overall layout, <b>the NavbarBrand widget that bears this qualifier must have a height of 25px.</b>
 */
public class MainBrand extends Composite {

    public MainBrand(final String text){
        initWidget(new Text(text));
    }

    public MainBrand(final Image image){
        initWidget(image);
    }
}
