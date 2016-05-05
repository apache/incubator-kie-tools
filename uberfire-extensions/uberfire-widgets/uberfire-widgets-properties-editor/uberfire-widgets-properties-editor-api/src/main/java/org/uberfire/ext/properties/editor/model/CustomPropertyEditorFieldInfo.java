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

import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;

public class CustomPropertyEditorFieldInfo extends PropertyEditorFieldInfo {

    private Class<?> customEditorClass;

    public CustomPropertyEditorFieldInfo( String label, String stringValue, Class<?> customEditorClass ) {
        super( label, stringValue, PropertyEditorType.CUSTOM );
        this.customEditorClass = customEditorClass;
    }

    public Class<?> getCustomEditorClass() {
        return customEditorClass;
    }

    public void setCustomEditorClass( Class<?> customEditorClass ) {
        this.customEditorClass = customEditorClass;
    }
}
