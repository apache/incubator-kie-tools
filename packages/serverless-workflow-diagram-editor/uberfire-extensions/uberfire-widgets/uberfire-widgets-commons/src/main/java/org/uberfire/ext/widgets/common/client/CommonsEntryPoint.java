/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.client.views.pfly.sys.PatternFlyBootstrapper;
import org.uberfire.ext.widgets.common.client.resources.CommonResources;

/**
 * Bootstrap widgets-commons
 */
@EntryPoint
public class CommonsEntryPoint {

    @PostConstruct
    public void startApp() {
        //Ensure CSS has been loaded
        CommonResources.INSTANCE.CSS().ensureInjected();
        PatternFlyBootstrapper.ensurejQueryIsAvailable();
        PatternFlyBootstrapper.ensureBootstrapSelectIsAvailable();
    }
}
