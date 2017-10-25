package org.guvnor.ala.build.maven.model;

import java.util.Collection;

import org.guvnor.ala.build.Project;

/**
 * Interface that represent the specifics of a MavenProject
 * @see Project
 */
public interface MavenProject extends Project {

    /*
     * Return the collection of maven plugins used in the Project
     * @return Collection<PlugIn> with the plugins used by the project
     * @see PlugIn
     */
    Collection<PlugIn> getBuildPlugins();
}
