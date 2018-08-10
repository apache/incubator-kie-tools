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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.kie.workbench.common.services.backend.compiler.BaseCompilerTest;
import org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.uberfire.java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultLocalExecutorTest extends BaseCompilerTest {

    public DefaultLocalExecutorTest(){
        super("target/test-classes/kjar-2-single-resources");
        executorService = Executors.newFixedThreadPool(1);
    }
    private ExecutorService executorService;

    @Test
    public void buildNonExistentProject() throws Exception{

        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(tmpRoot,
                                                                             mavenRepo.toString());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildAndSkipDepsNonExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(tmpRoot,
                                                                             mavenRepo.toString(),
                                                                             Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildAndInstallNonExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(tmpRoot, mavenRepo.toString());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildAndInstallSkipDepsNonExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(tmpRoot,
                                                                                       mavenRepo.toString(),
                                                                                       Boolean.FALSE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(Paths.get(tmpRoot.toAbsolutePath()+"/dummy"),
                                                                             mavenRepo.toString());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }



    @Test
    public void buildAndInstallExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath()+"/dummy"),
                                                                                       mavenRepo.toString());
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isNotEmpty();
        assertThat(res.getDependencies().size()).isGreaterThan(0);
    }

    @Test
    public void buildAndInstallSkipDepsExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildAndInstall(Paths.get(tmpRoot.toAbsolutePath()+"/dummy"),
                                                                                       mavenRepo.toString(),
                                                                                       Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
        assertThat(res.getDependencies()).isEmpty();
    }

    @Test
    public void buildSpecializedNonExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildSpecialized(tmpRoot,
                                                                                        mavenRepo.toString(),
                                                                                        new String[]{MavenCLIArgs.COMPILE});
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildSpecializedSkipDepsExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildSpecialized(Paths.get(tmpRoot.toAbsolutePath()+"/dummy"),
                                                                                        mavenRepo.toString(),
                                                                                        new String[]{MavenCLIArgs.COMPILE},
                                                                                        Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }

    @Test
    public void buildSpecializedSkipDepsNonExistentProject() throws Exception{
        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.buildSpecialized(tmpRoot,
                                                                                        mavenRepo.toString(),
                                                                                        new String[]{MavenCLIArgs.COMPILE},
                                                                                        Boolean.TRUE);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }



    @Test
    public void buildWithOverrideNonExistentProject() throws Exception{

        //change some files
        Map<org.uberfire.java.nio.file.Path, InputStream> override = new HashMap<>();

        org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get(tmpRoot+ "/dummy/src/main/java/dummy/DummyOverride.java");
        InputStream input = new FileInputStream(new File("target/test-classes/dummy_override/src/main/java/dummy/DummyOverride.java"));
        override.put(path, input);

        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(tmpRoot,
                                                                             mavenRepo.toString(), override);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isFalse();
    }

    @Test
    public void buildWithOverrideExistentProject() throws Exception{

        //change some files
        Map<org.uberfire.java.nio.file.Path, InputStream> override = new HashMap<>();

        org.uberfire.java.nio.file.Path path = org.uberfire.java.nio.file.Paths.get(tmpRoot+ "/dummy/src/main/java/dummy/DummyOverride.java");
        InputStream input = new FileInputStream(new File("target/test-classes/dummy_override/src/main/java/dummy/DummyOverride.java"));
        override.put(path, input);

        DefaultLocalExecutor executor = new DefaultLocalExecutor(executorService);
        CompletableFuture<KieCompilationResponse> futureRes = executor.build(Paths.get(tmpRoot.toAbsolutePath()+"/dummy"),
                                                                             mavenRepo.toString(),
                                                                             override);
        KieCompilationResponse res = futureRes.get();
        assertThat(res.isSuccessful()).isTrue();
    }
}
