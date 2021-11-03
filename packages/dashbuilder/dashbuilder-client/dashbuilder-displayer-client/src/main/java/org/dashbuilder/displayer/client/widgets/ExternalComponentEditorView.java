/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.displayer.client.widgets;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ExternalComponentEditorView extends Composite implements ExternalComponentEditor.View {

    @Inject
    @DataField
    HTMLDivElement componentEditorRootContainer;

    @Inject
    @DataField
    HTMLDivElement componentEditorPropertiesColumn;

    @Inject
    @DataField
    HTMLDivElement componentEditorPreviewColumn;

    @Inject
    Elemental2DomUtil domUtil;

    @Override
    public void init(ExternalComponentEditor presenter) {
        domUtil.appendWidgetToElement(componentEditorPropertiesColumn, presenter.getPropertiesEditor().asWidget());
        domUtil.appendWidgetToElement(componentEditorPreviewColumn, presenter.getExternalComponentPresenter().getView().asWidget());
    }

}