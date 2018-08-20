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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.guvnor.common.services.backend.cache.LRUCache;
import org.kie.workbench.common.services.backend.compiler.AFCompiler;
import org.kie.workbench.common.services.backend.compiler.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.BaseMavenCompiler;
import org.kie.workbench.common.services.backend.compiler.impl.DefaultCompilationRequest;
import org.kie.workbench.common.services.backend.compiler.impl.WorkspaceCompilationInfo;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.ClasspathDepsAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.KieAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.decorators.OutputLogAfterDecorator;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Paths;

/**
 * Implementation for a local build requested by a remote execution
 */
public class DefaultRemoteExecutor implements RemoteExecutor {

    private ExecutorService executor;
    private LRUCache<String, CompilerAggregateEntryCache> compilerCacheForRemoteInvocation;

    public DefaultRemoteExecutor(ExecutorService executorService) {
        executor = executorService;
        compilerCacheForRemoteInvocation = new LRUCache<String, CompilerAggregateEntryCache>() {
        };
    }

    private AFCompiler getCompiler(String projectPath) {
        CompilerAggregateEntryCache info = compilerCacheForRemoteInvocation.getEntry(projectPath);
        if (info != null && info.getCompiler() != null) {
            return info.getCompiler();
        } else {
            return getNewCachedAFCompiler(projectPath);
        }
    }

    private AFCompiler getNewCachedAFCompiler(String projectPath) {
        CompilerAggregateEntryCache info = setupCompileInfo(projectPath);
        compilerCacheForRemoteInvocation.setEntry(projectPath, info);
        return info.getCompiler();
    }

    private CompilerAggregateEntryCache setupCompileInfo(String workingDir) {
        AFCompiler compiler = new KieAfterDecorator(new OutputLogAfterDecorator(new ClasspathDepsAfterDecorator(new BaseMavenCompiler())));
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(workingDir));
        return new CompilerAggregateEntryCache(compiler, info);
    }

    private CompletableFuture<KieCompilationResponse> internalBuild(String projectPath, String mavenRepo,
                                                                    boolean skipProjectDepCreation, String goal) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        AFCompiler compiler = getCompiler(projectPath);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               new String[]{goal},
                                                               skipProjectDepCreation);
        return runInItsOwnThread(compiler, req);
    }

    private CompletableFuture<KieCompilationResponse> internalBuild(String projectPath, String mavenRepo,
                                                                    boolean skipProjectDepCreation, String[] args) {
        WorkspaceCompilationInfo info = new WorkspaceCompilationInfo(Paths.get(projectPath));
        AFCompiler compiler = getCompiler(projectPath);
        CompilationRequest req = new DefaultCompilationRequest(mavenRepo,
                                                               info,
                                                               args,
                                                               skipProjectDepCreation);

        return runInItsOwnThread(compiler, req);
    }

    private CompletableFuture<KieCompilationResponse> runInItsOwnThread(AFCompiler compiler, CompilationRequest req) {
        return CompletableFuture.supplyAsync(() -> ((KieCompilationResponse) compiler.compile(req)), executor);
    }

    /************************************ Suitable for the REST Builds ************************************************/

    @Override
    public CompletableFuture<KieCompilationResponse> build(String projectPath, String mavenRepo) {
        return internalBuild(projectPath, mavenRepo, Boolean.FALSE, MavenCLIArgs.COMPILE);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> build(String projectPath, String mavenRepo, Boolean skipPrjDependenciesCreationList) {
        return internalBuild(projectPath, mavenRepo, skipPrjDependenciesCreationList, MavenCLIArgs.COMPILE);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildAndInstall(String projectPath, String mavenRepo) {
        return internalBuild(projectPath, mavenRepo, Boolean.FALSE, MavenCLIArgs.INSTALL);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildAndInstall(String projectPath, String mavenRepo,
                                                                     Boolean skipPrjDependenciesCreationList) {
        return internalBuild(projectPath, mavenRepo, skipPrjDependenciesCreationList, MavenCLIArgs.INSTALL);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildSpecialized(String projectPath, String mavenRepo, String[] args) {
        return internalBuild(projectPath, mavenRepo, Boolean.FALSE, args);
    }

    @Override
    public CompletableFuture<KieCompilationResponse> buildSpecialized(String projectPath, String mavenRepo, String[] args,
                                                                      Boolean skipPrjDependenciesCreationList) {
        return internalBuild(projectPath, mavenRepo, skipPrjDependenciesCreationList, args);
    }
}