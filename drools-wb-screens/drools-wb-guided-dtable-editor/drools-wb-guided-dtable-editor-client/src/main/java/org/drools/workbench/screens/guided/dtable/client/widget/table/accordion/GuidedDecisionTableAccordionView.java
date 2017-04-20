/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.accordion;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class GuidedDecisionTableAccordionView implements GuidedDecisionTableAccordion.View,
                                                         IsElement {

    @DataField("items")
    private UnorderedList items;

    @DataField("columnsNoteInfo")
    private Div columnsNoteInfo;

    private ManagedInstance<GuidedDecisionTableAccordionItem> itemManagedInstance;

    private GuidedDecisionTableAccordion presenter;

    @Inject
    public GuidedDecisionTableAccordionView(final UnorderedList items,
                                            final Div columnsNoteInfo,
                                            final ManagedInstance<GuidedDecisionTableAccordionItem> itemManagedInstance) {
        this.items = items;
        this.columnsNoteInfo = columnsNoteInfo;
        this.itemManagedInstance = itemManagedInstance;
    }

    @Override
    public void init(final GuidedDecisionTableAccordion presenter) {
        this.presenter = presenter;
        this.columnsNoteInfo.setHidden(true);
    }

    @Override
    public void setColumnsNoteInfoHidden(final boolean isHidden) {
        columnsNoteInfo.setHidden(isHidden);
    }

    @Override
    public void addItem(final GuidedDecisionTableAccordionItem item) {
        items.appendChild(getViewElement(item));
    }

    @Override
    public void setParentId(final String parentId) {
        items.setId(parentId);
    }

    private HTMLElement getViewElement(final GuidedDecisionTableAccordionItem accordionItem) {
        final GuidedDecisionTableAccordionItem.View view = accordionItem.getView();

        return view.getElement();
    }
}
