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

import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.Parameter;
import org.kie.workbench.common.services.datamodeller.core.Type;
import org.kie.workbench.common.services.datamodeller.core.Visibility;

public class MethodImpl extends AbstractHasAnnotations implements Method {

    private String name;

    private List<Parameter> parameters = new ArrayList<>( );

    private String body;

    private Type returnType;

    private Visibility visibility = Visibility.PACKAGE_PRIVATE;

    public MethodImpl() {
    }

    public MethodImpl( String name, List<Parameter> parameters, String body, Type returnType, Visibility visibility ) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.returnType = returnType;
        this.visibility = visibility;
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
    public List<Parameter> getParameters() {
        return parameters;
    }

    @Override
    public void setParameters( List<Parameter> parameters ) {
        this.parameters = parameters;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody( String body ) {
        this.body = body;
    }

    @Override
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public void setReturnType( Type returnType ) {
        this.returnType = returnType;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        MethodImpl method = ( MethodImpl ) o;

        if ( name != null ? !name.equals( method.name ) : method.name != null ) {
            return false;
        }
        if ( body != null ? !body.equals( method.body ) : method.body != null ) {
            return false;
        }
        if ( parameters != null ? !parameters.equals( method.parameters ) : method.parameters != null ) {
            return false;
        }
        if ( returnType != null ? !returnType.equals( method.returnType ) : method.returnType != null ) {
            return false;
        }
        return visibility == method.visibility;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( body != null ? body.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( parameters != null ? parameters.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( returnType != null ? returnType.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( visibility != null ? visibility.hashCode() : 0 );
        result = ~~result;
        return result;
    }

    @Override
    public boolean isPackagePrivate() {
        return visibility == Visibility.PACKAGE_PRIVATE;
    }

    @Override
    public boolean isPublic() {
        return visibility == Visibility.PUBLIC;
    }

    @Override
    public boolean isPrivate() {
        return visibility == Visibility.PRIVATE;
    }

    @Override
    public boolean isProtected() {
        return visibility == Visibility.PROTECTED;
    }

    @Override
    public Visibility getVisibilty() {
        return visibility;
    }

    @Override
    public void setVisibility( Visibility visibility ) {
        this.visibility = visibility;
    }

}
