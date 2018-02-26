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

package org.kie.workbench.common.screens.library.client.screens.assets.add;

import java.util.List;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.select.SelectComponent;
import org.uberfire.ext.widgets.common.client.select.SelectOption;

@Templated
public class AddAssetView implements AddAssetScreen.View,
                                     IsElement {

    private AddAssetScreen presenter;

    @Inject
    private Elemental2DomUtil domUtil;

    @Inject
    @DataField("title")
    private HTMLDivElement title;

    @Inject
    @DataField("filter-text")
    private HTMLInputElement filterText;

    @Inject
    @DataField("filter-type")
    private HTMLDivElement filterType;

    @Inject
    @DataField("assets-list")
    private HTMLDivElement assetsList;

    @Inject
    @DataField("cancel")
    private HTMLButtonElement cancel;

    @Inject
    private SelectComponent selectComponent;

    @Override
    public void init(AddAssetScreen presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addNewAssetWidget(HTMLElement view) {
        this.assetsList.appendChild(view);
    }

    @Override
    public void clear() {
        this.domUtil.removeAllElementChildren(assetsList);
    }

    @Override
    public void setTitle(String title) {
        this.title.textContent = title;
    }

    @Override
    public void setCategories(List<SelectOption> categories) {
        this.selectComponent.setup(categories,
                                   selectOption -> {
                                       presenter.setFilterType(selectOption.getSelector());
                                   });
        this.filterType.appendChild(selectComponent.getView().getElement());
    }

    @EventHandler("cancel")
    public void cancel(final ClickEvent event) {
        presenter.cancel();
    }

    @EventHandler("filter-text")
    public void onFilterText(KeyUpEvent keyUpEvent) {
        this.presenter.setFilter(this.filterText.value);
    }
}
