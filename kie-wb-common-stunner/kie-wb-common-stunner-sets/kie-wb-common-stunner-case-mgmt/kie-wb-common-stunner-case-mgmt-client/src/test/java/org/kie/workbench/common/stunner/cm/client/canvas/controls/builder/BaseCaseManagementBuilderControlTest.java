/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.canvas.controls.builder;

import java.util.Optional;

import org.junit.Test;
import org.kie.workbench.common.stunner.cm.client.canvas.CaseManagementCanvasHandler;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.builder.impl.AbstractElementBuilderControl;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.graph.processing.index.bounds.GraphBoundsIndexer;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.mockito.Mock;

import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class BaseCaseManagementBuilderControlTest {

    @Mock
    protected ClientDefinitionManager clientDefinitionManager;

    @Mock
    protected ClientFactoryService clientFactoryServices;

    @Mock
    protected GraphUtils graphUtils;

    @Mock
    protected RuleManager ruleManager;

    @Mock
    protected CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    protected GraphBoundsIndexer graphBoundsIndexer;

    @Mock
    protected CanvasLayoutUtils canvasLayoutUtils;

    @Mock
    protected CaseManagementCanvasHandler canvasHandler;

    protected AbstractElementBuilderControl control;

    protected void setup() {
        this.control = getBuilderControl();
        this.control.enable(canvasHandler);

        when(canvasHandler.getElementAt(anyDouble(),
                                        anyDouble())).thenReturn(Optional.empty());
    }

    protected abstract AbstractElementBuilderControl getBuilderControl();

    @Test
    public void checkGetParentUseCanvas() {
        control.getParent(10.0,
                          20.0);

        verify(canvasHandler,
               times(1)).getElementAt(eq(10.0,
                                         0.0),
                                      eq(20.0,
                                         0.0));
    }
}
