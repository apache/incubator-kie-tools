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

import java.lang.reflect.Modifier;

import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;

public class ObjectPropertyImpl extends AbstractHasAnnotations implements ObjectProperty {

    private String name;

    private String className;
    
    private String bag;
    
    private boolean multiple;

    private int modifiers = 0x0;
    
    private static final String DEFAULT_PROPERTY_BAG = "java.util.List";

    public ObjectPropertyImpl(String name, String className, boolean multiple) {
        this(name, className, multiple, 0x0);
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple, int modifiers) {
        this.name = name;
        this.className = className;
        this.bag = DEFAULT_PROPERTY_BAG;
        this.multiple = multiple;
        this.modifiers = modifiers;
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple, String bag, int modifiers) {
        this.name = name;
        this.className = className;
        this.multiple = multiple;
        this.bag = bag;
        this.modifiers = modifiers;
    }

    public ObjectPropertyImpl(String name, String className, boolean multiple, String bag) {
        this(name, className, multiple, bag, 0x0);
    }

    @Override
    public String getClassName() {
        return className;
    }

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
        return NamingUtils.getInstance().isPrimitiveTypeClass(getClassName());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override public boolean isStatic() {
        return Modifier.isStatic( modifiers );
    }

    @Override public boolean isFinal() {
        return Modifier.isFinal( modifiers );
    }
}
