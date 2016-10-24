/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.Type;

public class TypeImpl implements Type {

    private String name;

    private List<Type> typeArguments;

    public TypeImpl() {
    }

    public TypeImpl( String name ) {
        this.name = name;
        this.typeArguments = new ArrayList<>( 0 );
    }


    public TypeImpl( String name, List<Type> typeArguments ) {
        this.name = name;
        this.typeArguments = typeArguments;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public List<Type> getTypeArguments() {
        return typeArguments;
    }

    @Override
    public void setTypeArguments( List<Type> typeArguments ) {
        this.typeArguments = typeArguments;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        TypeImpl type = ( TypeImpl ) o;

        if ( name != null ? !name.equals( type.name ) : type.name != null ) {
            return false;
        }
        return typeArguments != null ? typeArguments.equals( type.typeArguments ) : type.typeArguments == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = ~~result;
        result = 31 * result + ( typeArguments != null ? typeArguments.hashCode() : 0 );
        result = ~~result;
        return result;
    }

}
