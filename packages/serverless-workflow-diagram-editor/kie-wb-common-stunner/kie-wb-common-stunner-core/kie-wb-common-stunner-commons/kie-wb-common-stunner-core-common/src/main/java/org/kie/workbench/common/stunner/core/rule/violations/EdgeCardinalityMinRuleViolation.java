/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.rule.violations;

import java.util.Optional;

import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;

public class EdgeCardinalityMinRuleViolation extends AbstractRuleViolation {

    private final String node;
    private final String edge;
    private final Integer restrictedOccurrences;
    private final Integer currentOccurrences;
    private final EdgeCardinalityContext.Direction direction;

    public EdgeCardinalityMinRuleViolation(final String node,
                                           final String edge,
                                           final Integer restrictedOccurrences,
                                           final Integer currentOccurrences,
                                           final EdgeCardinalityContext.Direction direction,
                                           final Type type) {
        super(type);
        this.node = node;
        this.edge = edge;
        this.restrictedOccurrences = restrictedOccurrences;
        this.currentOccurrences = currentOccurrences;
        this.direction = direction;
    }

    @Override
    public Optional<Object[]> getArguments() {
        return of(node, edge, direction, restrictedOccurrences, currentOccurrences);
    }

    @Override
    public String getMessage() {
        return "The node '" + node + "' can have a minimum " +
                "of'" + restrictedOccurrences + "' occurrences for " + direction + " edge/s '" + edge + "'." +
                " But currently found '" + currentOccurrences + "' occurrences.";
    }
}
