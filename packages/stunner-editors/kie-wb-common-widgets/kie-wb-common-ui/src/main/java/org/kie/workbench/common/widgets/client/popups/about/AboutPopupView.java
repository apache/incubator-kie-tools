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


package org.kie.workbench.common.widgets.client.popups.about;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Image;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.Modal;

@Templated
public class AboutPopupView implements AboutPopup.View,
                                       IsElement {

    private AboutPopup presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("about")
    private Modal modal;

    @Inject
    @DataField("modal-content")
    private Div modalContent;

    @Inject
    @DataField("product-image")
    private Image productImage;

    @Inject
    @DataField("version")
    private Span version;

    @Inject
    @DataField("trademark-product-name")
    private Span trademarkProductName;

    @Inject
    @DataField("trademark2")
    private Span trademark2;

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
        this.productImage.setAlt(productName);
        this.trademarkProductName.setTextContent(productName);
    }

    @Override
    public void setProductVersion(final String productVersion) {
        this.version.setTextContent(productVersion);
    }

    @Override
    public void setProductLicense(final String productLicense) {
        this.trademark2.setTextContent(productLicense);
    }

    @Override
    public void setProductImageUrl(final String productImageUrl) {
        this.productImage.setSrc(productImageUrl);
    }

    @Override
    public void setBackgroundImageUrl(final String backgroundImageUrl) {
        this.modalContent.getStyle().setProperty("background-image",
                                                 "url(" + backgroundImageUrl + ")");
    }
}
