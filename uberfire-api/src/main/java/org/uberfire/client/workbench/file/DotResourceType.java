package org.uberfire.client.workbench.file;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class DotResourceType implements ResourceType {

    @Override
    public String getShortName() {
        return "meta data";
    }

    @Override
    public String getDescription() {
        return "Dot file";
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getPrefix() {
        return ".";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().startsWith( getPrefix() );
    }
}
