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

import java.util.HashMap;
import java.util.Optional;

import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.widgets.client.submarine.IsSubmarine;

public abstract class SubmarineKieAssetsDropdown extends AbstractKieAssetsDropdown {

    protected final IsSubmarine isSubmarine;

    public SubmarineKieAssetsDropdown(final SubmarineKieAssetsDropdown.View view,
                                      final IsSubmarine isSubmarine,
                                      final KieAssetsDropdownItemsProvider dataProvider) {
        super(view, dataProvider);
        this.isSubmarine = isSubmarine;
    }

    @Override
    public void loadAssets() {
        if (isSubmarine.get()) {
            clear();
            initializeInput();
        } else {
            super.loadAssets();
        }
    }

    @Override
    public void initialize() {
        if (!isSubmarine.get()) {
            super.initialize();
        }
    }

    @Override
    public void initializeDropdown() {
        ((SubmarineKieAssetsDropdown.View) view).enableDropdownMode();
        super.initializeDropdown();
    }

    protected void initializeInput() {
        ((SubmarineKieAssetsDropdown.View) view).enableInputMode();
        view.initialize();
    }

    @Override
    public Optional<KieAssetsDropdownItem> getValue() {
        if (isSubmarine.get()) {
            return Optional.of(new KieAssetsDropdownItem("", "", view.getValue(), new HashMap<>()));
        } else {
            return super.getValue();
        }
    }

    public interface View extends AbstractKieAssetsDropdown.View,
                                  IsElement {

        void enableInputMode();

        void enableDropdownMode();
    }
}
