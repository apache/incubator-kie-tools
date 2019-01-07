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

package org.kie.workbench.common.forms.editor.service.backend;

import org.kie.workbench.common.forms.model.FormModel;

/**
 * Exception thrown if the surce for a given {@link FormModel} cannot be found.
 */
public class SourceFormModelNotFoundException extends Exception {

    private String shortKey;
    private String[] shortKeyParams;
    private String longKey;
    private String[] longKeyParams;
    private String modelSourceKey;

    private FormModel formModel;

    public SourceFormModelNotFoundException(String shortKey, String[] shortKeyParams, String longKey, String[] longKeyParams, String modelSourceKey, FormModel formModel) {
        this.shortKey = shortKey;
        this.shortKeyParams = shortKeyParams;
        this.longKey = longKey;
        this.longKeyParams = longKeyParams;
        this.modelSourceKey = modelSourceKey;
        this.formModel = formModel;
    }

    public String getShortKey() {
        return shortKey;
    }

    public String[] getShortKeyParams() {
        return shortKeyParams;
    }

    public String getLongKey() {
        return longKey;
    }

    public String[] getLongKeyParams() {
        return longKeyParams;
    }

    public String getModelSourceKey() {
        return modelSourceKey;
    }

    public FormModel getFormModel() {
        return formModel;
    }
}
