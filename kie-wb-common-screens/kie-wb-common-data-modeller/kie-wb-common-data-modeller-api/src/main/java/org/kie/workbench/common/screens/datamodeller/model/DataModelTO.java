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

package org.kie.workbench.common.screens.datamodeller.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Portable
public class DataModelTO {
    
    private String parentProjectName;

    private List<DataObjectTO> dataObjects = new ArrayList<DataObjectTO>();

    /**
     * List of class names imported by this module.
     */
    private List<String> externalClasses = new ArrayList<String>();

    /**
     * A list to remember data objects that was deleted in memory and has to be removed fisically when the model
     * is saved.
     */
    private List<DataObjectTO> deletedDataObjects = new ArrayList<DataObjectTO>();

    
    private static int modelIds = 0;

    //only to distinguish models created in memory
    private int id = modelIds++;

    public DataModelTO() {
    }

    public String getParentProjectName() {
        return parentProjectName;
    }

    public void setParentProjectName(String parentProjectName) {
        this.parentProjectName = parentProjectName;
    }

    public List<DataObjectTO> getDataObjects() {
        return dataObjects;
    }

    public void setDataObjects(List<DataObjectTO> dataObjects) {
        this.dataObjects = dataObjects;
    }
    
    public DataObjectTO getDataObjectByClassName(String className) {
        for (DataObjectTO dataObject : dataObjects) {
            if (dataObject.getClassName() != null && dataObject.getClassName().equals(className)) return dataObject;
        }
        return null;
    }

    public void removeDataObject(DataObjectTO dataObject) {
        getDataObjects().remove(dataObject);
        deletedDataObjects.add(dataObject);
    }

    public List<DataObjectTO> getDeletedDataObjects() {
        return deletedDataObjects;
    }

    public void setDeletedDataObjects(List<DataObjectTO> deletedDataObjects) {
        this.deletedDataObjects = deletedDataObjects;
    }

    /**
     * Tag all objects as persisted and clean deleted objects list.
     *
     * @param includeReadonlyObjects true set also readonly objects as PERSISTENT objects, false to skip them.
     */
    public void setPersistedStatus(boolean includeReadonlyObjects) {
        deletedDataObjects.clear();
        for (DataObjectTO dataObjectTO : dataObjects) {
            if (includeReadonlyObjects || !dataObjectTO.isExternallyModified()) {
                dataObjectTO.setOriginalClassName(dataObjectTO.getClassName());
                dataObjectTO.setStatus(DataObjectTO.PERSISTENT);
            }
        }
    }

    public void updateFingerPrints(Map<String, String> fingerPrints) {
        String fingerPrint = null;
        for (DataObjectTO dataObject : getDataObjects()) {
            fingerPrint = fingerPrints.get(dataObject.getClassName());
            if (fingerPrint != null) {
                dataObject.setFingerPrint(fingerPrint);
            }
        }
    }

    public List<String> getExternalClasses() {
        return externalClasses;
    }

    public void setExternalClasses(List<String> externalClasses) {
        this.externalClasses = externalClasses;
    }
    
    public boolean isExternal(String className) {
        return externalClasses != null && externalClasses.contains(className);
    }

    public int getId() {
        return id;
    }
}

