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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;

/**
 * Custom toolbox control for BPMN, which includes additional items.
 */
@Dependent
@BPMN
public class BPMNToolboxControl extends AbstractToolboxControl {

    private final ActionsToolboxFactory flowActionsToolboxFactory;
    private final ActionsToolboxFactory morphActionsToolboxFactory;
    private final ActionsToolboxFactory bpmnCommonActionsToolboxFactory;
    private List<ActionsToolboxFactory> factories;

    // CDI proxy.
    public BPMNToolboxControl() {
        this(null,
             null,
             null);
    }

    @Inject
    public BPMNToolboxControl(final @FlowActionsToolbox ActionsToolboxFactory flowActionsToolboxFactory,
                              final @MorphActionsToolbox ActionsToolboxFactory morphActionsToolboxFactory,
                              final @BPMN ActionsToolboxFactory bpmnCommonActionsToolboxFactory) {
        this.flowActionsToolboxFactory = flowActionsToolboxFactory;
        this.morphActionsToolboxFactory = morphActionsToolboxFactory;
        this.bpmnCommonActionsToolboxFactory = bpmnCommonActionsToolboxFactory;
    }

    @PostConstruct
    public void init() {
        factories = new ArrayList<ActionsToolboxFactory>(2) {{
            add(flowActionsToolboxFactory);
            add(morphActionsToolboxFactory);
            add(bpmnCommonActionsToolboxFactory);
        }};
    }

    @Override
    protected List<ActionsToolboxFactory> getFactories() {
        return factories;
    }
}
