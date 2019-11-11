/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.PropertyType;
import org.kie.workbench.common.services.datamodeller.core.PropertyTypeFactory;

import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BOOLEAN;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.BYTE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.CHAR;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.DOUBLE;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.FLOAT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.INT;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.LONG;
import static org.kie.workbench.common.services.datamodel.util.PrimitiveUtilities.SHORT;

public class PropertyTypeFactoryImpl implements PropertyTypeFactory {

    private static List<PropertyType> baseTypes = new ArrayList<PropertyType>();

    private static HashMap<String, PropertyType> baseTypesByClass = new HashMap<String, PropertyType>();

    //needs to be public for errai marshalling
    public PropertyTypeFactoryImpl() {
        baseTypes.add(new PropertyTypeImpl("Short", Short.class.getName()));
        baseTypes.add(new PropertyTypeImpl("Integer", Integer.class.getName()));
        baseTypes.add(new PropertyTypeImpl("Long", Long.class.getName()));
        baseTypes.add(new PropertyTypeImpl("Byte", Byte.class.getName()));

        baseTypes.add(new PropertyTypeImpl("Float", Float.class.getName()));
        baseTypes.add(new PropertyTypeImpl("Double", Double.class.getName()));

        baseTypes.add(new PropertyTypeImpl("Date", Date.class.getName()));
        // TODO: Replace by class.getName once GWT supports the following types
        baseTypes.add(new PropertyTypeImpl("LocalDate", "java.time.LocalDate"));
        baseTypes.add(new PropertyTypeImpl("LocalDateTime", "java.time.LocalDateTime"));
        baseTypes.add(new PropertyTypeImpl("LocalTime", "java.time.LocalTime"));
        baseTypes.add(new PropertyTypeImpl("OffsetDateTime", "java.time.OffsetDateTime"));

        baseTypes.add(new PropertyTypeImpl("Boolean", Boolean.class.getName()));
        baseTypes.add(new PropertyTypeImpl("String", String.class.getName()));
        baseTypes.add(new PropertyTypeImpl("Character", Character.class.getName()));

        baseTypes.add(new PropertyTypeImpl("BigDecimal", BigDecimal.class.getName()));
        baseTypes.add(new PropertyTypeImpl("BigInteger", BigInteger.class.getName()));

        baseTypes.add(new PropertyTypeImpl(BYTE, BYTE));
        baseTypes.add(new PropertyTypeImpl(SHORT, SHORT));
        baseTypes.add(new PropertyTypeImpl(INT, INT));
        baseTypes.add(new PropertyTypeImpl(LONG, LONG));
        baseTypes.add(new PropertyTypeImpl(FLOAT, FLOAT));
        baseTypes.add(new PropertyTypeImpl(DOUBLE, DOUBLE));
        baseTypes.add(new PropertyTypeImpl(BOOLEAN, BOOLEAN));
        baseTypes.add(new PropertyTypeImpl(CHAR, CHAR));

        for (PropertyType type : baseTypes) {
            baseTypesByClass.put(type.getClassName(), type);
        }
    }

    public static PropertyTypeFactory getInstance() {

        return HoldInstance.INSTANCE;
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

    private static class HoldInstance {

        private static final PropertyTypeFactoryImpl INSTANCE = new PropertyTypeFactoryImpl();
    }
}
