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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;

@Dependent
@DMNEditor
public class DMNToolboxControl extends AbstractToolboxControl {

    private final ActionsToolboxFactory flowActionsToolboxFactory;
    private final ActionsToolboxFactory commonActionsToolboxFactory;
    private List<ActionsToolboxFactory> factories;

    @Inject
    public DMNToolboxControl(final @DMNFlowActionsToolbox ActionsToolboxFactory flowActionsToolboxFactory,
                             final @DMNCommonActionsToolbox ActionsToolboxFactory commonActionsToolboxFactory) {
        this.flowActionsToolboxFactory = flowActionsToolboxFactory;
        this.commonActionsToolboxFactory = commonActionsToolboxFactory;
    }

    @PostConstruct
    public void init() {
        factories = new ArrayList<ActionsToolboxFactory>(2) {{
            add(flowActionsToolboxFactory);
            add(commonActionsToolboxFactory);
        }};
    }

    @Override
    protected List<ActionsToolboxFactory> getFactories() {
        return factories;
    }
}
