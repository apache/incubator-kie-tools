/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodeller.core.AnnotationDefinition;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.backend.vfs.Path;

@Portable
public class EditorModelContent extends DataModelerResult {

    /**
     * Data model for current module at the time the DataObject was loaded.
     */
    DataModel dataModel;

    /**
     * Model for the .java file being edited.
     */
    private DataObject dataObject;

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

    private KieModule currentModule;

    private Set<String> currentModulePackages = new HashSet<String>();

    private Map<String, Path> dataObjectPaths = new HashMap<String, Path>();

    private Map<String, AnnotationDefinition> annotationDefinitions = null;

    private List<PropertyType> propertyTypes = null;

    public EditorModelContent() {
    }

    public DataModel getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public void setOriginalClassName(String originalClassName) {
        this.originalClassName = originalClassName;
    }

    public String getOriginalPackageName() {
        return originalPackageName;
    }

    public void setOriginalPackageName(String originalPackageName) {
        this.originalPackageName = originalPackageName;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public KieModule getCurrentModule() {
        return currentModule;
    }

    public void setCurrentModule(KieModule currentModule) {
        this.currentModule = currentModule;
    }

    public Set<String> getCurrentModulePackages() {
        return currentModulePackages;
    }

    public void setCurrentModulePackages(Set<String> currentModulePackages) {
        this.currentModulePackages = currentModulePackages;
    }

    public Overview getOverview() {
        return overview;
    }

    public void setOverview(Overview overview) {
        this.overview = overview;
    }

    public Map<String, Path> getDataObjectPaths() {
        return dataObjectPaths;
    }

    public void setDataObjectPaths(Map<String, Path> dataObjectPaths) {
        this.dataObjectPaths = dataObjectPaths;
    }

    public Map<String, AnnotationDefinition> getAnnotationDefinitions() {
        return annotationDefinitions;
    }

    public void setAnnotationDefinitions(Map<String, AnnotationDefinition> annotationDefinitions) {
        this.annotationDefinitions = annotationDefinitions;
    }

    public List<PropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<PropertyType> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
}