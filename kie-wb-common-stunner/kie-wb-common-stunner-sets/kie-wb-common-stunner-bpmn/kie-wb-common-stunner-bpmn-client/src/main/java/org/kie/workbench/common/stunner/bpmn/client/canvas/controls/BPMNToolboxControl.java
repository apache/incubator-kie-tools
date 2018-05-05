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

import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
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

    private final ManagedInstance<ActionsToolboxFactory> flowActionsToolboxFactories;
    private final ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories;
    private final ManagedInstance<ActionsToolboxFactory> bpmnCommonActionsToolboxFactories;

    @Inject
    public BPMNToolboxControl(final @Any @FlowActionsToolbox ManagedInstance<ActionsToolboxFactory> flowActionsToolboxFactories,
                              final @Any @MorphActionsToolbox ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories,
                              final @Any @BPMN ManagedInstance<ActionsToolboxFactory> bpmnCommonActionsToolboxFactories) {
        this.flowActionsToolboxFactories = flowActionsToolboxFactories;
        this.morphActionsToolboxFactories = morphActionsToolboxFactories;
        this.bpmnCommonActionsToolboxFactories = bpmnCommonActionsToolboxFactories;
    }

    @Override
    protected List<ActionsToolboxFactory> getFactories() {
        return Arrays.asList(flowActionsToolboxFactories.get(),
                             morphActionsToolboxFactories.get(),
                             bpmnCommonActionsToolboxFactories.get());
    }

    @PreDestroy
    @Override
    public void destroy() {
        super.destroy();
        flowActionsToolboxFactories.destroyAll();
        morphActionsToolboxFactories.destroyAll();
        bpmnCommonActionsToolboxFactories.destroyAll();
    }
}
