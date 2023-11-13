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

import java.util.Optional;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.Command;

public interface KieAssetsDropdown {

    void init();

    void registerOnChangeHandler(final Command onChangeHandler);

    void loadAssets();

    void initialize();

    void clear();

    HTMLElement getElement();

    Optional<KieAssetsDropdownItem> getValue();

    void onValueChanged();

    void initializeDropdown();

    void addValue(final KieAssetsDropdownItem kieAsset);

    interface View extends UberElemental<KieAssetsDropdown>,
                           IsElement {

        void clear();

        void addValue(final KieAssetsDropdownItem entry);

        void refreshSelectPicker();

        void initialize();

        String getValue();
    }
}
