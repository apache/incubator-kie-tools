/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.model;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.services.datamodeller.core.Annotation;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.uberfire.ext.properties.editor.model.CustomPropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldOption;

public class DataModelerPropertyEditorFieldInfo extends CustomPropertyEditorFieldInfo {

    protected DataObject currentDataObject;

    protected ObjectProperty currentObjectProperty;

    protected Annotation currentValue;

    protected Annotation newValue;

    protected Map<String, Object> currentValues = new HashMap<String, Object>();

    public DataModelerPropertyEditorFieldInfo(String label, String currentStringValue, Class<?> customEditorClass,
                                              DataObject currentDataObject, ObjectProperty currentObjectProperty,
                                              Annotation currentValue, Annotation newValue) {

        super(label, currentStringValue, customEditorClass);
        this.currentDataObject = currentDataObject;
        this.currentObjectProperty = currentObjectProperty;
        this.currentValue = currentValue;
        this.newValue = newValue;
    }

    public DataModelerPropertyEditorFieldInfo(String label, String currentStringValue, Class<?> customEditorClass) {
        super(label, currentStringValue, customEditorClass);
    }

    public Annotation getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Annotation currentValue) {
        this.currentValue = currentValue;
    }

    public Annotation getNewValue() {
        return newValue;
    }

    public void setNewValue(Annotation newValue) {
        this.newValue = newValue;
    }

    public void removeCurrentValue(String name) {
        currentValues.remove(name);
    }

    public void setCurrentValue(String name, Object value) {
        currentValues.put(name, value);
    }

    public Object getCurrentValue(String name) {
        return currentValues.get(name);
    }

    public void clearCurrentValues() {
        currentValues.clear();
    }

    public boolean isDisabled() {
        return super.getOptions().contains(PropertyEditorFieldOption.DISABLED);
    }

    public void setDisabled(boolean disabled) {
        getOptions().remove(PropertyEditorFieldOption.DISABLED);
        if (disabled) {
            withOptions(PropertyEditorFieldOption.DISABLED);
        }
    }
}
