/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client.builders;

import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.ObjectType;
import org.drools.workbench.services.verifier.api.client.index.Pattern;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;

public class PatternResolver {

    private final Index index;
    private final AnalyzerConfiguration configuration;
    private final HeaderMetaData headerMetaData;

    private Rule rule;
    private int columnIndex;

    public PatternResolver(final Index index,
                           final HeaderMetaData headerMetaData,
                           final AnalyzerConfiguration configuration) {
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.headerMetaData = PortablePreconditions.checkNotNull("headerMetaData",
                                                                 headerMetaData);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    private ObjectType resolveObjectType(final String factType) {
        final ObjectType first = index.getObjectTypes()
                .where(ObjectType.type()
                               .is(factType))
                .select()
                .first();

        if (first == null) {
            final ObjectType objectType = new ObjectType(factType,
                                                         configuration);
            index.getObjectTypes()
                    .add(objectType);
            return objectType;
        } else {
            return first;
        }
    }

    public Pattern resolve() {

        PortablePreconditions.checkNotNull("rule",
                                           rule);

        final Pattern pattern = rule.getPatterns()
                .where(Pattern.boundName()
                               .is(getBoundName()))
                .select()
                .first();

        if (pattern == null) {
            final Pattern build = new Pattern(getBoundName(),
                                              resolveObjectType(getFactType()),
                                              configuration);

            rule.getPatterns().add(build);

            return build;
        } else {
            return pattern;
        }
    }

    private String getFactType() {
        final String factType = headerMetaData
                .getPatternsByColumnNumber(PortablePreconditions.checkNotNull("columnIndex",
                                                                              columnIndex))
                .getFactType();
        return factType;
    }

    private String getBoundName() {
        final String boundName = headerMetaData
                .getPatternsByColumnNumber(PortablePreconditions.checkNotNull("columnIndex",
                                                                              columnIndex))
                .getBoundName();
        return boundName;
    }

    public PatternResolver with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public PatternResolver with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
