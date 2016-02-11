/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.dtablexls.backend.server.conversion.builders;

import java.util.List;
import java.util.Map;

import org.drools.workbench.models.guided.dtable.shared.model.BRLVariableColumn;

/**
 * Interface for additional responsibilities for Guided Decision Table builders
 */
public interface GuidedDecisionTableSourceBuilderIndirect
        extends
        GuidedDecisionTableSourceBuilder {

    /**
     * Get the BRL Variable Columns required to represent the XLS Column. The mapping between
     * XLS column and Guided Decision Table column may not be 1-to-1 and hence we gather the
     * BRL Variable Columns and match against RuleModel after all columns have been handled.
     * @return
     */
    List<BRLVariableColumn> getVariableColumns();

    /**
     * Get the ParameterizedValueBuilder for the BRL Fragment. The map is keyed on source XLS column index.
     * @return
     */
    Map<Integer, ParameterizedValueBuilder> getValueBuilders();

}
