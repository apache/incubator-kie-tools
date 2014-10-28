/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.*;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

@Portable
public class EditorModelContent extends DataModelerResult {

    /**
     * Data model for current project at the time the DataObjectTO was loaded.
     */
    DataModelTO dataModel;

    /**
     * Model for the .java file being edited.
     */
    private DataObjectTO dataObject;

    private String originalClassName;

    private String originalPackageName;

    /**
     * Path for the file that is being edited.
     */
    private Path path;

    /**
     * Source of the .java file being edited.
     */
    private String source;

    private Overview overview;

    private long elapsedTime;

    private Project currentProject;

    private Set<Package> currentProjectPackages = new HashSet<Package>(  );

    private static int modelIds = 0;

    //only to distinguish models created in memory
    private int id = modelIds++;

    public EditorModelContent() {
    }

    public DataModelTO getDataModel() {
        return dataModel;
    }

    public void setDataModel( DataModelTO dataModel ) {
        this.dataModel = dataModel;
    }

    public DataObjectTO getDataObject() {
        return dataObject;
    }

    public void setDataObject( DataObjectTO dataObject ) {
        this.dataObject = dataObject;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public void setOriginalClassName( String originalClassName ) {
        this.originalClassName = originalClassName;
    }

    public String getOriginalPackageName() {
        return originalPackageName;
    }

    public void setOriginalPackageName( String originalPackageName ) {
        this.originalPackageName = originalPackageName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath( Path path ) {
        this.path = path;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime( long elapsedTime ) {
        this.elapsedTime = elapsedTime;
    }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject( Project currentProject ) {
        this.currentProject = currentProject;
    }

    public Set<Package> getCurrentProjectPackages() {
        return currentProjectPackages;
    }

    public void setCurrentProjectPackages( Set<Package> currentProjectPackages ) {
        this.currentProjectPackages = currentProjectPackages;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview( Overview overview ) {
        this.overview = overview;
    }
}