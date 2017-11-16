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

package org.drools.workbench.screens.guided.dtable.client.editor.page.accordion;

import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class GuidedDecisionTableAccordionView implements GuidedDecisionTableAccordion.View,
                                                         IsElement {

    @DataField("items")
    private HTMLUListElement items;

    private GuidedDecisionTableAccordion presenter;

    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    public GuidedDecisionTableAccordionView(final HTMLUListElement items,
                                            final Elemental2DomUtil elemental2DomUtil) {
        this.items = items;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    @Override
    public void init(final GuidedDecisionTableAccordion presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addItem(final GuidedDecisionTableAccordionItem item) {
        items.appendChild(getViewElement(item));
    }

    @Override
    public void clear() {
        elemental2DomUtil.removeAllElementChildren(items);
    }

    @Override
    public void setParentId(final String parentId) {
        items.id = parentId;
    }

    private HTMLElement getViewElement(final GuidedDecisionTableAccordionItem accordionItem) {

        final GuidedDecisionTableAccordionItem.View view = accordionItem.getView();

        return elemental2DomUtil.asHTMLElement(view.getElement());
    }
}
