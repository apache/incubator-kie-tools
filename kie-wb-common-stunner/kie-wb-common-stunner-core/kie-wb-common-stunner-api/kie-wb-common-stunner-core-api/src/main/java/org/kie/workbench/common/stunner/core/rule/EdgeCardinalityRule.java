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

package org.kie.workbench.common.stunner.core.rule;

/**
 * Rule to restrict the cardinality of connections for connector/edges.
 */
public interface EdgeCardinalityRule extends RuleByRole, RuleById {

    // The connection type.
    enum Type {
        INCOMING,
        OUTGOING;
    }

    /**
     * The minimum number of connections an Element can have.
     */
    int getMinOccurrences();

    /**
     * The maximum number of connections an Element can have.
     */
    int getMaxOccurrences();

    /**
     * The connection type.
     */
    Type getType();

}
