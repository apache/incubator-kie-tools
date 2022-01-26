/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.widgets.client.popups.about;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLImageElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.uberfire.client.views.pfly.widgets.Modal;

@Templated
@Dependent
public class AboutPopupView implements AboutPopup.View,
                                       IsElement {

    private AboutPopup presenter;

    @Inject
    @DataField("about")
    private Modal modal;

    @Inject
    @DataField("modal-content")
    private HTMLDivElement modalContent;

    @Inject
    @DataField("product-image")
    private HTMLImageElement productImage;

    @Inject
    @DataField("version")
    @Named("span")
    private HTMLElement version;

    @Inject
    @DataField("trademark-product-name")
    @Named("span")
    private HTMLElement trademarkProductName;

    @Inject
    @DataField("trademark2")
    @Named("span")
    private HTMLElement trademark2;

    @Override
    public void init(final AboutPopup presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void setProductName(final String productName) {
        this.productImage.alt = (productName);
        this.trademarkProductName.textContent = (productName);
    }

    @Override
    public void setProductVersion(final String productVersion) {
        this.version.textContent = (productVersion);
    }

    @Override
    public void setProductLicense(final String productLicense) {
        this.trademark2.textContent = (productLicense);
    }

    @Override
    public void setProductImageUrl(final String productImageUrl) {
        this.productImage.src = (productImageUrl);
    }

    @Override
    public void setBackgroundImageUrl(final String backgroundImageUrl) {
        this.modalContent.style.setProperty("background-image",
                                                 "url(" + backgroundImageUrl + ")");
    }
}
