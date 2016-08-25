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

/**
 * The {@link #luceneValue} value is always set to a lowercase one-word (no -'s or _'s) value for the following reason:
 * <ol>
 * <li>Lucence is case-sensitive: let's make it easy by just always passing a lower case string for these values</li>
 * <li>Some lucene analyzers may break up tokens on the "-" or other non-word characters. That would be bad, so let's just use one-word values</li>
 * </ol>
 */
@Portable
public enum RelationshipType {

    CHILD_OF("child"), // "child-of", pure inheritance, whether a rule or java class
    IMPLEMENTS("impl"), // implementation of an interface
    CALLS("calls"); // calls a method, or calls a subprocess

    private final String luceneValue;

    /**
     *
     */
    private RelationshipType(String value) {
        this.luceneValue = value;
    }

    @Override
    public String toString() {
        return this.luceneValue;
    }

}