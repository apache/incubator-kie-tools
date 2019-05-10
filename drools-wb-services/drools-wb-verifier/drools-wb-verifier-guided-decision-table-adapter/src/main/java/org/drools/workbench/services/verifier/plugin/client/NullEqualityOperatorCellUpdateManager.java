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
package org.drools.workbench.services.verifier.plugin.client;

import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Condition;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;

public class NullEqualityOperatorCellUpdateManager
        extends CellUpdateManagerBase {

    public NullEqualityOperatorCellUpdateManager(final Index index,
                                                 final GuidedDecisionTable52 model,
                                                 final Coordinate coordinate) {
        super(index,
              model,
              coordinate);
    }

    @Override
    protected boolean updateCondition(final Condition condition) {
        final Boolean cellValue = model.getData()
                .get(coordinate.getRow())
                .get(coordinate.getCol())
                .getBooleanValue();

        if (cellValue != null && cellValue) {
            if (condition.getValues()
                    .isEmpty()) {
                condition.setValue(Values.nullValue());
                return true;
            } else {
                return false;
            }
        } else {
            if (condition.getValues()
                    .isEmpty()) {
                return false;
            } else {
                condition.setValue(new Values());
                return true;
            }
        }
    }

    @Override
    protected boolean updateAction(final Action action) {
        return false;
    }
}
