package org.uberfire.client.workbench.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.client.resources.i18n.UberfireConstants;
import org.uberfire.workbench.type.DotResourceTypeDefinition;

@ApplicationScoped
public class DotResourceType
        extends DotResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        String desc = UberfireConstants.INSTANCE.dotResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
