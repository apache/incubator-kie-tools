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
package org.kie.workbench.common.services.backend.compiler.rest.client;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;

import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;

/**
 * Async Client code to ask a build onto a remote machine
 */
public class MavenRestClient {

    public CompletableFuture<KieCompilationResponse> call(String projectPath, String mavenRepoPath, String settingsXmlPath, String url) {

        final CompletableFuture<KieCompilationResponse> cfInternal = new CompletableFuture<>();

        Future<?> future = ClientBuilder.newBuilder().build()
                .target(url)
                .request().header("project", projectPath).header("mavenrepo", mavenRepoPath).header("settings_xml",settingsXmlPath)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .async()
                .post(null, new InvocationCallback<KieCompilationResponse>() {
                    @Override
                    public void completed(final KieCompilationResponse result) {
                        cfInternal.complete(result);
                    }

                    @Override
                    public void failed(final Throwable throwable) {
                        cfInternal.completeExceptionally(throwable);
                    }
                });

        return cfInternal.whenComplete((result, exception) -> {
            if (CancellationException.class.isInstance(exception)) {
                future.cancel(true);
            }
        });
    }
}
