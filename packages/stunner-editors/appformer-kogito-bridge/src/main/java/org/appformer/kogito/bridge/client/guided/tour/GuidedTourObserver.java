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

package org.appformer.kogito.bridge.client.guided.tour;

import java.util.Optional;

import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourService;
import org.jboss.errai.ioc.client.api.Disposer;

/**
 * {@link GuidedTourObserver} implementation must events and notifies the {@link GuidedTourBridge}.
 * Notice: {@link GuidedTourObserver} instances must be disposed once the {@link GuidedTourService} is disable, this
 * provides the proper {@link Disposer}.
 */
public abstract class GuidedTourObserver<T extends GuidedTourObserver> {

    private final Disposer<T> selfDisposer;

    private GuidedTourBridge bridge;

    public GuidedTourObserver(final Disposer<T> selfDisposer) {
        this.selfDisposer = selfDisposer;
    }

    protected Optional<GuidedTourBridge> getMonitorBridge() {
        return Optional.ofNullable(this.bridge);
    }

    protected void setMonitorBridge(final GuidedTourBridge bridge) {
        this.bridge = bridge;
    }

    @SuppressWarnings("unchecked")
    protected void dispose() {
        selfDisposer.dispose((T) this);
    }
}
