package org.kie.uberfire.plugin.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class Plugin extends Activity {

    private Path path;

    public Plugin() {
    }

    public Plugin( final String name,
                   final PluginType type,
                   final Path path ) {
        super( name, type );
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
