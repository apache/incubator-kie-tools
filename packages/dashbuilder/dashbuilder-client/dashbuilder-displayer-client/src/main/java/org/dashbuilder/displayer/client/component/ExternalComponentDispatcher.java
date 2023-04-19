/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.displayer.client.component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.MessageEvent;
import jsinterop.base.Js;
import org.dashbuilder.displayer.client.resources.i18n.CommonConstants;
import org.dashbuilder.displayer.external.ExternalComponentMessage;
import org.dashbuilder.displayer.external.ExternalComponentMessageHelper;
import org.dashbuilder.displayer.external.ExternalComponentMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen to all component messages and find the destination.
 *
 */
@ApplicationScoped
public class ExternalComponentDispatcher {

    private static final CommonConstants i18n = CommonConstants.INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalComponentDispatcher.class);

    @Inject
    ExternalComponentMessageHelper messageHelper;

    Set<ExternalComponentListener> listeners;

    @PostConstruct
    public void setup() {
        listeners = new HashSet<>();
        startListening();
    }

    public void register(ExternalComponentListener listener) {
        // make the component listener ready by default
        listener.prepare();
        listeners.add(listener);
    }

    public void unregister(ExternalComponentListener listener) {
        listeners.remove(listener);
    }

    public void onMessage(ExternalComponentMessage message) {

        ExternalComponentMessageType type = messageHelper.messageType(message);

        switch (type) {
            case FILTER:
                handleFilter(message);
                break;

            case READY:
                handleReady(message);
                break;

            case FIX_CONFIGURATION:
                handleConfiguration(message);
                break;

            case CONFIGURATION_OK:
                handleOkConfiguration(message);
                break;

            default:
                break;
        }

    }

    private void handleOkConfiguration(ExternalComponentMessage message) {
        findDestination(message, ExternalComponentListener::configurationOk);
    }

    private void handleConfiguration(ExternalComponentMessage message) {
        findDestination(message, destination -> destination.onConfigurationIssue(messageHelper.getConfigurationIssue(message)
                                                                                              .orElse(i18n.componentConfigDefaultMessage())));
    }

    private void handleReady(ExternalComponentMessage message) {
        findDestination(message, ExternalComponentListener::prepare);
    }

    private void handleFilter(ExternalComponentMessage message) {
        findDestination(message, listener -> messageHelper.filterRequest(message).ifPresent(listener::onFilter));
    }

    private void findDestination(ExternalComponentMessage message,
                                 Consumer<ExternalComponentListener> consumeDestination) {
        Optional<ExternalComponentListener> destinationOp = messageHelper.getComponentId(message)
                                                                         .flatMap(id -> listeners.stream()
                                                                                                 .filter(listener -> listener.getId().equals(id))
                                                                                                 .findAny());
        destinationOp.ifPresent(consumeDestination);
        if (!destinationOp.isPresent()) {
            LOGGER.warn("Ignoring message, destination not found.");
        }
    }

    private void startListening() {
        DomGlobal.window.addEventListener("message", e -> {
            MessageEvent<Object> event = Js.cast(e);
            if (event.data instanceof ExternalComponentMessage) {
                this.onMessage(Js.cast(event.data));
            }
        });
    }

}