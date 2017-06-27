/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.preferences.client.central.form;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.properties.editor.client.PropertyEditorWidget;

@Templated
@Dependent
public class DefaultPreferenceFormView implements DefaultPreferenceForm.View,
                                                  IsElement {
    private DefaultPreferenceForm presenter;

    @Inject
    @DataField("properties-editor")
    PropertyEditorWidget propertiesEditorWidget;

    @Override
    public void init(final DefaultPreferenceForm presenter) {
        this.presenter = presenter;
        propertiesEditorWidget.setLastOpenAccordionGroupTitle("Properties");
        propertiesEditorWidget.handle(presenter.generatePropertyEditorEvent());
    }
}