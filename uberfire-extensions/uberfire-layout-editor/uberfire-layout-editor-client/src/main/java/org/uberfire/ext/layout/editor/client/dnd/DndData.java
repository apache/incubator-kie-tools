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
package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.event.dom.client.DropEvent;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;


public class DndData {

    //ie11 requires that on a dnd event, the DataTransfer format is exactly "text"
    public static final String FORMAT = "text";
    private static final String SEPARATOR = "@";

    public static String generateData( LayoutDragComponent type ) {
        if ( type instanceof GridLayoutDragComponent ) {
            return prepareData( GridLayoutDragComponent.INTERNAL_DRAG_COMPONENT, ( ( GridLayoutDragComponent ) type ).label() );
        } else {
            return prepareData( LayoutDragComponent.class.toString(), type.getClass().getName() );
        }
    }

    static String prepareData( String eventType, String eventData ) {
        return eventType + SEPARATOR + eventData;
    }


    public static String getEventType( DropEvent event ) {
        final String data = event.getData( FORMAT );
        final String[] split = data.split( SEPARATOR );
        if ( split.length > 0 ) {
            return split[0];
        }
        return "";
    }

    public static String getEventData( DropEvent event ) {
        final String data = event.getData( FORMAT );
        final String[] split = data.split( SEPARATOR );
        if ( split.length > 1 ) {
            return split[1];
        }
        return "";
    }
}
