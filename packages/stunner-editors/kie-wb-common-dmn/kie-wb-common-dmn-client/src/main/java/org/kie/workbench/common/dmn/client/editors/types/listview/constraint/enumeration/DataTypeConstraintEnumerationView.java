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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.DATA_POSITION;

@Templated
@Dependent
public class DataTypeConstraintEnumerationView implements DataTypeConstraintEnumeration.View {

    @DataField("items")
    private final HTMLDivElement items;

    @DataField("add-icon")
    private final HTMLAnchorElement addIcon;

    @DataField("add-button-container")
    private final HTMLDivElement addButtonContainer;

    private final DragAndDropHelper dragAndDrop;

    private DataTypeConstraintEnumeration presenter;

    @Inject
    public DataTypeConstraintEnumerationView(final HTMLDivElement items,
                                             final HTMLAnchorElement addIcon,
                                             final HTMLDivElement addButtonContainer) {
        this.items = items;
        this.addIcon = addIcon;
        this.addButtonContainer = addButtonContainer;
        this.dragAndDrop = new DragAndDropHelper(items, addButtonContainer);
    }

    @Override
    public void init(final DataTypeConstraintEnumeration presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-icon")
    public void onAddIconClick(final ClickEvent e) {
        presenter.addEnumerationItem();
    }

    @Override
    public void clear() {
        RemoveHelper.removeChildren(items);
    }

    @Override
    public void addItem(final Element enumerationItem) {

        enumerationItem.setAttribute(DATA_POSITION, items.childNodes.length);
        items.appendChild(enumerationItem);
        getDragAndDropHelper().refreshItemsPosition();
        presenter.scrollToBottom();
    }

    DragAndDropHelper getDragAndDropHelper() {
        return dragAndDrop;
    }
}
