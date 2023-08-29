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

package org.kie.workbench.common.dmn.client.editors.types.imported.treelist;

import java.util.Objects;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;

@Dependent
@Templated
public class TreeListItemView implements TreeListItem.View {

    private TreeListItem presenter;

    @DataField("item-header")
    private final HTMLDivElement itemHeader;

    @DataField("items-container")
    private final HTMLDivElement itemsContainer;

    @DataField("item-details")
    private final HTMLDivElement itemDetails;

    @DataField("item-root")
    private final HTMLDivElement itemRoot;

    @DataField("expand-container")
    private final HTMLDivElement expandContainer;

    @DataField("expand")
    private final HTMLElement expand;

    @DataField("data-object-checkbox")
    private final HTMLInputElement checkbox;

    @Inject
    public TreeListItemView(final HTMLDivElement itemHeader,
                            final HTMLDivElement itemsContainer,
                            final HTMLDivElement itemDetails,
                            final HTMLDivElement expandContainer,
                            @Named("span") final HTMLElement expand,
                            final HTMLInputElement checkbox,
                            final HTMLDivElement itemRoot) {
        this.itemHeader = itemHeader;
        this.itemsContainer = itemsContainer;
        this.itemDetails = itemDetails;
        this.expandContainer = expandContainer;
        this.expand = expand;
        this.checkbox = checkbox;
        this.itemRoot = itemRoot;
    }

    public TreeListItem getPresenter() {
        return presenter;
    }

    @Override
    public void init(final TreeListItem presenter) {
        this.presenter = presenter;
    }

    @EventHandler("data-object-checkbox")
    public void onCheckboxChanged(final ChangeEvent valueChanged) {
        getPresenter().setIsSelected(checkbox.checked);
    }

    @EventHandler("item-header")
    public void onClick(final ClickEvent e) {

        final Object target = getTarget(e);
        if (Objects.equals(target, checkbox)) {
            return;
        }

        if (HiddenHelper.isHidden(itemsContainer)) {
            showElement(itemsContainer);
        } else {
            hideElement(itemsContainer);
        }
    }

    void showElement(final HTMLElement element) {
        HiddenHelper.show(element);
    }

    void hideElement(final HTMLElement element) {
        HiddenHelper.hide(element);
    }

    Object getTarget(final ClickEvent event) {
        return Element.as(event.getNativeEvent().getEventTarget());
    }

    public void populate(final TreeListItem item) {

        checkbox.checked = item.getIsSelected();
        itemDetails.textContent = item.getDescription();
        addSubItems(item);
        setExpandVisibility(item);
    }

    void setExpandVisibility(final TreeListItem item) {
        if (item.getSubItems().isEmpty()) {
            hideElement(expand);
        } else {
            showElement(expand);
        }
    }

    void addSubItems(final TreeListItem item) {
        for (final TreeListSubItem sub : item.getSubItems()) {
            itemsContainer.appendChild(sub.getElement());
        }
    }

    @Override
    public HTMLElement getElement() {
        return itemRoot;
    }
}
