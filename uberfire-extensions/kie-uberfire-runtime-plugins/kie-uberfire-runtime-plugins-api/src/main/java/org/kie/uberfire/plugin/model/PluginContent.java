package org.kie.uberfire.plugin.model;

import java.util.Map;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class PluginContent extends PluginSimpleContent {

    private Set<Media> mediaLibrary;

    public PluginContent() {
    }

    public PluginContent( final String name,
                          final PluginType type,
                          final Path path,
                          final String template,
                          final String css,
                          final Map<CodeType, String> codeMap,
                          final Set<Framework> frameworks,
                          final Language language,
                          final Set<Media> mediaLibrary ) {
        super( name, type, path, template, css, codeMap, frameworks, language );
        this.mediaLibrary = mediaLibrary;
    }

    public Set<Media> getMediaLibrary() {
        return mediaLibrary;
    }
}
