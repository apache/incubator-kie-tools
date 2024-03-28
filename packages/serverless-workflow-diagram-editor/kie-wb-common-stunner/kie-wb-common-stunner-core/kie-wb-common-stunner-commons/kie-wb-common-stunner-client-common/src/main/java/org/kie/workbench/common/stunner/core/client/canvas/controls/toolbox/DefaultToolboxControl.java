/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;

@Dependent
@Default
public class DefaultToolboxControl extends AbstractToolboxControl {

    private final ManagedInstance<ActionsToolboxFactory> flowActionsToolboxFactories;
    private final ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories;
    private final ManagedInstance<ActionsToolboxFactory> commonActionsToolboxFactories;

    @Inject
    public DefaultToolboxControl(final @Any @FlowActionsToolbox ManagedInstance<ActionsToolboxFactory> flowActionsToolboxFactories,
                                 final @Any @MorphActionsToolbox ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories,
                                 final @Any @CommonActionsToolbox ManagedInstance<ActionsToolboxFactory> commonActionsToolboxFactories) {
        this.flowActionsToolboxFactories = flowActionsToolboxFactories;
        this.morphActionsToolboxFactories = morphActionsToolboxFactories;
        this.commonActionsToolboxFactories = commonActionsToolboxFactories;
    }

    DefaultToolboxControl(final @Any @FlowActionsToolbox ManagedInstance<ActionsToolboxFactory> flowActionsToolboxFactories,
                          final @Any @MorphActionsToolbox ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories,
                          final @Any @CommonActionsToolbox ManagedInstance<ActionsToolboxFactory> commonActionsToolboxFactories,
                          final ToolboxControlImpl<ActionsToolboxFactory> toolboxControl) {

        super(toolboxControl);
        this.flowActionsToolboxFactories = flowActionsToolboxFactories;
        this.morphActionsToolboxFactories = morphActionsToolboxFactories;
        this.commonActionsToolboxFactories = commonActionsToolboxFactories;
    }

    @Override
    protected List<ActionsToolboxFactory> getFactories() {
        return Arrays.asList(flowActionsToolboxFactories.get(),
                             morphActionsToolboxFactories.get(),
                             commonActionsToolboxFactories.get());
    }

    @PreDestroy
    @Override
    public void destroy() {
        super.destroy();
        flowActionsToolboxFactories.destroyAll();
        morphActionsToolboxFactories.destroyAll();
        commonActionsToolboxFactories.destroyAll();
    }
}
