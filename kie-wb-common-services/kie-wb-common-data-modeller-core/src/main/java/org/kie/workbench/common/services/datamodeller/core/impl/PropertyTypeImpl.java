package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.PropertyType;

public class PropertyTypeImpl implements PropertyType {

    String name;
    
    String className;

    public PropertyTypeImpl() {
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
}
