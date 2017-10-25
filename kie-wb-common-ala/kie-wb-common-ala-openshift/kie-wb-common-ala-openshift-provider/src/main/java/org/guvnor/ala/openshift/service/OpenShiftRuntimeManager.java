/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.service;

import javax.inject.Inject;

import org.guvnor.ala.exceptions.RuntimeOperationException;
import org.guvnor.ala.openshift.access.OpenShiftAccessInterface;
import org.guvnor.ala.openshift.access.exceptions.OpenShiftClientException;
import org.guvnor.ala.openshift.model.OpenShiftRuntime;
import org.guvnor.ala.openshift.model.OpenShiftRuntimeState;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for the "start", "stop", "restart", and "pause" lifecycle aspects of the openshift runtime.
 */
public class OpenShiftRuntimeManager implements RuntimeManager {

    private final RuntimeRegistry runtimeRegistry;
    private final OpenShiftAccessInterface openshift;
    private static final Logger LOG = LoggerFactory.getLogger(OpenShiftRuntimeManager.class);

    @Inject
    public OpenShiftRuntimeManager(final RuntimeRegistry runtimeRegistry, final OpenShiftAccessInterface openshift) {
        this.runtimeRegistry = runtimeRegistry;
        this.openshift = openshift;
    }

    @Override
    public boolean supports(final RuntimeId runtimeId) {
        return runtimeId instanceof OpenShiftRuntime || runtimeRegistry.getRuntimeById(runtimeId.getId()) instanceof OpenShiftRuntime;
    }

    @Override
    public void start(RuntimeId runtimeId) throws RuntimeOperationException {
        OpenShiftRuntime runtime = (OpenShiftRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            LOG.info("Starting runtime: " + runtimeId.getId());
            openshift.getOpenShiftClient(runtime.getProviderId()).start(runtime.getId());
            refresh(runtimeId);
            LOG.info("Started runtime: " + runtimeId.getId());
        } catch (OpenShiftClientException ex) {
            LOG.error("Error Starting runtime: " + runtimeId.getId(), ex);
            throw new RuntimeOperationException("Error Starting runtime: " + runtimeId.getId(), ex);
        }
    }

    @Override
    public void stop(RuntimeId runtimeId) throws RuntimeOperationException {
        OpenShiftRuntime runtime = (OpenShiftRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            LOG.info("Stopping runtime: " + runtimeId.getId());
            openshift.getOpenShiftClient(runtime.getProviderId()).stop(runtime.getId());
            refresh(runtimeId);
            LOG.info("Stopped runtime: " + runtimeId.getId());
        } catch (OpenShiftClientException ex) {
            LOG.error("Error Stopping runtime: " + runtimeId.getId(), ex);
            throw new RuntimeOperationException("Error Stopping runtime: " + runtimeId.getId(), ex);
        }
    }

    @Override
    public void restart(RuntimeId runtimeId) throws RuntimeOperationException {
        OpenShiftRuntime runtime = (OpenShiftRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            LOG.info("Restarting runtime: " + runtimeId.getId());
            openshift.getOpenShiftClient(runtime.getProviderId()).restart(runtime.getId());
            refresh(runtimeId);
            LOG.info("Restarted runtime: " + runtimeId.getId());
        } catch (OpenShiftClientException ex) {
            LOG.error("Error Restarting runtime: " + runtimeId.getId(), ex);
            throw new RuntimeOperationException("Error Restarting runtime: " + runtimeId.getId(), ex);
        }
    }

    @Override
    public void refresh(RuntimeId runtimeId) throws RuntimeOperationException {
        OpenShiftRuntime runtime = (OpenShiftRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        if (runtime != null) {
            try {
                //LOG.info( "Refreshing runtime: " + runtimeId.getId() );
                OpenShiftRuntimeState runtimeState = openshift.getOpenShiftClient(runtime.getProviderId()).getRuntimeState(runtime.getId());
                OpenShiftRuntime newRuntime = new OpenShiftRuntime(runtime.getId(), runtime.getName(), runtime.getConfig(), runtime.getProviderId(), runtime.getEndpoint(), runtime.getInfo(), runtimeState);
                runtimeRegistry.registerRuntime(newRuntime);
            } catch (OpenShiftClientException ex) {
                LOG.error("Error Refreshing runtime: " + runtimeId.getId(), ex);
                throw new RuntimeOperationException("Error Refreshing runtime: " + runtimeId.getId(), ex);
            }
        }
    }

    @Override
    public void pause(RuntimeId runtimeId) throws RuntimeOperationException {
        OpenShiftRuntime runtime = (OpenShiftRuntime) runtimeRegistry.getRuntimeById(runtimeId.getId());
        try {
            LOG.info("Pausing runtime: " + runtimeId.getId());
            openshift.getOpenShiftClient(runtime.getProviderId()).pause(runtime.getId());
            refresh(runtimeId);
            LOG.info("Paused runtime: " + runtimeId.getId());
        } catch (OpenShiftClientException ex) {
            LOG.error("Error Pausing runtime: " + runtimeId.getId(), ex);
            throw new RuntimeOperationException("Error Pausing runtime: " + runtimeId.getId(), ex);
        }
    }

}
