/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.rule.context.ConnectorCardinalityContext;

@Portable
public final class EdgeOccurrences extends Occurrences {

    private final String edgeId;
    private final ConnectorCardinalityContext.Direction direction;

    public EdgeOccurrences(final @MapsTo("name") String name,
                           final @MapsTo("edgeId") String edgeId,
                           final @MapsTo("role") String candidateRole,
                           final @MapsTo("direction") ConnectorCardinalityContext.Direction direction,
                           final @MapsTo("minOccurrences") int minOccurrences,
                           final @MapsTo("maxOccurrences") int maxOccurrences) {
        super(name,
              candidateRole,
              minOccurrences,
              maxOccurrences);
        this.edgeId = edgeId;
        this.direction = direction;
    }

    public String getEdgeId() {
        return edgeId;
    }

    public ConnectorCardinalityContext.Direction getDirection() {
        return direction;
    }
}
