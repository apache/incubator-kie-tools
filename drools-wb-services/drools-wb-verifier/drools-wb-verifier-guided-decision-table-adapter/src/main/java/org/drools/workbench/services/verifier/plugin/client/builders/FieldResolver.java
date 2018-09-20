/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;

public class FieldResolver {

    private final BuilderFactory builderFactory;
    private final HeaderMetaData headerMetaData;
    private final AnalyzerConfiguration configuration;
    private BaseColumn baseColumn;
    private Rule rule;
    private Pattern pattern;
    private int columnIndex;

    public FieldResolver(final BuilderFactory builderFactory,
                         final HeaderMetaData headerMetaData,
                         final AnalyzerConfiguration configuration) {
        this.builderFactory = PortablePreconditions.checkNotNull("builderFactory",
                                                                 builderFactory);
        this.headerMetaData = PortablePreconditions.checkNotNull("headerMetaData",
                                                                 headerMetaData);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);
    }

    public Field resolveField(final Pattern pattern,
                              final String fieldType,
                              final String factField,
                              final AnalyzerConfiguration configuration) {

        PortablePreconditions.checkNotNull("pattern",
                                           pattern);
        PortablePreconditions.checkNotNull("fieldType",
                                           fieldType);
        PortablePreconditions.checkNotNull("factField",
                                           factField);

        final Field first = pattern.getFields()
                .where(Field.name()
                               .is(factField))
                .select()
                .first();

        if (first == null) {
            final Field field = new Field(Utils.resolveObjectField(pattern.getObjectType(),
                                                                   fieldType,
                                                                   factField,
                                                                   configuration),
                                          pattern.getName(),
                                          fieldType,
                                          factField,
                                          configuration);
            pattern.getFields()
                    .add(field);
            return field;
        } else {
            return first;
        }
    }

    public Field resolve() throws
            BuildException {
        if (rule != null) {
            return resolveField(getPattern(),
                                getType(),
                                getFactField(),
                                configuration);
        } else {
            return resolveField(pattern,
                                getType(),
                                getFactField(),
                                configuration);
        }
    }

    private Pattern getPattern() {
        if (pattern != null) {
            return pattern;
        } else {

            final PatternResolver patternResolver = builderFactory.getPatternResolver()
                    .with(rule)
                    .with(columnIndex);

            if (baseColumn instanceof ActionCol52) {
                return patternResolver
                        .with(columnIndex)
                        .resolve();
            } else {
                return patternResolver
                        .resolve();
            }
        }
    }

    private String getFactField() {
        if (baseColumn instanceof ConditionCol52) {
            return ((ConditionCol52) baseColumn).getFactField();
        } else if (baseColumn instanceof ActionSetFieldCol52) {
            return ((ActionSetFieldCol52) baseColumn).getFactField();
        } else if (baseColumn instanceof ActionInsertFactCol52) {
            return ((ActionInsertFactCol52) baseColumn).getFactField();
        } else {
            return null;
        }
    }

    private String getType() {
        if (baseColumn instanceof ConditionCol52) {
            return ((ConditionCol52) baseColumn).getFieldType();
        } else if (baseColumn instanceof ActionSetFieldCol52) {
            return ((ActionSetFieldCol52) baseColumn).getType();
        } else if (baseColumn instanceof ActionInsertFactCol52) {
            return ((ActionInsertFactCol52) baseColumn).getType();
        } else {
            return null;
        }
    }

    public FieldResolver with(final BaseColumn actionCol52) {
        this.baseColumn = actionCol52;
        return this;
    }

    public FieldResolver with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public FieldResolver with(final Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public FieldResolver with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
