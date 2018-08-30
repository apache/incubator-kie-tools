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

package org.kie.workbench.common.services.backend.compiler.impl.external339;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.codehaus.plexus.classworlds.ClassWorld;

/**
 * Used to open the API of Maven embedder
 * original version: https://maven.apache.org/ref/3.3.9/maven-embedder/xref/org/apache/maven/cli/CliRequest.html
 * IMPORTANT: Preserve the structure for an easy update when the maven version will be updated
 */
public class AFCliRequest {

    private String[] args;
    private CommandLine commandLine;
    private ClassWorld classWorld;
    private String workingDirectory;
    private String multiModuleProjectDirectory;
    private boolean debug;
    private boolean quiet;
    private boolean showErrors = true;
    private Properties userProperties = new Properties();
    private Properties systemProperties = new Properties();
    private MavenExecutionRequest request;
    private Map<String, Object> map;
    private String requestUUID;
    private Properties bannedEnvVars;

    public AFCliRequest(String multiModuleProjectDirectory,
                        String[] args,
                        Map<String, Object> map,
                        String requestUUID,
                        Properties bannedEnvVars) {
        this.multiModuleProjectDirectory = multiModuleProjectDirectory;
        this.workingDirectory = multiModuleProjectDirectory;
        this.request = new DefaultMavenExecutionRequest();
        this.args = args;
        this.map = new HashMap<>(map);
        this.requestUUID = requestUUID;
        this.bannedEnvVars = bannedEnvVars;
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public ClassWorld getClassWorld() {
        return classWorld;
    }

    public void setClassWorld(ClassWorld classWorld) {
        this.classWorld = classWorld;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getMultiModuleProjectDirectory() {
        return multiModuleProjectDirectory;
    }

    public void setMultiModuleProjectDirectory(String multiModuleProjectDirectory) {
        this.multiModuleProjectDirectory = multiModuleProjectDirectory;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isShowErrors() {
        return showErrors;
    }

    public void setShowErrors(boolean showErrors) {
        this.showErrors = showErrors;
    }

    public Properties getUserProperties() {
        return userProperties;
    }

    public Properties getSystemProperties() {
        return systemProperties;
    }

    public MavenExecutionRequest getRequest() {
        return request;
    }

    public Properties getBannedEnvVars(){
        return bannedEnvVars;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AFCliRequest{");
        sb.append("args=").append(Arrays.toString(args));
        sb.append(", commandLine=").append(commandLine);
        sb.append(", classWorld=").append(classWorld);
        sb.append(", workingDirectory='").append(workingDirectory).append('\'');
        sb.append(", multiModuleProjectDirectory='").append(multiModuleProjectDirectory).append('\'');
        sb.append(", debug=").append(debug);
        sb.append(", quiet=").append(quiet);
        sb.append(", showErrors=").append(showErrors);
        sb.append(", bannedEnvVars=").append(bannedEnvVars);
        sb.append(", userProperties=").append(userProperties);
        sb.append(", systemProperties=").append(systemProperties);
        sb.append(", request=").append(request);
        sb.append(", map=").append(map);
        sb.append(", requestUUID='").append(requestUUID).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
