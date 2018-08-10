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
package org.kie.workbench.common.services.backend.compiler.rest.server;

import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kie.workbench.common.services.backend.compiler.impl.DefaultHttpCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.impl.kie.KieCompilationResponse;
import org.kie.workbench.common.services.backend.compiler.rest.RestUtils;
import org.kie.workbench.common.services.backend.compiler.service.AFCompilerService;
import org.kie.workbench.common.services.backend.compiler.service.DefaultKieCompilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rest endpoint to ask an async remote compilation
 */
@Path("/build/maven/")
@RequestScoped
public class MavenRestHandler extends Application {

    private static Logger logger = LoggerFactory.getLogger(MavenRestHandler.class);

    private static String maven = "Apache Maven";

    private AFCompilerService compilerService;

    public MavenRestHandler() {
        compilerService = new DefaultKieCompilerService();
    }

    /**
     * Endpoint to know the version of the available Maven
     */
    @GET
    @Produces("text/plain")
    public String get() {
        return maven;
    }

    /**
     * Endpoint to ask an async build
     */
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public void postAsync(@Suspended AsyncResponse ar, @HeaderParam("project") String projectRepo, @HeaderParam("mavenrepo") String mavenRepo) throws Exception {
        CompletableFuture<KieCompilationResponse> response = compilerService.build(projectRepo, mavenRepo);
        response.whenCompleteAsync((kieCompilationResponse, throwable) -> {
            if (throwable != null) {
                logger.error(throwable.getMessage());
                ar.resume(Response.serverError().build());
            } else {
                byte[] bytes = RestUtils.serialize(new DefaultHttpCompilationResponse(kieCompilationResponse));
                ar.resume(Response.ok(bytes).build());
            }
        });
    }
}
