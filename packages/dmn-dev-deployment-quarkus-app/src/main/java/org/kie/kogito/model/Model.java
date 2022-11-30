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

public class Model {

    private String filePath;
    private Form form;

    private String formUrl;
    private String modelUrl;
    private String swaggerUIUrl;

    public Model(final String filePath,
                 final Form form,
                 final String formUrl,
                 final String modelUrl,
                 final String swaggerUIUrl) {
        this.filePath = filePath;
        this.form = form;
        this.formUrl = formUrl;
        this.modelUrl = modelUrl;
        this.swaggerUIUrl = swaggerUIUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
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
        var model = (Model) o;
        return filePath.equals(model.filePath)
                && form.equals(model.form)
                && formUrl.equals(model.formUrl)
                && modelUrl.equals(model.modelUrl)
                && swaggerUIUrl.equals(model.swaggerUIUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath, form, formUrl, modelUrl, swaggerUIUrl);
    }
}

