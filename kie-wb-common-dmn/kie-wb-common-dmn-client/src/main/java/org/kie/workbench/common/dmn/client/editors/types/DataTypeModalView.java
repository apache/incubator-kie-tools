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

package org.kie.workbench.common.dmn.client.editors.types;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessages;

@Templated
@ApplicationScoped
public class DataTypeModalView implements DataTypeModal.View {

    @DataField("header")
    private final HTMLDivElement header;

    @DataField("body")
    private final HTMLDivElement body;

    @DataField("footer")
    private final HTMLDivElement footer;

    private DataTypeModal presenter;

    @Inject
    public DataTypeModalView(final HTMLDivElement header,
                             final HTMLDivElement body,
                             final HTMLDivElement footer) {
        this.header = header;
        this.body = body;
        this.footer = footer;
    }

    @Override
    public void init(final DataTypeModal presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setup(final DataTypeFlashMessages flashMessages,
                      final DataTypeList treeGrid) {
        body.appendChild(flashMessages.getElement());
        body.appendChild(treeGrid.getElement());
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }
}
