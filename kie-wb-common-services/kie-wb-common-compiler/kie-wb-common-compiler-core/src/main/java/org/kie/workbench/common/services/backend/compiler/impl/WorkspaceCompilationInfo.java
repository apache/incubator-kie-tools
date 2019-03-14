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
package org.kie.workbench.common.services.backend.compiler.impl;

import java.util.Properties;

import org.kie.workbench.common.services.backend.compiler.configuration.MavenBannedVars;
import org.uberfire.java.nio.file.Path;

/***
 * Holds informations shared with Kie compilers and the Compilation Request/Response
 * between builds
 */
public class WorkspaceCompilationInfo {

    protected Path prjPath;
    protected Boolean kiePluginPresent = Boolean.FALSE;
    protected Properties bannedEnvVars;

    public WorkspaceCompilationInfo(Path prjPath) {
        this.prjPath = prjPath;
        bannedEnvVars = MavenBannedVars.getBannedProperties();
    }

    public WorkspaceCompilationInfo(Path prjPath, Properties bannedEnvVars) {
        this.prjPath = prjPath;
        this.bannedEnvVars = bannedEnvVars;
    }

    public Boolean lateAdditionKiePluginPresent(Boolean present) {
        if ((kiePluginPresent == null && present != null)) {
            this.kiePluginPresent = present;
            return Boolean.TRUE;
        }
        if (present != null) {
            kiePluginPresent = kiePluginPresent || present;
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean isKiePluginPresent() {
        return kiePluginPresent;
    }

    public Path getPrjPath() {
        return prjPath;
    }

    public Properties getBennedEnvVars(){
        return bannedEnvVars;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorkspaceCompilationInfo{");
        sb.append("prjPath=").append(prjPath);
        sb.append(", bannedEnvVars=").append(bannedEnvVars);
        sb.append(", kiePluginPresent=").append(kiePluginPresent);
        sb.append('}');
        return sb.toString();
    }
}
