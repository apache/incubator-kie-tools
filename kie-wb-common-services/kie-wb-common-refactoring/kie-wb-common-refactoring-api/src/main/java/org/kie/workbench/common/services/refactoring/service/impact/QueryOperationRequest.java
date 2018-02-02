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
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

/**
 * This class is the input into the the {@link ImpactAnalysisService#getImpactedFiles(RefactorOperation)} operation.
 */
@Portable
public class QueryOperationRequest extends AbstractOperationRequest {

    public QueryOperationRequest() {
        super();
    }

    // Class builders

    public static RefactorOperationBuilder<QueryOperationRequest>.PossiblyRequiresPart references(
            String fullyQualifiedResourceName,
            ResourceType type) {

        RefactorOperationBuilder<QueryOperationRequest> builder = RefactorOperationBuilderFactory.newResourceBasedInstance(
                fullyQualifiedResourceName, type, OperationType.QUERY);
        return builder.possiblyRequiresPart(builder);
    }

    public static RefactorOperationBuilder<QueryOperationRequest>.PossiblyRequiresPart references(
            String fullyQualifiedResourceName,
            ResourceType type,
            TermSearchType searchType) {

        RefactorOperationBuilder<QueryOperationRequest> builder = RefactorOperationBuilderFactory.newResourceBasedInstance(
                fullyQualifiedResourceName, type, OperationType.QUERY);
        return builder.possiblyRequiresPart(builder);
    }

    public static RefactorOperationBuilder<QueryOperationRequest>.RequiresModule referencesSharedPart(
            String fullyQualifiedPartName,
            PartType partType) {

        RefactorOperationBuilder<QueryOperationRequest> builder = RefactorOperationBuilderFactory.newSharedPartBasedInstance(
                fullyQualifiedPartName, partType, OperationType.QUERY);
        return builder.requiresModule(builder);
    }

    public static RefactorOperationBuilder<QueryOperationRequest>.RequiresModule referencesSharedPart(
            String fullyQualifiedPartName,
            PartType partType,
            TermSearchType searchType) {

        RefactorOperationBuilder<QueryOperationRequest> builder = RefactorOperationBuilderFactory.newSharedPartBasedInstance(
                fullyQualifiedPartName, partType, searchType, OperationType.QUERY);
        return builder.requiresModule(builder);
    }

    public static RefactorOperationBuilder<QueryOperationRequest>.RequiresModule referencesPart(
            String fullyQualifiedResourceName,
            String fullyQualifiedPartName,
            PartType partType) {

        RefactorOperationBuilder<QueryOperationRequest> builder = RefactorOperationBuilderFactory.newResourcePartBasedInstance(
                fullyQualifiedResourceName, fullyQualifiedPartName, partType, OperationType.QUERY);
        return builder.requiresModule(builder);
    }

    public static RefactorOperationBuilder<QueryOperationRequest>.RequiresModule referencesPart(
            String fullyQualifiedResourceName,
            String fullyQualifiedPartName,
            PartType partType,
            TermSearchType searchType) {

        RefactorOperationBuilder<QueryOperationRequest> builder = RefactorOperationBuilderFactory.newResourcePartBasedInstance(
                fullyQualifiedResourceName, fullyQualifiedPartName, partType, searchType, OperationType.QUERY);
        return builder.requiresModule(builder);
    }
}
