/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.properties.editor.model;

/**
 * A Property Editor CDI event.
 * Class that groups a PropertyEditorChangeEvent information.
 * This event is fired by property editor when a fields has its value changed.
 */
public class PropertyEditorChangeEvent {

    private final PropertyEditorFieldInfo property;
    private final String newValue;
    private final String idEvent;

    /**
     * Creates a new PropertyEditorChangeEvent
     * @param property the property changed
     * @param newValue the new  value of the field
     */
    public PropertyEditorChangeEvent( PropertyEditorFieldInfo property,
                                      String newValue ) {
        this.idEvent = property.getEventId();
        this.property = property;
        this.newValue = newValue;
    }

    public PropertyEditorFieldInfo getProperty() {
        return property;
    }

    public String getNewValue() {
        return newValue;
    }
}
