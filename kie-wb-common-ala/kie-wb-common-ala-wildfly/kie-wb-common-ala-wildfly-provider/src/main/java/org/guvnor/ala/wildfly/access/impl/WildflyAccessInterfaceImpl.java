/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.wildfly.access.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.WildflyClient;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class WildflyAccessInterfaceImpl
        implements WildflyAccessInterface {

    protected static final Logger LOG = LoggerFactory.getLogger(WildflyAccessInterfaceImpl.class);
    private final Map<String, WildflyClient> clientMap = new ConcurrentHashMap<>();

    @Override
    public WildflyClient getWildflyClient(final ProviderId providerId) {
        if (!clientMap.containsKey(providerId.getId())) {
            clientMap.put(providerId.getId(),
                          buildClient(providerId));
        }
        return clientMap.get(providerId.getId());
    }

    private WildflyClient buildClient(final ProviderId providerId) {
        assert (providerId instanceof WildflyProvider);
        WildflyProvider wildflyProvider = ((WildflyProvider) providerId);

        return new WildflyClient(
                wildflyProvider.getConfig().getName(),
                wildflyProvider.getConfig().getUser(),
                wildflyProvider.getConfig().getPassword(),
                wildflyProvider.getConfig().getHost(),
                Integer.valueOf(defaultIfBlank(wildflyProvider.getConfig().getPort(),
                                               "8080")),
                Integer.valueOf(defaultIfBlank(wildflyProvider.getConfig().getManagementPort(),
                                               "9990"))
        );
    }

    @Override
    public void dispose() {
        clientMap.values().forEach(WildflyClient::close);
    }
}
