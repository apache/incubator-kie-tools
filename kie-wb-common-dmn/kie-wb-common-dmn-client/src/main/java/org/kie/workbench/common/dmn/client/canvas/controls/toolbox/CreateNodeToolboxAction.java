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

package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.GeneralCreateNodeAction;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

@Dependent
@DMNFlowActionsToolbox
public class CreateNodeToolboxAction extends org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CreateNodeToolboxAction {

    @Inject
    public CreateNodeToolboxAction(final DefinitionUtils definitionUtils,
                                   final ClientTranslationService translationService,
                                   final @Any @DMNEditor ManagedInstance<GeneralCreateNodeAction> actions) {
        super(definitionUtils,
              translationService,
              actions);
    }
}
