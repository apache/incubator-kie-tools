package org.dashbuilder.renderer.c3.client.exports;

import com.google.gwt.dom.client.StyleInjector;

public class ResourcesInjector {

    static boolean injected;

    public static void ensureInjected() {
        if (!injected) {
            injectAllResources();
            injected = true;
        }
    }

    private static void injectAllResources() {
        String c3css = NativeLibraryResources.INSTANCE.c3css().getText();
        StyleInjector.inject(c3css + ".c3-tooltip td{color: #000}");
        JavaScriptInjector.inject(NativeLibraryResources.INSTANCE.d3js().getText());
        JavaScriptInjector.inject(NativeLibraryResources.INSTANCE.c3js().getText());
    }

}
