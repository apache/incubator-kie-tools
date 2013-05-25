package org.uberfire.workbench.type;

import org.uberfire.backend.vfs.Path;

/**
 * Definition of a Resource Type
 */
public interface ResourceTypeDefinition {

    /**
     * A short name of the resource type
     * @return the short name
     */
    public String getShortName();

    /**
     * A description of the resource type
     * @return the description
     */
    public String getDescription();

    /**
     * Resource prefix
     * @return the prefix
     */
    public String getPrefix();

    /**
     * Resource suffix
     * @return the prefix
     */
    public String getSuffix();

    /**
     * Defines the resource priority in terms of resource resolution
     * @return the priority
     */
    public int getPriority();

    /**
     * A simple (and maybe not very accurate) wildcard pattern to search for
     * this type of resource.
     * This is only used in order to optimize index and search.
     * @return the wildcard pattern
     */
    public String getSimpleWildcardPattern();

    /**
     * Indicates if the current parameter path matched the current resource type
     * @return true if matches, otherwise false
     */
    boolean accept( final Path path );
}
