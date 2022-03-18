/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.AbstractFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.IsFormView;

@Dependent
public class EmbeddedFormDisplayer extends AbstractFormDisplayer {

    public interface EmbeddedFormDisplayerView extends IsWidget {

        public void setPresenter(EmbeddedFormDisplayer presenter);

        public void show(String title,
                         IsFormView formView);

        public void clear();
    }

    private EmbeddedFormDisplayerView view;

    @Inject
    public EmbeddedFormDisplayer(EmbeddedFormDisplayerView view) {
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
        view.clear();
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
