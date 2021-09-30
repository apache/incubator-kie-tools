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

package org.uberfire.ext.plugin.client;

import javax.annotation.PostConstruct;

import org.jboss.errai.bus.client.util.BusToolsCli;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.shared.api.annotations.Bundle;
import org.uberfire.ext.plugin.client.resources.WebAppResource;

@EntryPoint
@Bundle("resources/i18n/Constants.properties")
public class RuntimePluginsEntryPoint {
    
    @PostConstruct
    public void init() {
        if (!BusToolsCli.isRemoteCommunicationEnabled()) {
            return;
        }
        WebAppResource.INSTANCE.CSS().ensureInjected();
    }

}