/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.model.persistence;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Property {

    String name;

    String value;

    public Property() {
    }

    public Property( String name, String value ) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Property property = ( Property ) o;

        if ( name != null ? !name.equals( property.name ) : property.name != null ) {
            return false;
        }
        return !( value != null ? !value.equals( property.value ) : property.value != null );

    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( value != null ? value.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
