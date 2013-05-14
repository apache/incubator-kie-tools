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

@Portable
public class DataModelerPropertyChangeEvent extends DataModelerEvent {

    private String propertyName;

    private Object oldValue;

    private Object newValue;

    public DataModelerPropertyChangeEvent() {
    }

    public DataModelerPropertyChangeEvent(String source, DataModelTO currentModel, DataObjectTO currentDataObject, String propertyName, Object oldValue, Object newValue) {
        super(source, currentModel, currentDataObject);
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }
}
