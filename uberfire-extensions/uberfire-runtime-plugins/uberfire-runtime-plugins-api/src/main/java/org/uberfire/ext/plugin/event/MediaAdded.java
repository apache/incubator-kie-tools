package org.uberfire.ext.plugin.event;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.plugin.model.Media;

@Portable
public class MediaAdded {

    private String pluginName;
    private Media media;

    public MediaAdded() {
    }

    public MediaAdded( final String pluginName,
                       final Media media ) {
        this.pluginName = pluginName;
        this.media = media;
    }

    public String getPluginName() {
        return pluginName;
    }

    public Media getMedia() {
        return media;
    }
}
