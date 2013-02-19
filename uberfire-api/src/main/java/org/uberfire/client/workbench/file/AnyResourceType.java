package org.uberfire.client.workbench.file;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class AnyResourceType implements ResourceType {

    @Override
    public String getShortName() {
        return "any";
    }

    @Override
    public String getDescription() {
        return "Any file";
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean accept( final Path path ) {
        return true;
    }
}
