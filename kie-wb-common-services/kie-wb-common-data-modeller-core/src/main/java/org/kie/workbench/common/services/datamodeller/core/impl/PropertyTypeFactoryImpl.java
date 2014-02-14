package org.kie.workbench.common.services.datamodeller.core.impl;

import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.PropertyTypeFactory;
import org.kie.workbench.common.services.datamodeller.util.NamingUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
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
        baseTypes.add(new PropertyTypeImpl(Byte.class.getSimpleName(), Byte.class.getName()));

        baseTypes.add(new PropertyTypeImpl(Float.class.getSimpleName(), Float.class.getName()));
        baseTypes.add(new PropertyTypeImpl(Double.class.getSimpleName(), Double.class.getName()));

        baseTypes.add(new PropertyTypeImpl(Date.class.getSimpleName(), Date.class.getName()));

        baseTypes.add(new PropertyTypeImpl(Boolean.class.getSimpleName(), Boolean.class.getName()));
        baseTypes.add(new PropertyTypeImpl(String.class.getSimpleName(), String.class.getName()));
        baseTypes.add(new PropertyTypeImpl(Character.class.getSimpleName(), Character.class.getName()));

        baseTypes.add(new PropertyTypeImpl(BigDecimal.class.getSimpleName(), BigDecimal.class.getName()));
        baseTypes.add(new PropertyTypeImpl(BigInteger.class.getSimpleName(), BigInteger.class.getName()));


        baseTypes.add(new PropertyTypeImpl(NamingUtils.BYTE, NamingUtils.BYTE));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.SHORT, NamingUtils.SHORT));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.INT, NamingUtils.INT));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.LONG, NamingUtils.LONG));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.FLOAT, NamingUtils.FLOAT));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.DOUBLE, NamingUtils.DOUBLE));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.BOOLEAN, NamingUtils.BOOLEAN));
        baseTypes.add(new PropertyTypeImpl(NamingUtils.CHAR, NamingUtils.CHAR));

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

    @Override
    public boolean isPrimitivePropertyType(String className) {
        PropertyType type = baseTypesByClass.get(className);
        return type != null && type.isPrimitive();
    }
}
