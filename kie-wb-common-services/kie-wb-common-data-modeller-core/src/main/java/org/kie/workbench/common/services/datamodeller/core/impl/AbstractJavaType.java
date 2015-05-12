/**
 * Copyright 2012 JBoss Inc
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
}
