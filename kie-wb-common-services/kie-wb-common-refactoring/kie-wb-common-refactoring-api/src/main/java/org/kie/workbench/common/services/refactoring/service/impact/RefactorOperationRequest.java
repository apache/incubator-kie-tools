/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.service.impact;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

/**
 * This class is the input into the the {@link ImpactAnalysisService#getImpactedFiles(RefactorOperation)} operation.
 */
@Portable
public class RefactorOperationRequest extends AbstractOperationRequest {

    public RefactorOperationRequest() {
        super();
    }

    public static RefactorOperationBuilder<RefactorOperationRequest>.RequiresModule refactorReferences(
            String fullyQualifiedResourceName, ResourceType type, String newFQN) {

        RefactorOperationBuilder<RefactorOperationRequest> builder = RefactorOperationBuilderFactory.newResourceBasedInstance(
                fullyQualifiedResourceName, type, OperationType.REFACTOR);
        return builder.requiresModule(builder);
    }

    public static RefactorOperationBuilder<RefactorOperationRequest>.RequiresModule refactorPartReferences(
            String fullyQualifiedResourceName,
            String partName,
            PartType partType,
            String partNewName) {

        RefactorOperationBuilder<RefactorOperationRequest> builder = RefactorOperationBuilderFactory.newResourcePartBasedInstance(
                fullyQualifiedResourceName,
                partName, partType,
                partNewName,
                OperationType.REFACTOR);
        return builder.requiresModule(builder);
    }
}
