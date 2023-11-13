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


package org.kie.workbench.common.stunner.core.lookup.rule;

import java.util.Set;

import org.kie.workbench.common.stunner.core.lookup.AbstractLookupRequest;
import org.kie.workbench.common.stunner.core.lookup.AbstractLookupRequestBuilder;

public class RuleLookupRequest extends AbstractLookupRequest {

    private final String definitionSetId;

    public RuleLookupRequest(final String criteria,
                             final int page,
                             final int pageSize,
                             final String definitionSetId) {
        super(criteria,
              page,
              pageSize);
        this.definitionSetId = definitionSetId;
    }

    public String getDefinitionSetId() {
        return definitionSetId;
    }

    public static class Builder extends AbstractLookupRequestBuilder<Builder> {

        public enum RuleType {
            CONNECTION,
            CONTAINMENT,
            CARDINALITY,
            EDGECARDINALITY;
        }

        public enum EdgeType {
            INCOMING,
            OUTGOING;
        }

        private String defSetId;
        private final StringBuilder criteria = new StringBuilder();

        public Builder id(final String id) {
            criteria.append("id=").append(id).append(";");
            return this;
        }

        public Builder type(final RuleType ruleType) {
            criteria.append("type=").append(ruleType.toString().toLowerCase()).append(";");
            return this;
        }

        public Builder definitionSetId(final String defSetId) {
            this.defSetId = defSetId;
            return this;
        }

        public Builder from(final Set<String> labels) {
            criteria.append("from=").append(fromSet(labels)).append(";");
            return this;
        }

        public Builder role(final String role) {
            criteria.append("role=").append(role).append(";");
            return this;
        }

        public Builder roleIn(final Set<String> roles) {
            criteria.append("roleIn=").append(fromSet(roles)).append(";");
            return this;
        }

        public Builder edgeType(final EdgeType type) {
            criteria.append(" edgeType=").append(type.name().toLowerCase()).append(";");
            return this;
        }

        public RuleLookupRequest build() {
            return new RuleLookupRequest(criteria.toString(),
                                         page,
                                         pageSize,
                                         defSetId);
        }
    }
}
