package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.PropertyTypeFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PropertyTypeFactoryImpl implements PropertyTypeFactory {

    private static PropertyTypeFactory singleton = null;

    private static List<PropertyType> baseTypes = new ArrayList<PropertyType>();
    
    private static HashMap<String, PropertyType> baseTypesByClass = new HashMap<String, PropertyType>();

    private PropertyTypeFactoryImpl() {
        baseTypes.add(new PropertyTypeImpl(Short.class.getSimpleName(), Short.class.getName()));
        baseTypes.add(new PropertyTypeImpl(Integer.class.getSimpleName(), Integer.class.getName()));
        baseTypes.add(new PropertyTypeImpl(Long.class.getSimpleName(), Long.class.getName()));

        baseTypes.add(new PropertyTypeImpl(Float.class.getSimpleName(), Float.class.getName()));
        baseTypes.add(new PropertyTypeImpl(Double.class.getSimpleName(), Double.class.getName()));

        baseTypes.add(new PropertyTypeImpl(Date.class.getSimpleName(), Date.class.getName()));

        baseTypes.add(new PropertyTypeImpl(String.class.getSimpleName(), String.class.getName()));
        baseTypes.add(new PropertyTypeImpl(Boolean.class.getSimpleName(), Boolean.class.getName()));
        baseTypes.add(new PropertyTypeImpl(BigDecimal.class.getSimpleName(), BigDecimal.class.getName()));

        for (PropertyType type : baseTypes) {
            baseTypesByClass.put(type.getClassName(), type);
        }
    }

    public static PropertyTypeFactory getInstance() {
        if (singleton == null) {
            singleton = new PropertyTypeFactoryImpl();
        }
        return singleton;
    }

    @Override
    public List<PropertyType> getBasePropertyTypes() {
        return baseTypes;
    }

    @Override
    public boolean isBasePropertyType(String className) {
        return baseTypesByClass.containsKey(className);
    }
}
