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

import org.kie.workbench.common.services.datamodeller.core.JavaType;
import org.kie.workbench.common.services.datamodeller.core.JavaTypeKind;
import org.kie.workbench.common.services.datamodeller.core.Visibility;

public abstract class AbstractJavaType extends AbstractHasAnnotations implements JavaType {

    protected String name;

    protected String packageName;

    protected JavaType enclosingType;

    protected List<JavaType> nestedTypes = new ArrayList<JavaType>(  );

    protected Visibility visibility = Visibility.PACKAGE_PRIVATE;

    protected JavaTypeKind typeKind = JavaTypeKind.CLASS;

    protected AbstractJavaType() {
        //errai marshalling
    }

    protected AbstractJavaType( String packageName, String name, JavaTypeKind typeKind ) {
        this.packageName = packageName;
        this.name = name;
        this.typeKind = typeKind;
    }

    protected AbstractJavaType( String packageName, String name, JavaTypeKind typeKind, Visibility visibility ) {
        this( packageName, name, typeKind );
        this.visibility = visibility;
    }

    public JavaTypeKind getTypeKind() {
        return typeKind;
    }

    @Override
    public boolean isClass() {
        return typeKind == JavaTypeKind.CLASS;
    }

    @Override
    public boolean isEnum() {
        return typeKind == JavaTypeKind.ENUM;
    }

    @Override
    public boolean isInterface() {
        return typeKind == JavaTypeKind.INTERFACE;
    }

    @Override
    public boolean isAnnotation() {
        return typeKind == JavaTypeKind.ANNOTATION;
    }

    @Override
    public JavaType getEnclosingType() {
        return enclosingType;
    }

    @Override
    public String getClassName() {
        return ( (packageName != null && !"".equals(packageName)) ? packageName+"." : "") + getName();
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
    public List<JavaType> getNestedTypes() {
        return nestedTypes;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName( String packageName ) {
        this.packageName = packageName;
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

        AbstractJavaType that = ( AbstractJavaType ) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( packageName != null ? !packageName.equals( that.packageName ) : that.packageName != null ) {
            return false;
        }
        if ( enclosingType != null ? !enclosingType.equals( that.enclosingType ) : that.enclosingType != null ) {
            return false;
        }
        if ( nestedTypes != null ? !nestedTypes.equals( that.nestedTypes ) : that.nestedTypes != null ) {
            return false;
        }
        if ( visibility != that.visibility ) {
            return false;
        }
        return typeKind == that.typeKind;

    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( packageName != null ? packageName.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( enclosingType != null ? enclosingType.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( nestedTypes != null ? nestedTypes.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( visibility != null ? visibility.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( typeKind != null ? typeKind.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
