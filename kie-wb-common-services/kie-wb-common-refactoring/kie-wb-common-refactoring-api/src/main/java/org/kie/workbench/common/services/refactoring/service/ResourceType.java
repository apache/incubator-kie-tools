/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.service;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum ResourceType {

    /**
     * jBPM
     */
    BPMN2("bpmn2id"),
    // the problem is that processes can be referred to
    // 1. by id
    // OR
    // 2. by name
    // because of that, we add both when indexing
    BPMN2_NAME("bpmn2name"),

    /**
     * Drools
     */
    RULE("rule"),
    FUNCTION("function"),
    QUERY("query"),
    DRL_ENUM("drlenum"),
    DRL_ANNOTATION("drlanno"),

    /**
     * Form (as in, form-modeller)
     */
    FORM("form"),

    /**
     * Java file
     */
    JAVA("java"),

    /**
     * Properites configuration files
     */
    PROPERTIES_OR_CONFIG("properties"),

    /**
     * Case Modeler
     */
    BPMN_CM("bpmncmid"),

    BPMN_CM_NAME("bpmncmname");


    /**
     * All luceneValue values (used in indexing) should be lowercase
     */
    private final String luceneValue;

    /**
     *
     */
    private ResourceType(String value) {
        this.luceneValue = value;
    }

    @Override
    public String toString() {
        return this.luceneValue;
    }

}
