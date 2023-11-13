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


package org.kie.workbench.common.stunner.bpmn.client.workitem;

public enum MvelDataType {

    BOOLEAN_DATA_TYPE("BooleanDataType", "Boolean"),
    ENUM_DATA_TYPE("EnumDataType", "java.lang.Object"),
    FLOAT_DATA_TYPE("FloatDataType", "Float"),
    INTEGER_DATA_TYPE("IntegerDataType", "Integer"),
    LIST_DATA_TYPE("ListDataType", "java.util.List"),
    OBJECT_DATA_TYPE("ObjectDataType", "java.lang.Object"),
    STRING_DATA_TYPE("StringDataType", "String"),
    UNDEFINED_DATA_TYPE("UndefinedDataType", "java.lang.Object");

    private final String mvelType;
    private final String javaType;

    MvelDataType(String mvelType, String javaType) {
        this.mvelType = mvelType;
        this.javaType = javaType;
    }

    public String getJavaType() {
        return javaType;
    }

    public String getMvelType() {
        return mvelType;
    }

    /**
     * Converts a MVEL datatype to Java type.
     *
     * @param dataType type to search in predefined MVEL types
     * @return The Java corresponding type e.g. String if provided type is MVEL type.
     */
    public static String getJavaTypeByMvelType(String dataType) {
        for (MvelDataType type : MvelDataType.values()) {
            if (type.getMvelType().equals(dataType)) {
                return type.getJavaType();
            }
        }

        return dataType;
    }
}
