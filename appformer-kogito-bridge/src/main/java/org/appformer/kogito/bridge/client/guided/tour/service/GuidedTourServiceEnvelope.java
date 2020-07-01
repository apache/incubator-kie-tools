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

import org.appformer.kogito.bridge.client.guided.tour.service.api.Tutorial;
import org.appformer.kogito.bridge.client.guided.tour.service.api.UserInteraction;

/**
 * Expose Guided Tour features provided by the native envelope on {@link GuidedTourServiceNativeEnvelope}.
 */
public class GuidedTourServiceEnvelope implements GuidedTourService {

    @Override
    public void refresh(final UserInteraction userInteraction) {
        getNativeEnvelope().refresh(userInteraction);
    }

    @Override
    public void registerTutorial(final Tutorial tutorial) {
        getNativeEnvelope().registerTutorial(tutorial);
    }

    @Override
    public boolean isEnabled() {
        return getNativeEnvelope().isEnabled();
    }

    GuidedTourServiceNativeEnvelope getNativeEnvelope() {
        return GuidedTourServiceNativeEnvelope.get();
    }
}
