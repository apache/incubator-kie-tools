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
package org.uberfire.ext.wires.core.client.properties;

import java.util.List;

import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;
import org.uberfire.ext.properties.editor.model.PropertyEditorType;

/**
 * This is a work-around for being able to set the ComboValues and override a method on PropertyEditorFieldInfo
 * Class PropertyEditorFieldInfo initialized ComboValues to null so we can't override a method with an anonymous
 * sub-class and then call getComboValues().addAll(...)
 */
public class WiresComboPropertyEditorFieldInfo extends PropertyEditorFieldInfo {

    public WiresComboPropertyEditorFieldInfo( final String label,
                                              final String currentStringValue,
                                              final List<String> comboValues ) {
        super( label,
               currentStringValue,
               PropertyEditorType.COMBO );
        withComboValues( comboValues );
    }

}
