/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.service.executors;

import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;

/**
 * Used to store in a cache related objects
 */
class CompilerAggregateEntryCache {

    private AFCompiler compiler;
    private WorkspaceCompilationInfo info;

    public CompilerAggregateEntryCache(AFCompiler compiler,
                                       WorkspaceCompilationInfo info) {
        this.compiler = compiler;
        this.info = info;
    }

    public AFCompiler getCompiler() {
        return compiler;
    }

    public WorkspaceCompilationInfo getInfo() {
        return info;
    }

    public void setInfo(WorkspaceCompilationInfo info) {
        this.info = info;
    }
}
