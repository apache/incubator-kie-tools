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

package org.drools.workbench.screens.testscenario.client.page.audit;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class AuditTable implements IsElement {

    @DataField("title")
    private AuditTableItem title;

    @DataField("items")
    private HTMLUListElement itemsContainer;

    private ManagedInstance<AuditTableItem> items;

    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    public AuditTable(final HTMLUListElement itemsContainer,
                      final AuditTableItem title,
                      final ManagedInstance<AuditTableItem> items,
                      final Elemental2DomUtil elemental2DomUtil) {
        this.itemsContainer = itemsContainer;
        this.items = items;
        this.title = title;
        this.elemental2DomUtil = elemental2DomUtil;

        this.title.getElement().id = "header";
    }

    public void setTitle(final String tableTitle) {
        this.title.setText(tableTitle);
    }

    public void showItems(final Collection<String> textItems) {
        elemental2DomUtil.removeAllElementChildren(itemsContainer);

        textItems.stream()
                .forEach(logMessage -> {
                    final AuditTableItem newItem = items.get();
                    newItem.setText(logMessage);
                    itemsContainer.appendChild(newItem.getElement());
                });
    }
}
