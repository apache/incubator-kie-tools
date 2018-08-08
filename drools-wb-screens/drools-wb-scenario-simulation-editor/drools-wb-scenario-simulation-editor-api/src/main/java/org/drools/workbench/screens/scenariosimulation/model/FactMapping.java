/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.model;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * It describes how to reach a single property of a fact
 */
@Portable
public class FactMapping {

    /**
     * Expression to reach the property. I.e. person.fullName.last
     */
    private List<ExpressionElement> expressionElements = new LinkedList<>();

    /**
     * Identifier of this expression (it contains the type of expression, i.e. given/expected)
     */
    private ExpressionIdentifier expressionIdentifier;

    /**
     * Identify the fact by name and class name
     */
    private FactIdentifier factIdentifier;

    /**
     * String name of the type of the property described by this class
     */
    private String className;

    /**
     * Used to store column position in the UI so it can be saved and restore
     */
    private int columnPosition;

    public FactMapping() {
    }

    public FactMapping(ExpressionIdentifier expressionIdentifier, FactIdentifier factIdentifier, int columnPosition) {
        this.expressionIdentifier = expressionIdentifier;
        this.className = factIdentifier.getClassName();
        this.factIdentifier = factIdentifier;
        this.columnPosition = columnPosition;
    }

    public String getFullExpression() {
        return expressionElements.stream().map(ExpressionElement::getStep).collect(Collectors.joining("."));
    }

    public List<ExpressionElement> getExpressionElements() {
        return expressionElements;
    }

    public void addExpressionElement(String stepName, String className) {
        this.className = className;
        expressionElements.add(new ExpressionElement(stepName));
    }

    public String getClassName() {
        return className;
    }

    public ExpressionIdentifier getExpressionIdentifier() {
        return expressionIdentifier;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public void setColumnPosition(int columnPosition) {
        this.columnPosition = columnPosition;
    }

    public int getColumnPosition() {
        return columnPosition;
    }
}
