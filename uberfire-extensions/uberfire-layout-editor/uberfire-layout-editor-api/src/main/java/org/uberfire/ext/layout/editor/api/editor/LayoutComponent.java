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

package org.uberfire.ext.layout.editor.api.editor;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class LayoutComponent {

    private String dragTypeName;

    private Map<String, String> properties = new HashMap<String, String>();

    public LayoutComponent() {
    }

    public LayoutComponent( Class dragTypeclass ) {
        this.dragTypeName = dragTypeclass.getName();
    }

    public LayoutComponent( String dragType ) {
        this.dragTypeName = dragType;
    }

    public String getDragTypeName() {
        return dragTypeName;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof LayoutComponent ) ) {
            return false;
        }

        LayoutComponent that = (LayoutComponent) o;

        if ( dragTypeName != null ? !dragTypeName.equals( that.dragTypeName ) : that.dragTypeName != null ) {
            return false;
        }
        return !( properties != null ? !properties.equals( that.properties ) : that.properties != null );

    }

    @Override
    public int hashCode() {
        int result = dragTypeName != null ? dragTypeName.hashCode() : 0;
        result = 31 * result + ( properties != null ? properties.hashCode() : 0 );
        return result;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void addProperty( String key,
                             String value ) {
        properties.put( key, value );
    }

    public void addProperties( Map<String, String> properties ) {
        for ( String key : properties.keySet() ) {
            this.properties.put( key, properties.get( key ) );
        }
    }

    public void removeParameter( String key ) {
        properties.remove( key );
    }

    public boolean isFromMyDragTypeType( Class dragType ) {
        return dragTypeName.equalsIgnoreCase( dragType.getName() );
    }

    @Override
    public String toString() {
        return "LayoutComponent{" +
                "dragTypeName='" + dragTypeName + '\'' +
                ", properties=" + properties +
                '}';
    }
}
