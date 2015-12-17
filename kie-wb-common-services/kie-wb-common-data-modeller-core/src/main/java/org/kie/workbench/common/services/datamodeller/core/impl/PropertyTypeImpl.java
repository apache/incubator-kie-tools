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

import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;

public class PropertyTypeImpl implements PropertyType {

    String name;
    
    String className;

    public PropertyTypeImpl() {
        //errai marshalling
    }

    public PropertyTypeImpl(String name, String className) {
        this.name = name;
        this.className = className;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;    
    }

    @Override
    public boolean isBaseType() {
        return PropertyTypeFactoryImpl.getInstance().isBasePropertyType(className);
    }

    @Override
    public boolean isPrimitive() {
        return NamingUtils.isPrimitiveTypeId(className);
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PropertyTypeImpl that = ( PropertyTypeImpl ) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        return !( className != null ? !className.equals( that.className ) : that.className != null );

    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( className != null ? className.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
