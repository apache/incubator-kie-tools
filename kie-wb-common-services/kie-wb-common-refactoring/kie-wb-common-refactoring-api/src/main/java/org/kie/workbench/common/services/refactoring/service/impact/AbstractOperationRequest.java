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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.uberfire.paging.PageRequest;

/**
 * This class is the input into the the {@link ImpactAnalysisService#getImpactedFiles(RefactorOperation)} operation.
 */
@Portable
public class AbstractOperationRequest extends PageRequest {

    protected List<ValueIndexTerm> queryTerms = new ArrayList<>();

    protected OperationType changeType;

    protected String moduleName = ALL;
    protected String moduleRootPathURI = ALL;
    protected String branchName = ALL;

    // git branch names may not contain ".."
    // module names may not contain a " " (space)
    public static final String ALL = ".. all".intern();

    public AbstractOperationRequest() {
        super(0, 10);
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleRootPathURI() {
        return moduleRootPathURI;
    }

    public void setModuleRootPathURI(String moduleRootPathURI) {
        this.moduleRootPathURI = moduleRootPathURI;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public OperationType getChangeType() {
        return changeType;
    }

    public void setChangeType(OperationType changeType) {
        this.changeType = changeType;
    }

    public List<ValueIndexTerm> getQueryTerms() {
        return this.queryTerms;
    }
}
