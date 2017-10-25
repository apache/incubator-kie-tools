/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.wildfly.model;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.runtime.providers.base.BaseProvider;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.config.impl.WildflyProviderConfigImpl;

public class WildflyProviderImpl
        extends BaseProvider<WildflyProviderConfig>
        implements WildflyProvider,
                   CloneableConfig<WildflyProvider> {

    public WildflyProviderImpl() {
        //No-args constructor for enabling marshalling to work, please do not remove.
    }

    public WildflyProviderImpl(final WildflyProviderConfigImpl config) {
        super(config.getName(),
              WildflyProviderType.instance(),
              config);
    }

    @Override
    public WildflyProvider asNewClone(final WildflyProvider origin) {
        return new WildflyProviderImpl(new WildflyProviderConfigImpl(origin.getConfig().getName(),
                                                                     origin.getConfig().getHost(),
                                                                     origin.getConfig().getPort(),
                                                                     origin.getConfig().getManagementPort(),
                                                                     origin.getConfig().getUser(),
                                                                     origin.getConfig().getPassword()));
    }
}