package org.kie.uberfire.plugin.event;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.plugin.model.Media;

@Portable
public class MediaDeleted {

    private String pluginName;
    private Media media;

    public MediaDeleted() {
    }

    public MediaDeleted( final String pluginName,
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
