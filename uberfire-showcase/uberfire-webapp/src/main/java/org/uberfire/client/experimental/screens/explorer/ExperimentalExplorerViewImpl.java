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

package org.uberfire.client.experimental.screens.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Document;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.experimental.screens.explorer.asset.AssetDisplayer;
import org.uberfire.commons.data.Pair;

@Templated
public class ExperimentalExplorerViewImpl implements ExperimentalExplorerView,
                                                     IsElement {

    private Presenter presenter;

    @Inject
    @DataField
    private HTMLDivElement empty;

    @Inject
    @DataField
    private HTMLButtonElement addButton;

    @Inject
    @DataField
    @Named("ul")
    private HTMLUListElement assets;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    private Document document;

    private List<Pair<Element, AssetDisplayer>> assetsList = new ArrayList<>();

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(AssetDisplayer asset) {
        empty.hidden = true;
        Element listElement = document.createElement("li");
        listElement.className = "list-group-item";
        listElement.appendChild(asset.getElement());
        assets.appendChild(listElement);
        assetsList.add(new Pair<>(listElement, asset));
    }

    @Override
    public void delete(AssetDisplayer asset) {
        Optional<Pair<Element, AssetDisplayer>> optional = assetsList.stream().filter(pair -> pair.getK2().equals(asset)).findAny();

        if (optional.isPresent()) {
            Pair<Element, AssetDisplayer> pair = optional.get();

            assets.removeChild(pair.getK1());
            assetsList.remove(pair);
        }

        if (assetsList.isEmpty()) {
            empty.hidden = false;
        }
    }

    @Override
    public Optional<AssetDisplayer> findDisplayer(Path path) {
        return assetsList.stream()
                .map(Pair::getK2)
                .filter(displayer -> displayer.getPath().equals(path))
                .findAny();
    }

    @Override
    public void clean() {
        elemental2DomUtil.removeAllElementChildren(assets);
        assetsList.clear();
    }

    @EventHandler("addButton")
    public void onClick(ClickEvent event) {
        presenter.createNew();
    }
}
