/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.uberfire.backend.vfs.Path;

public class DataObjectImpl extends JavaClassImpl implements DataObject {

    private List<ObjectProperty> properties = new ArrayList<ObjectProperty>();

    public DataObjectImpl() {

    }

    public DataObjectImpl( String packageName, String name ) {
        super( packageName, name, Visibility.PUBLIC );
    }

    public DataObjectImpl( String packageName, String name, Visibility visibility, boolean isAbstract, boolean isFinal ) {
        super( packageName, name, visibility, isAbstract, isFinal );
    }

    @Override
    public List<ObjectProperty> getProperties() {
        return properties;
    }

    @Override
    public ObjectProperty addProperty( String name, String className ) {
        return addProperty( name, className, false );
    }

    @Override
    public ObjectProperty addProperty( String name, String className, Visibility visibility, boolean isStatic, boolean isFinal) {
        return addProperty( name, className, false, visibility, isStatic, isFinal );
    }

    @Override
    public ObjectProperty addProperty( String name, String className, boolean multiple ) {
        return addProperty( name, className, multiple, Visibility.PUBLIC, false, false );
    }

    @Override
    public ObjectProperty addProperty( String name, String className, boolean multiple, String bag ) {
        return addProperty( name, className, multiple, bag, Visibility.PUBLIC, false, false );
    }

    @Override
    public ObjectProperty addProperty( String name, String className, boolean multiple, Visibility visibility, boolean isStatic, boolean isFinal ) {
        return addProperty( new ObjectPropertyImpl( name, className, multiple, visibility, isStatic, isFinal ) );
    }

    @Override
    public ObjectProperty addProperty( String name, String className, boolean multiple, String bag, Visibility visibility, boolean isStatic, boolean isFinal ) {
        ObjectProperty property = new ObjectPropertyImpl( name, className, multiple, bag, visibility, isStatic, isFinal );
        return addProperty( property );
    }

    @Override
    public ObjectProperty addProperty( ObjectProperty property ) {
        if ( property == null ) {
            return null;
        }
        removeProperty( property.getName() );
        properties.add( property );
        return property;
    }

    @Override
    public ObjectProperty removeProperty( String name ) {
        ObjectProperty removedProperty = getProperty( name );
        if ( removedProperty != null ) properties.remove( removedProperty );
        return removedProperty;
    }

    @Override
    public ObjectProperty getProperty( String name ) {
        if ( name == null ) return null;
        for ( ObjectProperty property : properties ) {
            if ( name.equals( property.getName() ) ) {
                return property;
            }
        }
        return null;
    }

    @Override public ObjectProperty getUnManagedProperty( String propertyName ) {
        if ( propertyName == null ) return null;
        for ( ObjectProperty property : getUnmanagedProperties() ) {
            if ( propertyName.equals( property.getName() ) ) {
                return property;
            }
        }
        return null;
    }

    @Override public List<ObjectProperty> getUnmanagedProperties() {
        return new ArrayList<ObjectProperty>(  );
    }

    @Override
    public boolean hasProperty( String name ) {
        if ( name == null ) return false;
        for ( ObjectProperty property : properties ) {
            if ( name.equals( property.getName() ) ) return true;
        }
        return false;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        DataObjectImpl that = ( DataObjectImpl ) o;

        return !( properties != null ? !properties.equals( that.properties ) : that.properties != null );

    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( properties != null ? properties.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
