package org.drools.workbench.screens.categories.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.categories.client.resources.ImageResources;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class CategoryDefinitionResourceType
        implements ClientResourceType {

    private static final Image IMAGE = new Image( ImageResources.INSTANCE.typeCategories() );

    @Override
    public String getShortName() {
        return "catogories xml";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getPrefix() {
        return "categories";
    }

    @Override
    public String getSuffix() {
        return "xml";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return getPrefix() + "." + getSuffix();
    }

    @Override
    public boolean accept( final Path path ) {
        return path.getFileName().equals( getPrefix() + "." + getSuffix() );
    }
}
