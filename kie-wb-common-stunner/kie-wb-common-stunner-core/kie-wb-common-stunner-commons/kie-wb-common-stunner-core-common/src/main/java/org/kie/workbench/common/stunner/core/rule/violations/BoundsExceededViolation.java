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

package org.kie.workbench.common.stunner.core.rule.violations;

import java.util.Optional;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

@Portable
public class BoundsExceededViolation extends AbstractRuleViolation {

    private final Bounds bounds;

    public BoundsExceededViolation(final @MapsTo("bounds") Bounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(getUUID(), bounds.getLowerRight().getX(), bounds.getLowerRight().getY());
    }

    @Override
    public String getMessage() {
        return "Diagram bounds exceeded "
                + "[candidate=" + getUUID()
                + "[maxX=" + bounds.getLowerRight().getX()
                + "[maxY=" + bounds.getLowerRight().getY()
                + "]";
    }
}
