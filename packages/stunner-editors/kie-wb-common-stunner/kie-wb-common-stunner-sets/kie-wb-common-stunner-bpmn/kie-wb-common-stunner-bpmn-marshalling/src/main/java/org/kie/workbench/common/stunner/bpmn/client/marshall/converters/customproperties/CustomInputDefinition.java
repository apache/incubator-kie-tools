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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.Optional;

import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Task;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.FormalExpressionBodyHandler;

public abstract class CustomInputDefinition<T> {

    protected final T defaultValue;
    private final String name;
    private final String type;

    public CustomInputDefinition(String name, String type, T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    private static Object evaluate(Assignment assignment) {
        FormalExpression expr = (FormalExpression) assignment.getFrom();
        return FormalExpressionBodyHandler.of(expr).getBody();
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public abstract T getValue(Task element);

    Optional<String> getStringValue(Task element) {
        for (DataInputAssociation din : element.getDataInputAssociations()) {
            DataInput targetRef = (DataInput) (din.getTargetRef());
            if (targetRef.getName().equalsIgnoreCase(name) && !din.getAssignment().isEmpty()) {
                Assignment assignment = din.getAssignment().get(0);
                return Optional.of(evaluate(assignment).toString());
            }
        }
        return Optional.empty();
    }

    public CustomInput<T> of(Task element) {
        return new CustomInput<>(this, element);
    }
}

class BooleanInput extends CustomInputDefinition<Boolean> {

    BooleanInput(String name, Boolean defaultValue) {
        super(name, "Object", defaultValue);
    }

    @Override
    public Boolean getValue(Task element) {
        return getStringValue(element)
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }
}

class StringInput extends CustomInputDefinition<String> {

    StringInput(String name, String type, String defaultValue) {
        super(name, type, defaultValue);
    }

    StringInput(String name, String defaultValue) {
        this(name, "Object", defaultValue);
    }

    @Override
    public String getValue(Task element) {
        return getStringValue(element)
                .orElse(defaultValue);
    }
}