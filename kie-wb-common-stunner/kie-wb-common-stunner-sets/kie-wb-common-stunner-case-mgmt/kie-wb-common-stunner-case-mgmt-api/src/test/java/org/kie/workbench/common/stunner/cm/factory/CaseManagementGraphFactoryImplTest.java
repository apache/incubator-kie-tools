/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementGraphFactoryImplTest {

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private GraphCommandManager graphCommandManager;

    @Mock
    private GraphCommandFactory graphCommandFactory;

    @Mock
    private RuleManager ruleManager;

    @Mock
    private GraphIndexBuilder<?> indexBuilder;

    private CaseManagementGraphFactoryImpl factory;

    @Before
    public void setup() {
        this.factory = new CaseManagementGraphFactoryImpl(definitionManager,
                                                          factoryManager,
                                                          ruleManager,
                                                          graphCommandManager,
                                                          graphCommandFactory,
                                                          indexBuilder);
    }

    @Test
    public void assertFactoryType() {
        // It is important that CaseManagementGraphFactoryImpl declares it relates to the CaseManagementGraphFactory
        // otherwise all sorts of things break. This test attempts to drawer the importance of this to future changes
        // should someone decide to change the apparent innocuous method in CaseManagementGraphFactoryImpl.
        assertEquals(CaseManagementGraphFactory.class,
                     factory.getFactoryType());
    }
}
