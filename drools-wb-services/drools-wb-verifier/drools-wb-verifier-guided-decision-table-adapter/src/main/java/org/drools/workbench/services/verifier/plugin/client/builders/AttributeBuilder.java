/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import java.util.Date;
import java.util.Optional;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.DateEffectiveRuleAttribute;
import org.drools.verifier.core.index.model.DateExpiresRuleAttribute;
import org.drools.verifier.core.index.model.RuleAttribute;
import org.drools.verifier.core.util.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;

public class AttributeBuilder {

    public static Optional<RuleAttribute> build(final int columnIndex,
                                                final AnalyzerConfiguration configuration,
                                                final String attribute,
                                                final DTCellValue52 realCellValue) {
        PortablePreconditions.checkNotNull("columnIndex",
                                           columnIndex);
        PortablePreconditions.checkNotNull("attribute",
                                           attribute);
        PortablePreconditions.checkNotNull("realCellValue",
                                           realCellValue);

        final Comparable value = ActionBuilder.getValue(realCellValue);

        if (attribute.equals(DateExpiresRuleAttribute.NAME)) {
            final Date date = toDate(configuration,
                                     value);
            if (date != null) {
                return Optional.of(new DateExpiresRuleAttribute(columnIndex,
                                                                date));
            }
        } else if (attribute.equals(DateEffectiveRuleAttribute.NAME)) {
            final Date date = toDate(configuration,
                                     value);
            if (date != null) {
                return Optional.of(new DateEffectiveRuleAttribute(columnIndex,
                                                                  date));
            }
        }

        return Optional.empty();
    }

    private static Date toDate(final AnalyzerConfiguration configuration,
                               final Comparable comparable) {
        if (comparable instanceof Date) {
            return (Date) comparable;
        } else if (comparable instanceof String) {
            configuration.parse((String) comparable);
        }
        return null;
    }
}
