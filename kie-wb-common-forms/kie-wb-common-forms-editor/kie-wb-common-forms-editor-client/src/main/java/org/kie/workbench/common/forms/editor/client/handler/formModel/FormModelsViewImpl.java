/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.handler.formModel;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.editor.client.handler.formModel.container.FormModelCreationContainer;

@Templated
public class FormModelsViewImpl extends Composite implements FormModelsView {

    @Inject
    private Document document;

    @Inject
    @DataField
    private Div content;

    protected List<FormModelCreationContainer> creationViews;

    @Override
    public void setCreationViews(List<FormModelCreationContainer> creationViews) {
        this.creationViews = creationViews;

        render();
    }

    protected void render() {
        DOMUtil.removeAllChildren(content);

        creationViews.forEach(container -> {

            HTMLElement row = document.createElement("div");
            DOMUtil.addCSSClass(row,
                                Styles.ROW);

            HTMLElement col = document.createElement("div");
            DOMUtil.addCSSClass(col,
                                ColumnSize.MD_12.getCssName());

            col.appendChild(container.getElement());

            row.appendChild(col);

            content.appendChild(row);
        });

        creationViews.get(0).selectManager();
    }

    @Override
    public void reset() {
        render();
    }
}
