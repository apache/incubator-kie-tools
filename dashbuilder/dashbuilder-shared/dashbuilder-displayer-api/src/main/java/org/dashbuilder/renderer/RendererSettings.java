package org.dashbuilder.renderer;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Hold Renderer Settings.
 * 
 */
@Portable
public class RendererSettings {

    private final String defaultRenderer;
    private final boolean offline;

    public RendererSettings() {
        this.defaultRenderer = "";
        this.offline = false;
    }


    public RendererSettings(String defaultRenderer, boolean offline) {
        this.defaultRenderer = defaultRenderer;
        this.offline = offline;
    }
    

    /**
     * The UUID of the renderer that should be used by default. <br>
     * It can be set using <i>org.dashbuilder.renderer.default</i>.
     * 
     * @return The selected default renderer UUID
     */
    public String getDefaultRenderer() {
        return defaultRenderer;
    }

    /**
     * When true renderers that can't work offline are discarded. <br>
     * It can be set <i>org.dashbuilder.renderer.offline</i>. 
     * 
     * @return The offline flag value
     */
    public boolean isOffline() {
        return offline;
    }

}