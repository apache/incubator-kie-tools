/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.content.view;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

@Portable
public final class ViewConnectorImpl<W> implements ViewConnector<W> {

    protected W definition;
    protected Bounds bounds;
    private Magnet sourceMagnet;
    private Magnet targetMagnet;

    public ViewConnectorImpl(final @MapsTo("definition") W definition,
                             final @MapsTo("bounds") Bounds bounds) {
        this.definition = definition;
        this.bounds = bounds;
        this.sourceMagnet = null;
        this.targetMagnet = null;
    }

    @Override
    public W getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(final W definition) {
        this.definition = definition;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(final Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public Optional<Magnet> getSourceMagnet() {
        return Optional.ofNullable(sourceMagnet);
    }

    @Override
    public Optional<Magnet> getTargetMagnet() {
        return Optional.ofNullable(targetMagnet);
    }

    @Override
    public void setSourceMagnet(final Magnet sourceMagnet) {
        this.sourceMagnet = sourceMagnet;
    }

    @Override
    public void setTargetMagnet(final Magnet targetMagnet) {
        this.targetMagnet = targetMagnet;
    }

}
