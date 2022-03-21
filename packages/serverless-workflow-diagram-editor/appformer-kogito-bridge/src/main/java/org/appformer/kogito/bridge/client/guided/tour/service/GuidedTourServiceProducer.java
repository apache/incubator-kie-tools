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

package org.appformer.kogito.bridge.client.guided.tour.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import elemental2.dom.DomGlobal;
import org.appformer.kogito.bridge.client.interop.WindowRef;

import static org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService.DEFAULT;

/**
 * Produces {@link GuidedTourService} beans according to whether the envelope API is available or not
 */
public class GuidedTourServiceProducer {

    @Produces
    @ApplicationScoped
    public GuidedTourService produce() {
        if (isEnvelopeAvailable()) {
            return new GuidedTourServiceEnvelope();
        }
        DomGlobal.console.info("[GuidedTourServiceProducer] Envelope API is not available.");
        return DEFAULT;
    }

    boolean isEnvelopeAvailable() {
        return WindowRef.isEnvelopeAvailable();
    }
}
