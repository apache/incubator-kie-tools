package org.uberfire.client.workbench.file;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DotResourceType implements ResourceType {

    @Override
    public String getDescription() {
        return "Dot file";
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().startsWith( "." );
    }
}
