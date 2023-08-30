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


package org.kie.workbench.common.widgets.client.assets.dropdown;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import elemental2.dom.HTMLElement;
import org.uberfire.mvp.Command;

public abstract class AbstractKieAssetsDropdown implements KieAssetsDropdown {

    protected final View view;

    protected final KieAssetsDropdownItemsProvider dataProvider;

    protected final List<KieAssetsDropdownItem> kieAssets = new ArrayList<>();

    protected Command onValueChangeHandler = () -> {/* Nothing. */};

    public AbstractKieAssetsDropdown(final View view,
                                     final KieAssetsDropdownItemsProvider dataProvider) {
        this.view = view;
        this.dataProvider = dataProvider;
    }

    @Override
    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public void registerOnChangeHandler(final Command onChangeHandler) {
        this.onValueChangeHandler = onChangeHandler;
    }

    @Override
    public void loadAssets() {
        clear();
        initializeDropdown();
    }

    @Override
    public void initialize() {
        view.refreshSelectPicker();
    }

    @Override
    public void clear() {
        kieAssets.clear();
        view.clear();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public Optional<KieAssetsDropdownItem> getValue() {
        String currentValue = view.getValue();
        return kieAssets
                .stream()
                .filter(kieAsset -> Objects.equals(currentValue, kieAsset.getValue()))
                .findAny();
    }

    @Override
    public void onValueChanged() {
        onValueChangeHandler.execute();
    }

    @Override
    public void initializeDropdown() {
        dataProvider.getItems(getAssetListConsumer());
    }

    @Override
    public void addValue(final KieAssetsDropdownItem kieAsset) {
        kieAssets.add(kieAsset);
        view.addValue(kieAsset);
    }

    protected Consumer<List<KieAssetsDropdownItem>> getAssetListConsumer() {
        return this::assetListConsumerMethod;
    }

    protected void assetListConsumerMethod(final List<KieAssetsDropdownItem> assetList) {
        assetList.forEach(this::addValue);
        view.refreshSelectPicker();
        view.initialize();
    }

}
