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


@Portable
public class DataModelTO {
    
    private String name;

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

    public DataModelTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
     */
    public void setPersistedStatus() {
        deletedDataObjects.clear();
        for (DataObjectTO dataObjectTO : dataObjects) {
            dataObjectTO.setOriginalClassName(dataObjectTO.getClassName());
            dataObjectTO.setStatus(DataObjectTO.PERSISTENT);
        }
    }

    public List<String> getExternalClasses() {
        return externalClasses;
    }

    public void setExternalClasses(List<String> externalClasses) {
        this.externalClasses = externalClasses;
    }

    public int getId() {
        return id;
    }
}

