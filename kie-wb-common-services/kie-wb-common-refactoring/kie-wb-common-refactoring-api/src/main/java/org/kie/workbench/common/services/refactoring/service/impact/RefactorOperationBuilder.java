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

import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

public class RefactorOperationBuilder<R extends AbstractOperationRequest> extends AbstractOperationRequest {

    private R request;

    RefactorOperationBuilder(final OperationType operation,
                             final R request) {
        this.changeType = operation;
        this.request = request;
    }

    private R internalBuild() {
        this.request.setChangeType(changeType);

        // OCRAM: clone?
        this.request.queryTerms = this.queryTerms;

        this.request.setStartRowIndex(startRowIndex);
        this.request.setPageSize(pageSize);

        this.request.setModuleName(moduleName);
        this.request.setModuleRootPathURI(moduleRootPathURI);
        this.request.setBranchName(branchName);

        return this.request;
    }

    RefactorOperationBuilder<R>.RequiresPart requiresPart(RefactorOperationBuilder<R> builder) {
        return new RequiresPart(builder);
    }

    RefactorOperationBuilder<R>.PossiblyRequiresPart possiblyRequiresPart(RefactorOperationBuilder<R> builder) {
        return new PossiblyRequiresPart(builder);
    }

    RequiresModule requiresModule(RefactorOperationBuilder<R> builder) {
        return new RequiresModule(builder);
    }

    public class RequiresPart extends Requires {

        RequiresPart(RefactorOperationBuilder<R> delegate) {
            super(delegate);
        }

        public RequiresModule referencesPart(String resourceFQN,
                                             ResourceType resourceType,
                                             String partName,
                                             PartType partType) {
            this.delegate.queryTerms.add(new ValuePartReferenceIndexTerm(resourceFQN,
                                                                         partName,
                                                                         partType));
            return new RequiresModule(delegate);
        }

        public RequiresPart pageSize(int pageSize) {
            this.delegate.setPageSize(pageSize);
            return this;
        }

        public RequiresPart startRowIndex(int startRowIndex) {
            this.delegate.setStartRowIndex(startRowIndex);
            return this;
        }
    }

    public class PossiblyRequiresPart extends RequiresModule {

        PossiblyRequiresPart(RefactorOperationBuilder<R> delegate) {
            super(delegate);
        }

        public RequiresModule referencesSharedPart(String resourceFQN,
                                                   String partName,
                                                   PartType partType) {
            this.delegate.queryTerms.add(new ValuePartReferenceIndexTerm(resourceFQN,
                                                                         partName,
                                                                         partType));
            return new RequiresModule(delegate);
        }

        public RequiresModule referencesPart(String resourceFQN,
                                             String partName,
                                             PartType partType,
                                             TermSearchType searchType) {
            this.delegate.queryTerms.add(new ValuePartReferenceIndexTerm(resourceFQN,
                                                                         partName,
                                                                         partType,
                                                                         searchType));
            return new RequiresModule(delegate);
        }

        public PossiblyRequiresPart pageSize(int pageSize) {
            this.delegate.setPageSize(pageSize);
            return this;
        }

        public PossiblyRequiresPart startRowIndex(int startRowIndex) {
            this.delegate.setStartRowIndex(startRowIndex);
            return this;
        }
    }

    abstract class Requires<N extends Requires> {

        protected final RefactorOperationBuilder<R> delegate;

        Requires(RefactorOperationBuilder<R> delegate) {
            this.delegate = delegate;
        }

        RefactorOperationBuilder getDelegate() {
            return delegate;
        }
    }

    public class RequiresModule extends Requires<RequiresModule> {

        RequiresModule(RefactorOperationBuilder<R> delegate) {
            super(delegate);
        }

        public RequiresBranch inModule(String moduleName) {
            this.delegate.moduleName = moduleName;
            return new RequiresBranch(delegate);
        }

        public RequiresBranch inModuleRootPathURI(String moduleRootPathURI) {
            this.delegate.moduleRootPathURI = moduleRootPathURI;
            return new RequiresBranch(delegate);
        }

        public RequiresBranch inAllModules() {
            this.delegate.moduleName = ALL;
            this.delegate.moduleRootPathURI = ALL;
            return new RequiresBranch(delegate);
        }
    }

    public class RequiresBranch extends Requires<RequiresBranch> {

        RequiresBranch(RefactorOperationBuilder<R> delegate) {
            super(delegate);
        }

        public R onBranch(String branchName) {
            this.delegate.branchName = branchName;
            return this.delegate.internalBuild();
        }

        public R onAllBranches() {
            this.delegate.branchName = ALL;
            return this.delegate.internalBuild();
        }
    }
}
