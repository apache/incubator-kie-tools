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


import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;

import java.util.List;
import java.util.Map;

public class DataModelerContext {

    private DataModelTO dataModel;

    private DataModelHelper helper;

    private Map<String, AnnotationDefinitionTO> annotationDefinitions;

    private List<PropertyTypeTO> baseTypes;

    private boolean dirty = false;

    public DataModelerContext() {
        helper = new DataModelHelper();
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

    public void setBaseTypes(List<PropertyTypeTO> baseTypes) {
        this.baseTypes = baseTypes;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
