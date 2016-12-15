/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.impl.model;

import org.kie.workbench.common.stunner.core.rule.impl.AbstractRulesManager;
import org.kie.workbench.common.stunner.core.rule.model.*;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class ModelRulesManagerImpl extends AbstractRulesManager<ModelContainmentRuleManager, ModelConnectionRuleManager,
        ModelCardinalityRuleManager, ModelEdgeCardinalityRuleManager, ModelDockingRuleManager> implements ModelRulesManager {

    private static final String NAME = "Domain Model Rules Manager";

    @Inject
    public ModelRulesManagerImpl( final ModelContainmentRuleManager containmentRuleManager,
                                  final ModelConnectionRuleManager connectionRuleManager,
                                  final ModelCardinalityRuleManager cardinalityRuleManager,
                                  final ModelEdgeCardinalityRuleManager edgeCardinalityRuleManager,
                                  final ModelDockingRuleManager dockingRuleManager ) {
        super( containmentRuleManager, connectionRuleManager, cardinalityRuleManager,
                edgeCardinalityRuleManager, dockingRuleManager );
    }

    @Override
    public String getName() {
        return NAME;
    }

}
