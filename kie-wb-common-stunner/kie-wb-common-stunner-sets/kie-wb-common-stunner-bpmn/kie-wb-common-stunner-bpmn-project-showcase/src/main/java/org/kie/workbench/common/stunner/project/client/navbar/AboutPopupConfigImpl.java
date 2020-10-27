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

package org.kie.workbench.common.stunner.project.client.navbar;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.project.client.resources.i18n.AppConstants;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopupConfig;

@ApplicationScoped
public class AboutPopupConfigImpl implements AboutPopupConfig {

    @Override
    public String productName() {
        return AppConstants.INSTANCE.LogoTitle();
    }

    @Override
    public String productVersion() {
        return "${version.org.kie.workbench.app}";
    }

    @Override
    public String productLicense() {
        return AppConstants.INSTANCE.License();
    }

    @Override
    public String productImageUrl() {
        return "images/kie-ide.png";
    }

    @Override
    public String backgroundImageUrl() {
        return "images/home_bg.jpg";
    }
}
