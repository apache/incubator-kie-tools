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
import java.util.concurrent.Executors;

import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultRemoteExecutorTest extends BaseCompilerTest {

    public DefaultRemoteExecutorTest(){
        super("target/test-classes/kjar-2-single-resources");
        executorService = Executors.newFixedThreadPool(1);
    }
    private ExecutorService executorService;

    @Test
    public void buildNonExistentProject() throws Exception{

        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(tmpRoot.toAbsolutePath().toString(),
                                                                             mavenRepo);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildAndSkipDepsNonExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(tmpRoot.toAbsolutePath().toString(),
                                                                             mavenRepo,
                                                                             Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildAndInstallNonExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(tmpRoot.toAbsolutePath().toString(), mavenRepo);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildAndInstallSkipDepsNonExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(tmpRoot.toAbsolutePath().toString(),
                                                                                       mavenRepo,
                                                                                       Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(Paths.get(tmpRoot.toAbsolutePath()+"/dummy").toAbsolutePath().toString(),
                                                                             mavenRepo);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }



    @Test
    public void buildAndInstallExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath()+"/dummy").toAbsolutePath().toString(),
                                                                                       mavenRepo);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isNotEmpty();
        assertThat(res.getDependencies().size()).isGreaterThan(0);
    }

    @Test
    public void buildAndInstallSkipDepsExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath()+"/dummy").toAbsolutePath().toString(),
                                                                                       mavenRepo,
                                                                                       Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildSpecializedNonExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildSpecialized(tmpRoot.toAbsolutePath().toString(),
                                                                                        mavenRepo,
                                                                                        new String[]{MavenCLIArgs.COMPILE});
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildSpecializedSkipDepsExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildSpecialized(Paths.get(tmpRoot.toAbsolutePath()+"/dummy").toAbsolutePath().toString(),
                                                                                        mavenRepo,
                                                                                        new String[]{MavenCLIArgs.COMPILE},
                                                                                        Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void buildSpecializedSkipDepsNonExistentProject() throws Exception{
        DefaultRemoteExecutor executor = new DefaultRemoteExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildSpecialized(tmpRoot.toAbsolutePath().toString(),
                                                                                        mavenRepo,
                                                                                        new String[]{MavenCLIArgs.COMPILE},
                                                                                        Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

}
