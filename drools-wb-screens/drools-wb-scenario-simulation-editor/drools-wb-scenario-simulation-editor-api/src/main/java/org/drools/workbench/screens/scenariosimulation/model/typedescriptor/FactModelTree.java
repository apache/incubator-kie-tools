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
package org.drools.workbench.screens.scenariosimulation.model.typedescriptor;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Class used to recursively represent a given fact with its ModelFields eventually expanded
 */
@Portable
public class FactModelTree {

    public enum Type {
        INPUT,
        DECISION,
        UNDEFINED
    }

    private String factName;  // The name of the asset
    private String fullPackage;  // The package of the asset
    private boolean isSimple = false;

    private Map<String, String> simpleProperties; // Map of the properties: key = property name, value = property value
    private Map<String, String> expandableProperties = new HashMap<>(); // Map of the expandable properties: key = property name, value = property value
    private Type type;

    public FactModelTree() {
        // CDI
    }

    /**
     * Call this constructor to have a <code>FactModelTree</code> with <b>UNDEFINED</b> <code>Type</code>
     * @param factName
     * @param fullPackage
     * @param simpleProperties
     */
    public FactModelTree(String factName, String fullPackage, Map<String, String> simpleProperties) {
        this(factName, fullPackage, simpleProperties, Type.UNDEFINED);
    }

    /**
     * Call this constructor to specify the <code>FactModelTree</code>' <code>Type</code>
     * @param factName
     * @param fullPackage
     * @param simpleProperties
     * @param type
     */
    public FactModelTree(String factName, String fullPackage, Map<String, String> simpleProperties, Type type) {
        this.factName = factName;
        this.fullPackage = fullPackage;
        this.simpleProperties = simpleProperties;
        this.type = type;
    }

    public String getFactName() {
        return factName;
    }

    public String getFullPackage() {
        return fullPackage;
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

    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public Type getType() {
        return type;
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
