/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.widgets.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.kie.workbench.common.widgets.client.resources.CommonsResources;
import org.kie.workbench.common.widgets.client.resources.RoundedCornersResource;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

@EntryPoint
@Bundle("resources/i18n/KieWorkbenchWidgetsConstants.properties")
public class KieWorkbenchWidgetsCommonEntryPoint {

    @PostConstruct
    public void startApp() {
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
        CommonsResources.INSTANCE.css().ensureInjected();
        PatternFlyBootstrapper.ensureBootstrapSelectIsAvailable();
    }

}
