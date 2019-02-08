/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

@Templated
@Dependent
public class DataTypeConstraintEnumerationView implements DataTypeConstraintEnumeration.View {

    @DataField("items")
    private final HTMLDivElement items;

    @DataField("add-icon")
    private final HTMLAnchorElement addIcon;

    private DataTypeConstraintEnumeration presenter;

    @Inject
    public DataTypeConstraintEnumerationView(final HTMLDivElement items,
                                             final HTMLAnchorElement addIcon) {
        this.items = items;
        this.addIcon = addIcon;
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
        items.innerHTML = "";
    }

    @Override
    public void addItem(final Element enumerationItem) {
        items.appendChild(enumerationItem);
    }
}
