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


package org.kie.workbench.common.forms.dynamic.service.shared.impl.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DynamicModelConstraints {

    private String modelType;

    private Map<String, List<FieldConstraint>> fieldConstraints = new HashMap<>();

    public DynamicModelConstraints(@MapsTo("modelType") String modelType) {
        this.modelType = modelType;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public Map<String, List<FieldConstraint>> getFieldConstraints() {
        return fieldConstraints;
    }

    public void setFieldConstraints(Map<String, List<FieldConstraint>> fieldConstraints) {
        this.fieldConstraints = fieldConstraints;
    }

    public void addConstraintForField(String fieldName,
                                      FieldConstraint constraint) {
        List existingFieldConstraint = fieldConstraints.get(fieldName);

        if (existingFieldConstraint == null) {
            existingFieldConstraint = new ArrayList();
            fieldConstraints.put(fieldName,
                                 existingFieldConstraint);
        }

        existingFieldConstraint.add(constraint);
    }
}
