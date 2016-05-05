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
package org.uberfire.ext.layout.editor.client.dnd;

import javax.enterprise.context.Dependent;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import org.uberfire.ext.layout.editor.client.components.HasDragAndDropSettings;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;

@Dependent
public class DndDataJSONConverter {

    public static final String COMPONENT_TYPE = "type";
    public static final String COMPONENT_PARAMS = "params";

    private DragTypeBeanResolver beanResolver = new DragTypeBeanResolver();

    public String generateDragComponentJSON( LayoutDragComponent dragComponent ) {
        JSONObject jsonComponent = new JSONObject();
        jsonComponent.put( COMPONENT_TYPE, new JSONString( dragComponent.getClass().getName() ) );

        if ( dragComponent instanceof HasDragAndDropSettings ) {
            JSONObject params = new JSONObject(  );

            HasDragAndDropSettings sComponent = ( HasDragAndDropSettings ) dragComponent;

            for ( String key : sComponent.getSettingsKeys() ) {
                String value = sComponent.getSettingValue( key );
                params.put( key, new JSONString( value ) );
            }

            jsonComponent.put( COMPONENT_PARAMS, params );
        }

        return jsonComponent.toString();
    }

    public LayoutDragComponent readJSONDragComponent( String json ) {
        JSONObject jsonObject = JSONParser.parseStrict( json ).isObject();

        JSONString typeValue = jsonObject.get( COMPONENT_TYPE ).isString();
        if ( typeValue != null ) {
            String type = typeValue.stringValue();

            LayoutDragComponent dragComponent = beanResolver.lookupDragTypeBean( type );

            if ( dragComponent instanceof HasDragAndDropSettings ) {
                HasDragAndDropSettings sComponent = ( HasDragAndDropSettings ) dragComponent;

                JSONObject params = jsonObject.get( COMPONENT_PARAMS ).isObject();

                if ( params != null ) {
                    for ( String key : params.keySet() ) {
                        JSONString value = params.get( key ).isString();
                        if ( value != null ) {
                            sComponent.setSettingValue( key, value.stringValue() );
                        }
                    }
                }
            }

            return dragComponent;
        }

        return null;
    }
}
