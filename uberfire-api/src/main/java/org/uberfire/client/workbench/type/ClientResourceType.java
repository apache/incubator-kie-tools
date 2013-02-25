package org.uberfire.client.workbench.type;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.shared.workbench.type.ResourceTypeDefinition;

/**
 *
 */
public interface ClientResourceType extends ResourceTypeDefinition {

    /**
     * An icon representing the resource type
     * @return the icon
     */
    public IsWidget getIcon();

}
