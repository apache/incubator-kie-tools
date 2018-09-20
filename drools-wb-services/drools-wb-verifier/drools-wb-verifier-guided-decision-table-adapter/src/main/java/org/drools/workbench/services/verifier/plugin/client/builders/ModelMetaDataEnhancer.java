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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.drools.workbench.services.verifier.plugin.client.api.ModelMetaData;
import org.kie.soup.commons.validation.PortablePreconditions;

public class ModelMetaDataEnhancer {

    private final GuidedDecisionTable52 model;

    public ModelMetaDataEnhancer(final GuidedDecisionTable52 model) {
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
    }

    public HeaderMetaData getHeaderMetaData() {

        int columnIndex = 0;
        final Map<Integer, ModelMetaData> map = new HashMap<>();

        for (final BaseColumn baseColumn : model.getExpandedColumns()) {
            if (baseColumn instanceof ConditionCol52) {
                map.put(columnIndex,
                        new ModelMetaData(model.getPattern((ConditionCol52) baseColumn),
                                          PatternType.LHS));
            } else if (baseColumn instanceof ActionInsertFactCol52) {
                final ActionInsertFactCol52 aif = (ActionInsertFactCol52) baseColumn;
                map.put(columnIndex,
                        new ModelMetaData(aif.getFactType(),
                                          aif.getBoundName(),
                                          PatternType.RHS));
            } else if (baseColumn instanceof ActionSetFieldCol52) {
                final ActionSetFieldCol52 asf = (ActionSetFieldCol52) baseColumn;
                map.put(columnIndex,
                        new ModelMetaData(getFactType(asf),
                                          asf.getBoundName(),
                                          PatternType.RHS));
            }

            columnIndex++;
        }

        return new HeaderMetaData(map);
    }

    private String getFactType(final ActionSetFieldCol52 asf) {
        final String binding = asf.getBoundName();
        final Optional<Pattern52> pattern = Optional.ofNullable(model.getConditionPattern(binding));
        if (pattern.isPresent()) {
            return pattern.get().getFactType();
        }

        return model.getActionCols()
                .stream()
                .filter(c -> c instanceof ActionInsertFactCol52)
                .map(c -> (ActionInsertFactCol52) c)
                .filter(c -> c.getBoundName().equals(binding))
                .findFirst()
                .map(ActionInsertFactCol52::getFactType)
                .get();
    }

    public enum PatternType {
        LHS,
        RHS
    }
}
