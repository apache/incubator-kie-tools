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


package org.kie.workbench.common.forms.crud.client.component.formDisplay.modal;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.AbstractFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;

@Dependent
public class ModalFormDisplayer extends AbstractFormDisplayer {

    public interface ModalFormDisplayerView extends IsWidget {

        public void setPresenter(ModalFormDisplayer presenter);

        public void show(String title,
                         IsFormView formView);

        public void hide();
    }

    private ModalFormDisplayerView view;

    @Inject
    public ModalFormDisplayer(ModalFormDisplayerView view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void display() {
        view.show(title,
                  formView);
    }

    @Override
    public void hide() {
        view.hide();
    }

    @Override
    public boolean isEmbeddable() {
        return false;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
