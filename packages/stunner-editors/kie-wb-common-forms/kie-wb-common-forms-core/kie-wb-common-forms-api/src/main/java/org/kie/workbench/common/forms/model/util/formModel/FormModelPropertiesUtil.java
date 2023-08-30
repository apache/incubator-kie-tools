/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.model.util.formModel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormModelPropertiesUtil {

    private static final List<String> simplePropertyTypes = new ArrayList<>();

    private static final List<String> listPropertyTypes = new ArrayList<>();

    static {
        // Primitive types
        simplePropertyTypes.add(byte.class.getName());
        simplePropertyTypes.add(short.class.getName());
        simplePropertyTypes.add(int.class.getName());
        simplePropertyTypes.add(long.class.getName());
        simplePropertyTypes.add(float.class.getName());
        simplePropertyTypes.add(double.class.getName());
        simplePropertyTypes.add(char.class.getName());
        simplePropertyTypes.add(boolean.class.getName());

        // Base Types
        simplePropertyTypes.add(String.class.getName());
        simplePropertyTypes.add(Short.class.getName());
        simplePropertyTypes.add(Integer.class.getName());
        simplePropertyTypes.add(Long.class.getName());
        simplePropertyTypes.add(Float.class.getName());
        simplePropertyTypes.add(Double.class.getName());
        simplePropertyTypes.add(Byte.class.getName());
        simplePropertyTypes.add(BigDecimal.class.getName());
        simplePropertyTypes.add(BigInteger.class.getName());
        simplePropertyTypes.add(Date.class.getName());
        simplePropertyTypes.add(Boolean.class.getName());
        simplePropertyTypes.add(Character.class.getName());

        // TODO: Replace by class.getName once GWT supports the following types
        simplePropertyTypes.add("java.time.LocalDate");
        simplePropertyTypes.add("java.time.LocalDateTime");
        simplePropertyTypes.add("java.time.LocalTime");
        simplePropertyTypes.add("java.time.OffsetDateTime");

        // List types
        // TODO: Add support to other types
        listPropertyTypes.add(List.class.getName());
        listPropertyTypes.add(ArrayList.class.getName());
    }

    public static void registerBaseType(String baseType) {
        simplePropertyTypes.add(baseType);
    }

    public static boolean isBaseType(Class clazz) {
        return isBaseType(clazz.getName());
    }

    public static boolean isBaseType(String className) {
        return simplePropertyTypes.contains(className);
    }

    public static boolean isListType(Class clazz) {
        return isListType(clazz.getName());
    }

    public static boolean isListType(String className) {
        return listPropertyTypes.contains(className);
    }
}
