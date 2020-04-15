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

import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.kogito.client.dmn.feel.BuiltInType;

public class ClientDMNType {

    private String namespace;
    private String name;
    private String id;
    private boolean collection;
    private boolean composite;
    private Map<String, ClientDMNType> fields;
    private BuiltInType feelType;

    public ClientDMNType(String namespace, String name, String id, boolean isCollection, BuiltInType feelType) {
        this.namespace = namespace;
        this.name = name;
        this.id = id;
        this.collection = isCollection;
        this.feelType = feelType;
    }

    public ClientDMNType(String namespace, String name, String id, boolean isCollection, boolean isComposite, Map<String, ClientDMNType> fields, BuiltInType feelType) {
        this(namespace, name, id, isCollection, feelType);
        this.fields = fields;
        this.composite = isComposite;
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

    public boolean isComposite() {
        return composite;
    }

    public Map<String, ClientDMNType> getFields() {
        return fields;
    }

    public BuiltInType getFeelType() {
        return feelType;
    }

    public void setIsComposite(final boolean isComposite) {
        this.composite = isComposite;
    }
}