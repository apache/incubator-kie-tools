/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.factmodel.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class AnnotationMetaModel {

    private String name;
    private Map<String, String> values = new HashMap<>();

    public AnnotationMetaModel() {
    }

    public AnnotationMetaModel( String name,
                                Map<String, String> values ) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getValues() {
        return values;
    }

}
