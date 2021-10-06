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

package org.kie.kogito.dmn.form;

import java.util.Objects;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Form {

    private String filename;
    private String modelName;
    private ObjectNode schema;

    private String formUrl;
    private String modelUrl;
    private String swaggerUIUrl;

    public Form(final String filename,
                final String modelName,
                final ObjectNode schema,
                final String formUrl,
                final String modelUrl,
                final String swaggerUIUrl) {
        this.filename = filename;
        this.modelName = modelName;
        this.schema = schema;
        this.formUrl = formUrl;
        this.modelUrl = modelUrl;
        this.swaggerUIUrl = swaggerUIUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public String getSwaggerUIUrl() {
        return swaggerUIUrl;
    }

    public void setSwaggerUIUrl(String swaggerUIUrl) {
        this.swaggerUIUrl = swaggerUIUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var form = (Form) o;
        return filename.equals(form.filename)
                && modelName.equals(form.modelName)
                && schema.equals(form.schema)
                && formUrl.equals(form.formUrl)
                && modelUrl.equals(form.modelUrl)
                && swaggerUIUrl.equals(form.swaggerUIUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, modelName, schema, formUrl, modelUrl, swaggerUIUrl);
    }
}
