/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.content.view;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@JsType
public final class ViewConnectorImpl<W> implements ViewConnector<W> {

    protected W definition;
    protected Bounds bounds;
    private Connection sourceConnection;
    private Connection targetConnection;
    private ControlPoint[] controlPoints;

    public ViewConnectorImpl(W definition,
                             Bounds bounds) {
        this.definition = definition;
        this.bounds = bounds;
        this.sourceConnection = null;
        this.targetConnection = null;
        this.controlPoints = new ControlPoint[0];
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

    public Optional<Connection> getSourceConnection() {
        return Optional.ofNullable(sourceConnection);
    }

    public Optional<Connection> getTargetConnection() {
        return Optional.ofNullable(targetConnection);
    }

    public void setSourceConnection(final Connection sourceConnection) {
        this.sourceConnection = sourceConnection;
    }

    public void setTargetConnection(final Connection targetConnection) {
        this.targetConnection = targetConnection;
    }

    @Override
    public ControlPoint[] getControlPoints() {
        return controlPoints;
    }

    @Override
    public void setControlPoints(ControlPoint[] controlPoints) {
        this.controlPoints = controlPoints;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(definition.hashCode(),
                                         bounds.hashCode(),
                                         getSourceConnection().hashCode(),
                                         getTargetConnection().hashCode(),
                                         HashUtil.combineHashCodes(Stream.of(getControlPoints())
                                                                           .map(ControlPoint::hashCode)
                                                                           .mapToInt(i -> i)
                                                                           .toArray()));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ViewConnector) {
            ViewConnector other = (ViewConnector) o;
            return definition.equals(other.getDefinition()) &&
                    bounds.equals(other.getBounds()) &&
                    getSourceConnection().equals(other.getSourceConnection()) &&
                    getTargetConnection().equals(other.getTargetConnection()) &&
                    Arrays.equals(getControlPoints(), other.getControlPoints());
        }
        return false;
    }
}
