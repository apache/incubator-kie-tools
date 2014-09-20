package org.kie.uberfire.plugin.client.resources;

import com.google.gwt.resources.client.CssResource;

/**
 * TODO: update me
 */
public interface PluginCss extends CssResource {

    String content();

    @ClassName("editor-wrapping")
    String editorWrapping();

    String window();

    String column();

    String bottom();

    String left();

    String right();

    @ClassName("handler-vertical")
    String handlerVertical();

    @ClassName("handler-horizontal")
    String handlerHorizontal();

    String top();

    @ClassName("window-label")
    String windowLabel();

    @ClassName("bottom-inverted")
    String bottomInverted();

    @ClassName("top-inverted")
    String topInverted();

    @ClassName("media-lib")
    String mediaLib();
}
