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

package org.kie.workbench.common.screens.library.client.screens.assets;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class EmptyAssetsView implements IsElement,
                                        EmptyAssetsScreen.View {

    @Inject
    @DataField("add-asset")
    private HTMLButtonElement addAsset;

    @Inject
    @DataField("import-asset")
    private HTMLButtonElement importAsset;

    private EmptyAssetsScreen presenter;

    @Override
    public void init(EmptyAssetsScreen presenter) {
        this.presenter = presenter;
    }

    @EventHandler("import-asset")
    public void clickImportAsset(final ClickEvent clickEvent) {
        this.presenter.importAsset();
    }

    @EventHandler("add-asset")
    public void clickAddAsset(final ClickEvent clickEvent) {
        this.presenter.addAsset();
    }

    @Override
    public void enableImportButton(boolean enable) {
        this.importAsset.disabled = !enable;
    }

    @Override
    public void enableAddAssetButton(boolean enable) {
        this.addAsset.disabled = !enable;
    }
}
