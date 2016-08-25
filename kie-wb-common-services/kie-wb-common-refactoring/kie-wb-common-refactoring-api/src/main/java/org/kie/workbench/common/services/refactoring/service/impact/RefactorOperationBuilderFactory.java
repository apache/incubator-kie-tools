/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.service.impact;

import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

/**
 *
 */
public class RefactorOperationBuilderFactory {


    @SuppressWarnings("unchecked")
    private static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newInstance(OperationType operation) {
        T request;
        switch(operation) {
            case QUERY:
                request = (T) new QueryOperationRequest();
                break;
            case DELETE:
                request = (T) new DeleteOperationRequest();
                break;
            case REFACTOR:
                request = (T) new RefactorOperationRequest();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported request type: " + operation.toString());
        }

        return new RefactorOperationBuilder<>(operation, request);
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newResourceBasedInstance(
            String resourceName, ResourceType type,
            OperationType operation) {

        RefactorOperationBuilder<T> builder = newInstance(operation);
        builder.getQueryTerms().add(new ValueReferenceIndexTerm(resourceName, type));

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newResourceBasedInstance(
            String resourceName, ResourceType type, String newResourceName,
            OperationType operation) {

        RefactorOperationBuilder<T> builder = newInstance(operation);

        // TODO: info for new name??

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newResourceBasedInstance(
            String resourceName, ResourceType type, TermSearchType searchType,
            OperationType operation) {

        RefactorOperationBuilder<T> builder = newInstance(operation);
        builder.getQueryTerms().add(new ValueReferenceIndexTerm(resourceName, type, searchType));

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newSharedPartBasedInstance(
            String sharedPartName, PartType type,
            OperationType operation) {

        // OCRAM: check that part type is a shared-part type

        RefactorOperationBuilder<T> builder = newInstance(operation);
        builder.getQueryTerms().add(new ValueSharedPartIndexTerm(sharedPartName, type));

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newSharedPartBasedInstance(
            String sharedPartName, PartType type, TermSearchType searchType,
            OperationType operation) {

        // OCRAM: check that part type is a shared-part type

        RefactorOperationBuilder<T> builder = newInstance(operation);
        builder.getQueryTerms().add(new ValueSharedPartIndexTerm(sharedPartName, type, searchType));

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newResourcePartBasedInstance(
            String resourceName,
            String partName, PartType partType,
            OperationType operation) {

        // OCRAM: check that part type is NOT a shared-part type

        RefactorOperationBuilder<T> builder = newInstance(operation);
        builder.getQueryTerms().add(new ValuePartReferenceIndexTerm(resourceName, partName, partType));

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newResourcePartBasedInstance(
            String resourceName,
            String partName, PartType partType,
            TermSearchType searchType,
            OperationType operation) {

        // OCRAM: check that part type is NOT a shared-part type

        RefactorOperationBuilder<T> builder = newInstance(operation);
        builder.getQueryTerms().add(new ValuePartReferenceIndexTerm(resourceName, partName, partType, searchType));

        return builder;
    }

    public static <T extends AbstractOperationRequest> RefactorOperationBuilder<T> newResourcePartBasedInstance(
            String resourceName,
            String partName, PartType partType,
            String newPartName,
            OperationType operation) {

        // OCRAM: check that part type is NOT a shared-part type

        RefactorOperationBuilder<T> builder = newInstance(operation);
        // TODO: info for new name??

        return builder;
    }

}