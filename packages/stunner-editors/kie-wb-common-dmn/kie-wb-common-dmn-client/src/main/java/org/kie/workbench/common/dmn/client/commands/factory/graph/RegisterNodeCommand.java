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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.function.KindUtilities;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class RegisterNodeCommand extends org.kie.workbench.common.stunner.core.graph.command.impl.RegisterNodeCommand {

    public RegisterNodeCommand(final Node candidate) {
        super(candidate);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = super.execute(context);
        if (!CommandUtils.isError(results)) {
            final Node<?, Edge> candidate = getCandidate();
            if (candidate.getContent() instanceof View) {
                final DMNModelInstrumentedBase dmnModel = (DMNModelInstrumentedBase) ((View) candidate.getContent()).getDefinition();

                if (dmnModel instanceof BusinessKnowledgeModel) {
                    setupEncapsulatedLogicIfNodeIsNew((BusinessKnowledgeModel) dmnModel);
                }
            }
        }
        return results;
    }

    private void setupEncapsulatedLogicIfNodeIsNew(final BusinessKnowledgeModel businessKnowledgeModel) {
        final boolean isNewNode = businessKnowledgeModel.getEncapsulatedLogic() == null;
        if (isNewNode) {
            setupEncapsulatedLogic(businessKnowledgeModel);
        }
    }

    private void setupEncapsulatedLogic(final BusinessKnowledgeModel businessKnowledgeModel) {

        final LiteralExpression le = new LiteralExpression();
        final FunctionDefinition function = new FunctionDefinition();

        KindUtilities.setKind(function, FunctionDefinition.Kind.FEEL);
        function.setExpression(le);
        le.setParent(function);
        businessKnowledgeModel.setEncapsulatedLogic(function);
        function.setParent(businessKnowledgeModel);
    }
}
