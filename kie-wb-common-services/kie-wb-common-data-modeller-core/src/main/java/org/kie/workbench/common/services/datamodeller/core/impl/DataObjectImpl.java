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

import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataObjectImpl extends AbstractHasAnnotations implements DataObject {

    private String name;

    private String packageName;
    
    private String superClassName;

    private List<String> imports = new ArrayList<String>();

    private Map<String, ObjectProperty> properties = new HashMap<String, ObjectProperty>();

    int modifiers = 0x0;

    public DataObjectImpl(String packageName, String name, int modifiers) {
        this.setName(name);
        this.packageName = packageName;
        this.modifiers = modifiers;
    }

    public DataObjectImpl(String packageName, String name) {
        this(packageName, name, Modifier.PUBLIC);
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
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public String getClassName() {
        return ( (packageName != null && !"".equals(packageName)) ? packageName+"." : "") + getName();
    }

    @Override
    public boolean hasSuperClass() {
        return superClassName != null;
    }

    @Override
    public String getSuperClassName() {
        return superClassName;
    }

    @Override
    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    @Override
    public Map<String, ObjectProperty> getProperties() {
        return properties;
    }

    @Override
    public ObjectProperty addProperty(String name, String className) {
        return addProperty(name, className, false);
    }

    @Override
    public ObjectProperty addProperty(String name, String className, int modifiers) {
        return addProperty(name, className, false, modifiers);
    }

    @Override
    public ObjectProperty addProperty(String name, String className, boolean multiple) {
        ObjectProperty property = new ObjectPropertyImpl(name, className, multiple);
        properties.put(name, property);
        return property;
    }

    @Override
    public ObjectProperty addProperty(String name, String className, boolean multiple, int modifiers) {
        ObjectProperty property = new ObjectPropertyImpl(name, className, multiple, modifiers);
        properties.put(name, property);
        return property;
    }


    @Override
    public ObjectProperty addProperty(String name, String className, boolean multiple, String bag) {
        ObjectProperty property = new ObjectPropertyImpl(name, className, multiple, bag);
        properties.put(name, property);
        return property;
    }

    @Override
    public ObjectProperty addProperty(String name, String className, boolean multiple, String bag, int modifiers) {
        ObjectProperty property = new ObjectPropertyImpl(name, className, multiple, bag, modifiers);
        properties.put(name, property);
        return property;
    }

    @Override
    public ObjectProperty removeProperty(String name) {
        return properties.remove(name);
    }

    @Override
    public List<String> getImports() {
        return imports;
    }

    @Override
    public boolean isInterface() {
        return Modifier.isInterface(modifiers);
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }
}
