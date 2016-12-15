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

package org.kie.workbench.common.stunner.core.rule.impl.violations;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

// TODO: I18n.
@Portable
public class ContainmentRuleViolation extends AbstractRuleViolation {

    private String graph;
    private String candidate;

    public ContainmentRuleViolation( @MapsTo( "graph" ) String graph,
                                     @MapsTo( "candidate" ) String candidate ) {
        this.graph = graph;
        this.candidate = candidate;
    }

    @Override
    public String getMessage() {
        return "The '" + graph + "' cannot contain the labels ['" + candidate + "'].";
    }

}
