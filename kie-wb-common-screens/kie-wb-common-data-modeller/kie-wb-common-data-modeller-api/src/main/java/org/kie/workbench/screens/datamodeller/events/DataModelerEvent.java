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

package org.kie.workbench.screens.datamodeller.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.screens.datamodeller.model.ObjectPropertyTO;

@Portable
public class DataModelerEvent {

    protected DataModelTO currentModel;

    protected DataObjectTO currentDataObject;

    protected ObjectPropertyTO currentField;

    protected String source;
    
    public static final String DATA_MODEL_BROWSER = "DATA_MODEL_BROWSER";

    public static final String DATA_MODEL_BREAD_CRUMB = "DATA_MODEL_BREAD_CRUMB";

    public static final String DATA_OBJECT_BROWSER = "DATA_OBJECT_BROWSER";

    public static final String DATA_OBJECT_FIELD_EDITOR = "DATA_OBJECT_FIELD_EDITOR";

    public static final String DATA_OBJECT_EDITOR = "DATA_OBJECT_EDITOR";

    public static final String NEW_DATA_OBJECT_POPUP = "NEW_DATA_OBJECT_POPUP";

    private static int eventIds = 0;
    
    private int id = eventIds++;

    public DataModelerEvent() {
    }

    public DataModelerEvent(String source, DataModelTO currentModel, DataObjectTO currentDataObject) {
        this.source = source;
        this.currentModel = currentModel;
        this.currentDataObject = currentDataObject;
    }

    public DataModelerEvent(String source, DataModelTO currentModel, DataObjectTO currentDataObject, ObjectPropertyTO currentField) {
        this.source = source;
        this.currentModel = currentModel;
        this.currentDataObject = currentDataObject;
        this.currentField = currentField;
    }

    public DataModelTO getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(DataModelTO currentModel) {
        this.currentModel = currentModel;
    }

    public DataObjectTO getCurrentDataObject() {
        return currentDataObject;
    }

    public void setCurrentDataObject(DataObjectTO currentDataObject) {
        this.currentDataObject = currentDataObject;
    }

    public ObjectPropertyTO getCurrentField() {
        return currentField;
    }

    public void setCurrentField(ObjectPropertyTO currentField) {
        this.currentField = currentField;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isFrom(DataModelTO dataModel) {
        return dataModel != null && dataModel.getId() == getCurrentModel().getId();
    }

    public boolean isFrom(String source) {
        if (source != null) {
            return source.equals(this.source);
        }
        return source == this.source;
    }

    public int getId() {
        return id;
    }
}
