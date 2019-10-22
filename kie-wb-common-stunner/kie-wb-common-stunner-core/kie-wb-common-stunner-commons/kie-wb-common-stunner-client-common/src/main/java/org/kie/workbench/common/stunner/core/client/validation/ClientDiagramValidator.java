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

package org.kie.workbench.common.stunner.core.client.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.tree.TreeWalkTraverseProcessor;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.validation.ModelValidator;
import org.kie.workbench.common.stunner.core.validation.impl.AbstractDiagramValidator;

@ApplicationScoped
public class ClientDiagramValidator extends AbstractDiagramValidator {

    // CDI proxy.
    protected ClientDiagramValidator() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public ClientDiagramValidator(final DefinitionManager definitionManager,
                                  final RuleManager ruleManager,
                                  final TreeWalkTraverseProcessor treeWalkTraverseProcessor,
                                  final ModelValidator modelValidator) {
        super(definitionManager,
              ruleManager,
              treeWalkTraverseProcessor,
              modelValidator);
    }
}