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

import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.Visibility;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;

public class ObjectPropertyImpl extends AbstractHasAnnotations implements ObjectProperty {

    private String name;

    private String className;
    
    private String bag;
    
    private boolean multiple;

    private int modifiers = 0x0;

    private int fileOrder = -1;

    private Visibility visibility = Visibility.PUBLIC;

    private boolean _static = false;

    private boolean _final = false;

    public static final String DEFAULT_PROPERTY_BAG = "java.util.List";

    public ObjectPropertyImpl() {
        //errai marshalling
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple) {
        this(name, className, multiple, Visibility.PUBLIC, false, false);
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple, Visibility visibility, boolean isStatic, boolean isFinal) {
        this(name, className, multiple, DEFAULT_PROPERTY_BAG, visibility, isStatic, isFinal);
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple, String bag) {
        this(name, className, multiple, bag, Visibility.PUBLIC, false, false);
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple, String bag, Visibility visibility, boolean isStatic, boolean isFinal) {
        this.name = name;
        this.className = className;
        this.bag = bag;
        this.multiple = multiple;
        this.visibility = visibility;
        this._static = isStatic;
        this._final = isFinal;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getBag() {
        return bag;
    }

    @Override
    public void setBag(String bag) {
        this.bag = bag;
    }

    @Override
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
        if (!multiple) {
            setBag(null);
        }
    }

    @Override
    public boolean isArray() {
        //TODO check this when array support will be added
        return getClassName() != null && className.endsWith("[]");
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public boolean isBaseType() {
        return PropertyTypeFactoryImpl.getInstance().isBasePropertyType(getClassName());
    }

    @Override
    public boolean isPrimitiveType() {
        return NamingUtils.isPrimitiveTypeClass(getClassName());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isStatic() {
        return _static;
    }

    @Override
    public boolean isFinal() {
        return _final;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    public int getFileOrder() {
        return fileOrder;
    }

    public void setFileOrder( int fileOrder ) {
        this.fileOrder = fileOrder;
    }

    @Override
    public boolean isPackagePrivate() {
        return Visibility.PACKAGE_PRIVATE == visibility;
    }

    @Override
    public boolean isPublic() {
        return Visibility.PUBLIC == visibility;
    }

    @Override
    public boolean isPrivate() {
        return Visibility.PRIVATE == visibility;
    }

    @Override
    public boolean isProtected() {
        return Visibility.PROTECTED == visibility;
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

        ObjectPropertyImpl that = ( ObjectPropertyImpl ) o;

        if ( multiple != that.multiple ) {
            return false;
        }
        if ( modifiers != that.modifiers ) {
            return false;
        }
        if ( fileOrder != that.fileOrder ) {
            return false;
        }
        if ( _static != that._static ) {
            return false;
        }
        if ( _final != that._final ) {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null ) {
            return false;
        }
        if ( className != null ? !className.equals( that.className ) : that.className != null ) {
            return false;
        }
        if ( bag != null ? !bag.equals( that.bag ) : that.bag != null ) {
            return false;
        }
        return visibility == that.visibility;

    }

    @Override public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( name != null ? name.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( className != null ? className.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( bag != null ? bag.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( multiple ? 1 : 0 );
        result = ~~result;
        result = 31 * result + modifiers;
        result = ~~result;
        result = 31 * result + fileOrder;
        result = ~~result;
        result = 31 * result + ( visibility != null ? visibility.hashCode() : 0 );
        result = ~~result;
        result = 31 * result + ( _static ? 1 : 0 );
        result = ~~result;
        result = 31 * result + ( _final ? 1 : 0 );
        result = ~~result;
        return result;
    }
}