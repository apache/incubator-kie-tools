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

package org.guvnor.ala.wildfly.service;

import javax.inject.Inject;

import org.guvnor.ala.exceptions.RuntimeOperationException;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.RuntimeManager;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.WildflyAppState;
import org.guvnor.ala.wildfly.access.exceptions.WildflyClientException;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.model.WildflyRuntimeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyRuntimeManager implements RuntimeManager {

    private final RuntimeRegistry runtimeRegistry;
    private final WildflyAccessInterface wildfly;
    protected static final Logger LOG = LoggerFactory.getLogger(WildflyRuntimeManager.class);

    @Inject
    public WildflyRuntimeManager(final RuntimeRegistry runtimeRegistry,
                                 final WildflyAccessInterface docker) {
        this.runtimeRegistry = runtimeRegistry;
        this.wildfly = docker;
    }

    @Override
    public boolean supports(final RuntimeId runtimeId) {
        return runtimeId instanceof WildflyRuntime
                || runtimeRegistry.getRuntimeById(runtimeId.getId()) instanceof WildflyRuntime;
    }

    @Override
    public void start(RuntimeId runtimeId) throws RuntimeOperationException {
        WildflyRuntime runtime = (WildflyRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        int result = wildfly.getWildflyClient(runtime.getProviderId()).start(runtime.getId());
        if (result != 200) {
            throw new RuntimeOperationException("Error Starting container: " + runtimeId.getId()
                                                        + " \n\t There as a problem with starting your application, please check into the Wildfly Logs for more information.");
        }
        refresh(runtimeId);
    }

    @Override
    public void stop(RuntimeId runtimeId) throws RuntimeOperationException {
        WildflyRuntime runtime = (WildflyRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());

        try {
            wildfly.getWildflyClient(runtime.getProviderId()).stop(runtime.getId());
            refresh(runtimeId);
        } catch (WildflyClientException ex) {
            throw new RuntimeOperationException("Error Stopping container: " + runtimeId.getId()
                                                        + "\n\t There as a problem with stopping your application, please check into the Wildfly Logs for more information.",
                                                ex);
        }
    }

    @Override
    public void restart(RuntimeId runtimeId) {
        WildflyRuntime runtime = (WildflyRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            wildfly.getWildflyClient(runtime.getProviderId()).restart(runtime.getId());
            refresh(runtimeId);
        } catch (WildflyClientException ex) {
            throw new RuntimeOperationException("Error Restarting container: " + runtimeId.getId()
                                                        + "\n\t There as a problem with restarting your application, please check into the Wildfly Logs for more information.",
                                                ex);
        }
    }

    @Override
    public void refresh(RuntimeId runtimeId) throws RuntimeOperationException {
        WildflyRuntime runtime = (WildflyRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            WildflyAppState appState = wildfly.getWildflyClient(runtime.getProviderId()).getAppState(runtime.getId());
            WildflyRuntime newRuntime = new WildflyRuntime(runtime.getId(),
                                                           runtime.getName(),
                                                           runtime.getConfig(),
                                                           runtime.getProviderId(),
                                                           runtime.getEndpoint(),
                                                           runtime.getInfo(),
                                                           new WildflyRuntimeState(appState.getState(),
                                                                                   runtime.getState().getStartedAt()));
            runtimeRegistry.registerRuntime(newRuntime);
        } catch (WildflyClientException ex) {
            throw new RuntimeOperationException("Error Refreshing container: " + runtimeId.getId()
                                                        + "\n\t There as a problem with refreshing your application, please check into the Wildfly Logs for more information.",
                                                ex);
        }
    }

    @Override
    public void pause(RuntimeId runtimeId) throws RuntimeOperationException {
        WildflyRuntime runtime = (WildflyRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            wildfly.getWildflyClient(runtime.getProviderId()).stop(runtime.getId());
            refresh(runtimeId);
        } catch (WildflyClientException ex) {
            throw new RuntimeOperationException("Error Pausing container: " + runtimeId.getId()
                                                        + "\n\t There as a problem with stopping your application, please check into the Wildfly Logs for more information.",
                                                ex);
        }
    }
}
