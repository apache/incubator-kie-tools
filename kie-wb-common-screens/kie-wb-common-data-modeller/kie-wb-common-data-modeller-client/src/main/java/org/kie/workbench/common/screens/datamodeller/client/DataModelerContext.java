/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client;


import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.guvnor.common.services.project.model.Package;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class DataModelerContext {

    private DataModelTO dataModel;

    private DataModelHelper helper;

    private Map<String, AnnotationDefinitionTO> annotationDefinitions;

    private List<PropertyTypeTO> baseTypes;

    private boolean dirty = false;

    private InvalidateDMOProjectCacheEvent lastDMOUpdate;

    private InvalidateDMOProjectCacheEvent lastJavaFileChangeEvent;

    private List<String> currentProjectPackages = new ArrayList<String>();

    public DataModelerContext() {
    }

    public void init(List<PropertyTypeTO> baseTypes) {
        this.baseTypes = baseTypes;
        helper = new DataModelHelper();
        helper.setBaseTypes(baseTypes);
    }

    public DataModelTO getDataModel() {
        return dataModel;
    }

    public void setDataModel(DataModelTO dataModel) {
        this.dataModel = dataModel;
        helper.setDataModel(dataModel);
    }

    public DataModelHelper getHelper() {
        return helper;
    }

    public void setHelper(DataModelHelper helper) {
        this.helper = helper;
    }

    public Map<String, AnnotationDefinitionTO> getAnnotationDefinitions() {
        return annotationDefinitions;
    }

    public void setAnnotationDefinitions(Map<String, AnnotationDefinitionTO> annotationDefinitions) {
        this.annotationDefinitions = annotationDefinitions;
    }

    public List<PropertyTypeTO> getBaseTypes() {
        return baseTypes;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDMOInvalidated() {
        return lastDMOUpdate != null || lastJavaFileChangeEvent != null;
    }

    public InvalidateDMOProjectCacheEvent getLastDMOUpdate() {
        return lastDMOUpdate == null ? getLastJavaFileChangeEvent() : lastDMOUpdate;
    }

    public void setLastDMOUpdate(InvalidateDMOProjectCacheEvent lastDMOUpdate) {
        this.lastDMOUpdate = lastDMOUpdate;
    }

    public InvalidateDMOProjectCacheEvent getLastJavaFileChangeEvent() {
        return lastJavaFileChangeEvent;
    }

    public void setLastJavaFileChangeEvent(InvalidateDMOProjectCacheEvent lastJavaFileChangeEvent) {
        this.lastJavaFileChangeEvent = lastJavaFileChangeEvent;
    }

    public void appendPackages(Collection<Package> packages) {
        if (packages != null) {
            for (Package packageToAppend : packages) {
                if (!"".equals(packageToAppend.getPackageName()) && !currentProjectPackages.contains(packageToAppend.getPackageName())) {
                    currentProjectPackages.add(packageToAppend.getPackageName());
                }
            }
        }
    }

    public void appendPackage(String packageName) {

        if (packageName != null && !"".equals(packageName)) {
            String[] subPackages = DataModelerUtils.calculateSubPackages(packageName);
            String subPackage = null;
            for (int i = 0; subPackages != null && i < subPackages.length; i++) {
                subPackage = subPackages[i];
                if (!currentProjectPackages.contains(subPackage)) currentProjectPackages.add(subPackage);
            }
        }
    }
    
    public List<String> getCurrentProjectPackages() {
        return currentProjectPackages;
    }
    
    public void cleanPackages() {
        if (currentProjectPackages != null) currentProjectPackages.clear();
    }

    public void clear() {
        if (annotationDefinitions != null) annotationDefinitions.clear();
        if (baseTypes != null) baseTypes.clear();
        if (dataModel != null && dataModel.getDataObjects() != null) dataModel.getDataObjects().clear();
        cleanPackages();
        helper = new DataModelHelper();
        setLastDMOUpdate(null);
        setLastJavaFileChangeEvent(null);
    }
}
