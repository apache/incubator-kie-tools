/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.dmn;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.feel.BuiltInType;

public class ClientDMNType {

    private String namespace;
    private String name;
    private String id;
    private boolean collection;
    private boolean composite;
    private Map<String, ClientDMNType> fields = new HashMap<>();
    private BuiltInType feelType;
    private ClientDMNType baseType;

    public ClientDMNType(String namespace, String name, String id, boolean isCollection, BuiltInType feelType) {
        this.namespace = namespace;
        this.name = name;
        this.id = id;
        this.collection = isCollection;
        this.feelType = feelType;
        this.composite = false;
    }

    public ClientDMNType(String namespace, String name, String id, boolean isCollection, boolean isComposite) {
        this.namespace = namespace;
        this.name = name;
        this.id = id;
        this.collection = isCollection;
        this.composite = isComposite;
    }

    public ClientDMNType(String namespace, String name, String id, boolean isCollection, boolean isComposite, Map<String, ClientDMNType> fields, BuiltInType feelType) {
        this(namespace, name, id, isCollection, feelType);
        this.fields = fields;
        this.composite = isComposite;
    }

    /**
     * It *copies* the current ClientDMNType, setting it as Collection, with isCollection = true.
     * Please note, fields parameter reference should be the same for both object, the original one
     * and the copied one.
     * @return A new instance of ClientDMNType with the *same* fields value of the current one but isCollection set true
     */
    public ClientDMNType copyAsCollection() {
        return new ClientDMNType(namespace, name, id, true, composite, fields, feelType);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(final boolean collection) {
        this.collection = collection;
    }

    public boolean isComposite() {
        return composite;
    }

    public void setIsComposite(final boolean isComposite) {
        this.composite = isComposite;
    }

    public Map<String, ClientDMNType> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public void addField(String fieldName, ClientDMNType clientDMNType) {
        this.fields.put(fieldName, clientDMNType);
    }

    public void addFields(Map<String, ClientDMNType> fields) {
        this.fields.putAll(fields);
    }

    public BuiltInType getFeelType() {
        return feelType;
    }

    public void setFeelType(BuiltInType feelType) {
        this.feelType = feelType;
    }

    public ClientDMNType getBaseType() {
        return baseType;
    }

    public void setBaseType(ClientDMNType baseType) {
        this.baseType = baseType;
    }

}