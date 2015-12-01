/*
* Copyright 2015 JBoss Inc
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
package org.uberfire.ext.layout.editor.client.dnd.mocks;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import org.uberfire.ext.layout.editor.client.components.HasDragAndDropSettings;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.dnd.DndDataJSONConverter;

public class DndDataJSONConverterMock extends DndDataJSONConverter {
    private LayoutDragComponent component;
    private Gson gson = new Gson();

    @Override
    public String generateDragComponentJSON( LayoutDragComponent dragComponent ) {
        component = dragComponent;
        Map<String, Object> componentMap = new HashMap<String, Object>( );
        componentMap.put( COMPONENT_TYPE, dragComponent.getClass().getName() );

        if ( dragComponent instanceof HasDragAndDropSettings ) {
            HasDragAndDropSettings settingsComponent = (HasDragAndDropSettings) dragComponent;

            if ( settingsComponent.getSettingsKeys() != null ) {
                Map<String, String> params = new HashMap<String, String>();

                for ( String key : settingsComponent.getSettingsKeys() ) {
                    String value = settingsComponent.getSettingValue( key );
                    if ( value != null ) {
                        params.put( key, value );
                    }
                }
                componentMap.put( COMPONENT_PARAMS, params );
            }
        }

        return gson.toJson( componentMap );
    }

    @Override
    public LayoutDragComponent readJSONDragComponent( String json ) {
        return component;
    }
}
