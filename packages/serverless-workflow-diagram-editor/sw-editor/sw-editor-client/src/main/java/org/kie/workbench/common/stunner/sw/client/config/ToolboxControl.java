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


package org.kie.workbench.common.stunner.sw.client.config;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GroupActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GroupActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;
import org.kie.workbench.common.stunner.sw.SWEditor;

@Dependent
@SWEditor
public class ToolboxControl extends AbstractToolboxControl {

    private final ManagedInstance<GroupActionsToolboxFactory> groupActionsToolboxFactories;
    private final ManagedInstance<ActionsToolboxFactory> commonActionsToolboxFactories;
    private final ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories;

    @Inject
    public ToolboxControl(final @Any @GroupActionsToolbox ManagedInstance<GroupActionsToolboxFactory> groupActionsToolboxFactories,
                          final @Any @MorphActionsToolbox ManagedInstance<ActionsToolboxFactory> morphActionsToolboxFactories,
                          final @Any @CommonActionsToolbox ManagedInstance<ActionsToolboxFactory> commonActionsToolboxFactories) {
        this.groupActionsToolboxFactories = groupActionsToolboxFactories;
        this.morphActionsToolboxFactories = morphActionsToolboxFactories;
        this.commonActionsToolboxFactories = commonActionsToolboxFactories;
    }

    @Override
    protected List<ActionsToolboxFactory> getFactories() {
        return Arrays.asList(groupActionsToolboxFactories.get(),
                             morphActionsToolboxFactories.get(),
                             commonActionsToolboxFactories.get());
    }

    @PreDestroy
    @Override
    public void destroy() {
        super.destroy();
        groupActionsToolboxFactories.destroyAll();
        morphActionsToolboxFactories.destroyAll();
        commonActionsToolboxFactories.destroyAll();
    }
}
