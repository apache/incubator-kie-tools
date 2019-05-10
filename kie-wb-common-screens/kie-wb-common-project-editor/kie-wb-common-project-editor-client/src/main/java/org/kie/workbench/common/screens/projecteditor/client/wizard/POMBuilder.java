/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.projecteditor.client.wizard;

import java.util.ArrayList;

import org.guvnor.common.services.project.model.Build;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Plugin;
import org.guvnor.common.services.project.utils.NewWorkspaceProjectUtils;

/**
 * The Module Name is used to generate the folder name and hence is only checked to be a valid file name.
 * The ArtifactID is initially set to the module name, subsequently validated against the maven regex,
 * and preserved as is in the pom.xml file. However, as it is used to construct the default workspace and
 * hence package names, it is sanitized in the ModuleService.newModule() method.
 */
public class POMBuilder {

    private final POM pom;

    public POMBuilder() {
        this(new POM());
    }

    public POMBuilder(final POM pom) {
        this.pom = pom;
        setDefaultPackaging(pom);
        setDefaultVersion(pom);
    }

    private void setDefaultVersion(POM pom) {
        if (pom.getGav().getVersion() == null) {
            this.pom.getGav().setVersion("1.0");
        }
    }

    private void setDefaultPackaging(POM pom) {
        if (pom.getPackaging() == null) {
            this.pom.setPackaging("kjar");
        }
    }

    public POMBuilder setModuleName(final String moduleName) {
        pom.setName(moduleName);
        if (moduleName != null) {
            pom.getGav().setArtifactId(NewWorkspaceProjectUtils.sanitizeProjectName(moduleName));
        }
        return this;
    }

    public POMBuilder setGroupId(final String groupId) {
        pom.getGav().setGroupId(groupId);
        return this;
    }

    public POMBuilder setVersion(final String version) {
        pom.getGav().setVersion(version);
        return this;
    }

    public POMBuilder setPackaging(final String packaging) {
        pom.setPackaging(packaging);
        return this;
    }

    public POMBuilder setBuildPlugins(ArrayList<Plugin> plugins) {
        if (pom.getBuild() == null) {
            pom.setBuild(new Build());
        }
        pom.getBuild().setPlugins(plugins);
        return this;
    }

    public POM build() {
        return pom;
    }
}
