/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.common.client;

import javax.annotation.PostConstruct;

import com.ait.lienzo.client.core.config.LienzoCore;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;

@EntryPoint
public class DMNKogitoCommonEntryPoint {

    @PostConstruct
    public void init() {
        PatternFlyBootstrapper.ensureMomentIsAvailable();
        PatternFlyBootstrapper.ensureMomentTimeZoneIsAvailable();
        initializeLienzoCore();
    }

    void initializeLienzoCore() {
        // This method must be removed by https://issues.redhat.com/browse/KOGITO-5006.
        LienzoCore.get().setHidpiEnabled(true);
    }
}
