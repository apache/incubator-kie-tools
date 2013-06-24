package org.uberfire.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class JSNativePlugin {

    private final JavaScriptObject obj;

    public JSNativePlugin( final JavaScriptObject obj ) {
        this.obj = obj;
    }

    public Element build() {
        final String result = build( obj );
        return new HTML( new SafeHtmlBuilder().appendHtmlConstant( result ).toSafeHtml() ).getElement();
    }

    // Alias registerPlugin with a global JS function.
    private static native String build( final JavaScriptObject o ) /*-{
        return o.build();
    }-*/;

}
