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

    private FormModel formModel;

    private String shortMessage;

    private String fullMessage;

    private String modelSource;

    public SourceFormModelNotFoundException(String shortMessage, String fullMessage, String modelSource, FormModel formModel) {
        super(fullMessage);
        this.formModel = formModel;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
        this.modelSource = modelSource;
    }

    public FormModel getFormModel() {
        return formModel;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public String getModelSource() {
        return modelSource;
    }
}
