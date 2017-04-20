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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.commons.uuid.UUID;

@Dependent
public class GuidedDecisionTableAccordion {

    private final View view;

    private final ManagedInstance<GuidedDecisionTableAccordionItem> itemManagedInstance;

    private final List<GuidedDecisionTableAccordionItem> items = new ArrayList<>();

    private String parentId;

    @Inject
    public GuidedDecisionTableAccordion(final View view,
                                        final ManagedInstance<GuidedDecisionTableAccordionItem> itemManagedInstance) {
        this.view = view;
        this.itemManagedInstance = itemManagedInstance;
    }

    @PostConstruct
    public void setup() {
        view.init(this);

        setupParentId();
    }

    private void setupParentId() {
        parentId = UUID.uuid();

        view.setParentId(parentId);
    }

    public View getView() {
        return view;
    }

    public void addItem(final GuidedDecisionTableAccordionItem.Type type,
                        final Widget widget) {
        addItem(makeItem(type,
                         widget));
    }

    private GuidedDecisionTableAccordionItem makeItem(final GuidedDecisionTableAccordionItem.Type type,
                                                      final Widget widget) {
        final GuidedDecisionTableAccordionItem accordionItem = blankAccordionItem();

        accordionItem.init(getParentId(),
                           type,
                           widget
        );

        return accordionItem;
    }

    GuidedDecisionTableAccordionItem blankAccordionItem() {
        return itemManagedInstance.get();
    }

    List<GuidedDecisionTableAccordionItem> getItems() {
        return items;
    }

    private void addItem(final GuidedDecisionTableAccordionItem item) {
        getItems().add(item);
        getView().addItem(item);
    }

    public GuidedDecisionTableAccordionItem getItem(final GuidedDecisionTableAccordionItem.Type type) {
        return getItems()
                .stream()
                .filter(item -> item.getType() == type)
                .findFirst()
                .orElse(blankAccordionItem());
    }

    public void setColumnsNoteInfoHidden(final boolean isHidden) {
        view.setColumnsNoteInfoHidden(isHidden);
    }

    String getParentId() {
        return parentId;
    }

    public interface View extends UberElement<GuidedDecisionTableAccordion> {

        void setColumnsNoteInfoHidden(final boolean isHidden);

        void addItem(final GuidedDecisionTableAccordionItem item);

        void setParentId(final String parentId);
    }
}
