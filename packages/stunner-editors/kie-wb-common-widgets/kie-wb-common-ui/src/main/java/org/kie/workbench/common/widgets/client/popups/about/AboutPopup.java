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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;

public class AboutPopup {

    public interface View extends UberElement<AboutPopup> {

        void show();

        void setProductName(String productName);

        void setProductVersion(String productVersion);

        void setProductLicense(String productLicense);

        void setProductImageUrl(String productImageUrl);

        void setBackgroundImageUrl(String backgroundImageUrl);
    }

    private View view;

    private ManagedInstance<AboutPopupConfig> aboutPopupConfigs;

    @Inject
    public AboutPopup(final View view,
                      final ManagedInstance<AboutPopupConfig> aboutPopupConfigs) {
        this.view = view;
        this.aboutPopupConfigs = aboutPopupConfigs;
    }

    @PostConstruct
    public void setup() {
        view.init(this);

        if (aboutPopupConfigs.isUnsatisfied()) {
            throw new RuntimeException("One AboutPopupConfig implementation must be provided");
        }

        if (aboutPopupConfigs.isAmbiguous()) {
            throw new RuntimeException("Only one AboutPopupConfig implementation must be provided");
        }

        final AboutPopupConfig aboutPopupConfig = aboutPopupConfigs.get();
        view.setProductName(aboutPopupConfig.productName());
        view.setProductVersion(aboutPopupConfig.productVersion());
        view.setProductLicense(aboutPopupConfig.productLicense());
        view.setProductImageUrl(aboutPopupConfig.productImageUrl());

        final String backgroundImageUrl = aboutPopupConfig.backgroundImageUrl();
        if (backgroundImageUrl != null && !backgroundImageUrl.isEmpty()) {
            view.setBackgroundImageUrl(backgroundImageUrl);
        }
    }

    public void show() {
        view.show();
    }
}
