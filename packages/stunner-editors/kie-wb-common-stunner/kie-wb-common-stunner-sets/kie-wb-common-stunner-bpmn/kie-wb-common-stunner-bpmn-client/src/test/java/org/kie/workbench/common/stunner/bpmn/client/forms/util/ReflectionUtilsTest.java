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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import java.lang.reflect.Field;

import com.google.gwt.junit.GWTMockUtilities;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionUtilsTest {

    public void setUp() throws Exception {
        GWTMockUtilities.disarm();
    }

    protected <T> T getFieldValue(Class parent, Object instance, String fieldName) {
        Field inputField = FieldUtils.getField(parent, fieldName, true);
        try {
            return (T) inputField.get(instance);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }

    protected void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            FieldUtils.writeField(instance, fieldName, value, true);
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
