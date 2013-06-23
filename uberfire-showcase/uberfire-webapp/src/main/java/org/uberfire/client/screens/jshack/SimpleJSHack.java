package org.uberfire.client.screens.jshack;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCSingletonBean;
import org.uberfire.client.JSNativePlugin;
import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

/**
 *
 */
@WorkbenchPopup(identifier = "JSHack")
public class SimpleJSHack {

    private HTML element = null;

    @OnStart
    public void onStart() {
    }

    @OnReveal
    public void onReveal() {
        bind();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "My JS Hack!";
    }

    @WorkbenchPartView
    public Widget getView() {
        if ( element == null ) {
            final JSNativePlugin js = IOC.getBeanManager().lookupBeans( JSNativePlugin.class ).iterator().next().getInstance();
            this.element = new HTML( js.build().getInnerHTML() );
        }

        return this.element;
    }

    // Alias registerPlugin with a global JS function.
    private native String bind() /*-{
        $wnd.angular.bootstrap($wnd.document, []);
    }-*/;

}
