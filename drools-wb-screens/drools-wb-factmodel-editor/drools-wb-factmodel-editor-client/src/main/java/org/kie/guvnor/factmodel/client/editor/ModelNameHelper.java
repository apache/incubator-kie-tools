/*
 * Copyright 2012 JBoss Inc
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
package org.kie.guvnor.factmodel.client.editor;

import org.kie.guvnor.commons.ui.client.widget.HumanReadableDataTypes;
import org.kie.guvnor.factmodel.model.FieldMetaModel;

import java.util.HashMap;
import java.util.Map;

public class ModelNameHelper {

    private static Map<String, String> TYPE_DESCRIPTIONS = new HashMap<String, String>() {

        {
            putAll( HumanReadableDataTypes.getTypeDescriptions() );
        }
    };

    public String getDesc( FieldMetaModel fieldMetaModel ) {
        if ( TYPE_DESCRIPTIONS.containsKey( fieldMetaModel.type ) ) {
            return TYPE_DESCRIPTIONS.get( fieldMetaModel.type );
        }
        return fieldMetaModel.type;
    }

    public Map<String, String> getTypeDescriptions() {
        return TYPE_DESCRIPTIONS;
    }

    public boolean isUniqueName( String type ) {
        if ( getTypeDescriptions().containsKey( type ) ) {
            return false;
        }
        return true;
    }

    public void changeNameInModelNameHelper( String oldName,
                                             String newName ) {
        getTypeDescriptions().remove( oldName );
        getTypeDescriptions().put( newName,
                                   newName );
    }

    public String getUserFriendlyTypeName( String systemTypeName ) {
        if ( systemTypeName.contains( "." ) ) {
            systemTypeName = systemTypeName.substring( systemTypeName.lastIndexOf( "." ) + 1 );
        }
        String userFriendlyName = getTypeDescriptions().get( systemTypeName );
        if ( userFriendlyName == null ) {
            return systemTypeName;
        } else {
            return userFriendlyName;
        }
    }

}
