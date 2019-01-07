package org.dashbuilder.renderer.c3.client.exports;

import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

import com.google.gwt.core.client.ScriptInjector;

public class ResourcesInjector {

    static boolean injected;

    public static void ensureInjected() {
        if (!injected) {
            injectAllResources();
            injected = true;
        }
    }

    private static void injectAllResources() {
        PatternFlyBootstrapper.ensureD3IsAvailable();
        ScriptInjector.fromString(NativeLibraryResources.INSTANCE.c3js().getText())
                        .setWindow(ScriptInjector.TOP_WINDOW)
                        .inject();
    }

}
