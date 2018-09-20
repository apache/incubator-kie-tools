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

import java.util.ArrayList;

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;
import org.drools.workbench.services.verifier.plugin.client.builders.ModelMetaDataEnhancer.PatternType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModelMetaDataEnhancerTest {

    @Mock
    GuidedDecisionTable52 model;

    @Test
    public void emptyTableHasEmptyHeaderMetaData() throws
            Exception {

        assertTrue(new ModelMetaDataEnhancer(model).getHeaderMetaData()
                           .isEmpty());
    }

    @Test
    public void conditionCol52Column() throws
            Exception {

        final ArrayList<BaseColumn> columns = new ArrayList<>();
        final ConditionCol52 conditionCol52 = new ConditionCol52();
        final Pattern52 pattern52 = new Pattern52();

        columns.add(conditionCol52);

        when(model.getExpandedColumns()).thenReturn(columns);
        when(model.getPattern(conditionCol52)).thenReturn(pattern52);

        final HeaderMetaData headerMetaData = new ModelMetaDataEnhancer(model)
                .getHeaderMetaData();

        assertEquals(1,
                     headerMetaData.size());
        assertEquals(pattern52,
                     headerMetaData.getPatternsByColumnNumber(0).getPattern());
        assertEquals(PatternType.LHS,
                     headerMetaData.getPatternsByColumnNumber(0).getPatternType());
    }
}