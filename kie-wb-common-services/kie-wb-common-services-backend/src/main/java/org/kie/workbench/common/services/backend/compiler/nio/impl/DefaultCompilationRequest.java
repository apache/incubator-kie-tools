/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.nio.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.kie.workbench.common.services.backend.compiler.external339.AFCliRequest;
import org.kie.workbench.common.services.backend.compiler.nio.CompilationRequest;
import org.kie.workbench.common.services.backend.compiler.nio.WorkspaceCompilationInfo;
import org.uberfire.java.nio.file.Path;

/***
 * Implementation of CompilationRequest, holds the information for the AFMavenCli
 */
public class DefaultCompilationRequest implements CompilationRequest {

    private AFCliRequest req;
    private WorkspaceCompilationInfo info;
    private String requestUUID;
    private String[] originalArgs;
    private Map<String, Object> map;
    private String mavenRepo;
    private Boolean logRequested;

    /***
     * @param mavenRepo a string representation of the Path
     * @param info
     * @param args param for maven, can be used {@link org.kie.workbench.common.services.backend.compiler.configuration.MavenCLIArgs}
     * @param map to retrieve KieMetaInfo and KieModule when a Kie Plugin is present
     * @param logRequested if is true the output of the build will be provided as a List<String>
     */
    public DefaultCompilationRequest(String mavenRepo,
                                     WorkspaceCompilationInfo info,
                                     String[] args,
                                     Map<String, Object> map,
                                     Boolean logRequested) {
        this.mavenRepo = mavenRepo;
        this.info = info;
        this.requestUUID = UUID.randomUUID().toString();
        this.map = map;

        this.originalArgs = args;
        this.logRequested = logRequested;
        String[] internalArgs = getInternalArgs(args,
                                                logRequested);
        this.req = new AFCliRequest(this.info.getPrjPath().toAbsolutePath().toString(),
                                    internalArgs,
                                    this.map,
                                    this.requestUUID,
                                    logRequested);
    }

    private String[] getInternalArgs(String[] args,
                                     Boolean logRequested) {
        String[] internalArgs;
        StringBuilder sbCompilationID = new StringBuilder().append("-Dcompilation.ID=").append(requestUUID);

        if (logRequested) {
            StringBuilder sbLogID = new StringBuilder().append("-l ").append("log").append(".").append(requestUUID).append(".log");
            internalArgs = Arrays.copyOf(args,
                                         args.length + 2);
            internalArgs[args.length + 1] = sbLogID.toString();
        } else {
            internalArgs = Arrays.copyOf(args,
                                         args.length + 1);
        }

        internalArgs[args.length] = sbCompilationID.toString();
        return internalArgs;
    }

    @Override
    public String getRequestUUID() {
        return requestUUID;
    }

    @Override
    public WorkspaceCompilationInfo getInfo() {
        return info;
    }

    @Override
    public Optional<Path> getPomFile() {
        return info.getEnhancedMainPomFile();
    }

    public AFCliRequest getReq() {
        return req;
    }

    @Override
    public AFCliRequest getKieCliRequest() {
        return req;
    }

    @Override
    public String getMavenRepo() {
        return mavenRepo;
    }

    @Override
    public String[] getOriginalArgs() {
        return originalArgs;
    }

    @Override
    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public Boolean getLogRequested() {
        return logRequested;
    }
}
