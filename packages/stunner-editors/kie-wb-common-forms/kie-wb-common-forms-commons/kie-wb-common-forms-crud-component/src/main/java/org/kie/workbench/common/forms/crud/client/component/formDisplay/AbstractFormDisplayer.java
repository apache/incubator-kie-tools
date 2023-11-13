/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.crud.client.component.formDisplay;

public abstract class AbstractFormDisplayer implements FormDisplayer {

    protected String title;

    protected IsFormView formView;

    protected FormDisplayerCallback callback;

    @Override
    public void display(String title,
                        IsFormView formView,
                        FormDisplayerCallback callback) {
        this.title = title;
        this.formView = formView;
        this.callback = callback;

        display();
    }

    @Override
    public boolean isEmbeddable() {
        return true;
    }

    @Override
    public IsFormView getFormView() {
        return formView;
    }

    protected abstract void display();

    protected abstract void hide();

    public void submitForm() {
        if (formView.isValid()) {
            if (callback != null) {
                callback.onAccept();
            }
            formView = null;
            callback = null;
            hide();
        }
    }

    @Override
    public void cancel() {
        if (callback != null) {
            callback.onCancel();
        }
        hide();
    }
}
