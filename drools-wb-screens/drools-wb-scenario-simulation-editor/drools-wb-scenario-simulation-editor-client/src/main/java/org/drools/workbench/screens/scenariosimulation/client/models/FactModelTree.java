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
package org.drools.workbench.screens.scenariosimulation.client.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to recursively represent a given fact with its ModelFields eventually expanded
 */
public class FactModelTree {

    private final String factName;  // The name of the asset

    private final Map<String, String> simpleProperties; // Map of the properties: key = property name, value = property value
    private final Map<String, String> expandableProperties = new HashMap<>(); // Map of the expandable properties: key = property name, value = property value

    public FactModelTree(String factName, Map<String, String> simpleProperties) {
        this.factName = factName;
        this.simpleProperties = simpleProperties;
    }

    public String getFactName() {
        return factName;
    }

    public Map<String, String> getSimpleProperties() {
        return simpleProperties;
    }

    public Map<String, String> getExpandableProperties() {
        return expandableProperties;
    }

    public void addSimpleProperty(String propertyName, String propertyType) {
        simpleProperties.put(propertyName, propertyType);
    }

    public void addExpandableProperty(String propertyName, String propertyType) {
        expandableProperties.put(propertyName, propertyType);
    }

    public void removeSimpleProperty(String propertyName) {
        simpleProperties.remove(propertyName);
    }


    @Override
    public String toString() {
        return "FactModelTree{" +
                "factName='" + factName + '\'' +
                ", simpleProperties=" + simpleProperties +
                ", expandableProperties=" + expandableProperties +
                '}';
    }
}
