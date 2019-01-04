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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.api.core.DMNModel;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DMNTypeServiceImplTest extends AbstractDMNTest {

    private DMNTypeServiceImpl dmnTypeServiceImpl;

    @Before
    public void init() {
        super.init();
        dmnTypeServiceImpl = new DMNTypeServiceImpl() {
            @Override
            public DMNModel getDMNModel(Path path, String stringPath) {
                return dmnModelMock;
            }
        };
    }

    @Test
    public void retrieveType() {
        FactModelTuple factModelTuple = dmnTypeServiceImpl.retrieveType(mock(Path.class), null);
        assertEquals(SIMPLE_TYPE_NAME, factModelTuple.getVisibleFacts().get(SIMPLE_TYPE_NAME).getFactName());
        assertEquals(BASE_TYPE, factModelTuple.getVisibleFacts().get(SIMPLE_TYPE_NAME).getSimpleProperties().get("value"));
        assertTrue(factModelTuple.getVisibleFacts().get(SIMPLE_TYPE_NAME).isSimple());

        assertEquals(SIMPLE_DECISION_TYPE_NAME, factModelTuple.getVisibleFacts().get(SIMPLE_DECISION_TYPE_NAME).getFactName());
        assertEquals(BASE_TYPE, factModelTuple.getVisibleFacts().get(SIMPLE_DECISION_TYPE_NAME).getSimpleProperties().get("value"));
        assertTrue(factModelTuple.getVisibleFacts().get(SIMPLE_DECISION_TYPE_NAME).isSimple());

        assertEquals(COMPLEX_DECISION_TYPE_NAME, factModelTuple.getVisibleFacts().get(COMPLEX_DECISION_TYPE_NAME).getFactName());
        String hiddenKey = factModelTuple.getVisibleFacts().get(COMPLEX_DECISION_TYPE_NAME).getExpandableProperties().get(COMPLEX_DECISION_TYPE_NAME);
        assertEquals(BASE_TYPE, factModelTuple.getHiddenFacts().get(hiddenKey).getSimpleProperties().get(SIMPLE_DECISION_TYPE_NAME));
    }
}