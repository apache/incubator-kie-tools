package org.uberfire.client.workbench.file;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

/**
 * Definition of a Resource Type
 */
public interface ResourceType {

    /**
     * A description of the resource type
     * @return the description
     */
    public String getDescription();

    /**
     * An icon representing the resource type
     * @return the icon
     */
    public IsWidget getIcon();

    /**
     * Indicates if the current parameter path matched the current resource type
     * @return true if matches, otherwise false
     */
    boolean accept( final Path path );
}
