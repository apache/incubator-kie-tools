/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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


package org.kie.kogito.model;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Form {

    private String uri;
    private String modelName;
    private ObjectNode schema;

    public Form(final String uri, final String modelName, final ObjectNode schema) {
        this.uri = uri;
        this.modelName = modelName;
        this.schema = schema;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public ObjectNode getSchema() {
        return schema;
    }

    public void setSchema(ObjectNode schema) {
        this.schema = schema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Form form = (Form) o;
        return uri.equals(form.uri) && modelName.equals(form.modelName) && schema.equals(form.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, modelName, schema);
    }
}
