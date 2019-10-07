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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;
import org.drools.workbench.models.datamodel.rule.Attribute;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.getCurrentIndexFromListWithoutDefaultSelect;

@Dependent
@Templated
public class AttributeColumnPageView implements IsElement,
                                                AttributeColumnPage.View {

    private AttributeColumnPage page;

    @DataField("attributeList")
    private ListBox attributeList;

    @DataField("attributeDescription")
    private Div attributeDescription;

    @Inject
    public AttributeColumnPageView(final ListBox attributeList,
                                   final Div attributeDescription) {
        this.attributeList = attributeList;
        this.attributeDescription = attributeDescription;
    }

    @Override
    public void init(final AttributeColumnPage page) {
        this.page = page;
        hideAttributeDescription();
    }

    @EventHandler("attributeList")
    public void onSelectAttribute(ChangeEvent event) {
        page.selectItem(Stream.of(Attribute.values())
                                .filter(attribute -> Objects.equals(attribute.getAttributeName(),
                                                                    attributeList.getSelectedItemText()))
                                .findFirst()
                                .map(attribute -> attribute.getAttributeName())
                                .get());

        attributeDescription.setAttribute("data-enabled",
                                          attributeList.getSelectedValue());
        if (isAttributeDescriptionHidden()) {
            showAttributeDescription();
        }
    }

    @Override
    public void setupAttributeList(final List<String> attributes) {
        attributeList.clear();

        attributes.forEach(attributeList::addItem);

        attributeList.setVisibleItemCount(attributeList.getItemCount());
        attributeList.setSelectedIndex(attributeIndex());
    }

    @Override
    public boolean isAttributeDescriptionHidden() {
        return attributeDescription.getHidden();
    }

    @Override
    public void hideAttributeDescription() {
        this.attributeDescription.setHidden(true);
    }

    @Override
    public void showAttributeDescription() {
        this.attributeDescription.setHidden(false);
    }

    private int attributeIndex() {
        return getCurrentIndexFromListWithoutDefaultSelect(page.selectedAttribute(),
                                                           attributeList);
    }
}
