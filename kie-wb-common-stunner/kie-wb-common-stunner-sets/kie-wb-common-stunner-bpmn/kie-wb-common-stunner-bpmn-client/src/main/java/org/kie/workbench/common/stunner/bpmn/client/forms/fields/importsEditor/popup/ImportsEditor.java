/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.importsEditor.popup;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.ImportsValue;

public class ImportsEditor implements ImportsEditorView.Presenter {

    GetDataCallback callback = null;
    @Inject
    ImportsEditorView view;
    private ImportsValue importsValue;

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void save() {
        if (callback != null) {
            ImportsValue importsValue = new ImportsValue(view.getDefaultImports(), view.getWSDLImports());
            callback.getData(importsValue);
        }
        view.hideView();
    }

    @Override
    public void cancel() {
        view.hideView();
    }

    @Override
    public void setCallback(final GetDataCallback callback) {
        this.callback = callback;
    }

    @Override
    public void show() {
        view.showView();
    }

    public void setImportsValue(ImportsValue importsValue) {
        this.importsValue = importsValue;
        if (importsValue != null) {
            view.setDefaultImports(importsValue.getDefaultImports());
            view.setWSDLImports(importsValue.getWSDLImports());
        }
    }

    public interface GetDataCallback {

        void getData(ImportsValue importsValue);
    }
}
