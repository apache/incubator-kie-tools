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

package org.kie.workbench.common.stunner.cm.backend.converters.fromstunner;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsBuildingContext;
import org.kie.workbench.common.stunner.cm.backend.converters.fromstunner.properties.CaseManagementPropertyWriterFactory;
import org.kie.workbench.common.stunner.cm.definition.BaseCaseManagementReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.graph.impl.GraphImpl;
import org.kie.workbench.common.stunner.core.graph.store.GraphNodeStoreImpl;

import static org.junit.Assert.assertEquals;

public class CaseManagementFlowElementConverterTest {

    private CaseManagementFlowElementConverter tested;

    @Before
    public void setUp() throws Exception {
        CaseManagementPropertyWriterFactory factory = new CaseManagementPropertyWriterFactory();

        CaseManagementConverterFactory converterFactory = new CaseManagementConverterFactory(
                new DefinitionsBuildingContext(new GraphImpl("x", new GraphNodeStoreImpl()),
                                               CaseManagementDiagram.class),
                factory);

        tested = new CaseManagementFlowElementConverter(converterFactory);
    }

    @Test
    public void testGetReusableSubprocessClass() throws Exception {
        assertEquals(tested.getReusableSubprocessClass(), BaseCaseManagementReusableSubprocess.class);
    }
}