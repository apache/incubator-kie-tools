/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.assets.dropdown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.submarine.IsSubmarine;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

public abstract class KieAssetsDropdown {

    private final View view;

    private final KieAssetsDropdownItemsProvider dataProvider;

    private final IsSubmarine isSubmarine;

    private final List<KieAssetsDropdownItem> kieAssets = new ArrayList<>();

    private Command onValueChangeHandler = () -> {/* Nothing. */};

    public KieAssetsDropdown(final View view,
                             final IsSubmarine isSubmarine,
                             final KieAssetsDropdownItemsProvider dataProvider) {
        this.view = view;
        this.isSubmarine = isSubmarine;
        this.dataProvider = dataProvider;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void registerOnChangeHandler(final Command onChangeHandler) {
        this.onValueChangeHandler = onChangeHandler;
    }

    public void loadAssets() {
        clear();

        if (isSubmarine.get()) {
            initializeInput();
        } else {
            initializeDropdown();
        }
    }

    public void initialize() {
        if (!isSubmarine.get()) {
            view.refreshSelectPicker();
        }
    }

    public void clear() {
        getKieAssets().clear();
        view.clear();
    }

    private void initializeInput() {
        view.enableInputMode();
        view.initialize();
    }

    private void initializeDropdown() {
        view.enableDropdownMode();
        dataProvider.getItems(getAssetListConsumer());
    }

    Consumer<List<KieAssetsDropdownItem>> getAssetListConsumer() {
        return assetList -> {
            assetList.forEach(this::addValue);
            view.refreshSelectPicker();
            view.initialize();
        };
    }

    private void addValue(final KieAssetsDropdownItem kieAsset) {
        getKieAssets().add(kieAsset);
        view.addValue(kieAsset);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public Optional<KieAssetsDropdownItem> getValue() {
        if (isSubmarine.get()) {
            return Optional.of(new KieAssetsDropdownItem("", "", view.getValue(), new HashMap<>()));
        } else {
            return getKieAssets()
                    .stream()
                    .filter(kieAsset -> Objects.equals(view.getValue(), kieAsset.getValue()))
                    .findAny();
        }
    }

    List<KieAssetsDropdownItem> getKieAssets() {
        return kieAssets;
    }

    void onValueChanged() {
        onValueChangeHandler.execute();
    }

    public interface View extends UberElemental<KieAssetsDropdown>,
                                  IsElement {

        void clear();

        void addValue(final KieAssetsDropdownItem entry);

        void refreshSelectPicker();

        void initialize();

        String getValue();

        void enableInputMode();

        void enableDropdownMode();
    }
}
