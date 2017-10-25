/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.guvnor.ala.docker.model;

import java.util.List;
import java.util.Properties;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.model.impl.MavenBuildImpl;

/**
 * Docker Build Implementation, extending MavenBuildImpl because it uses the
 * maven plugin to create the docker image.
 * @see MavenBuildImpl
 * @see DockerBuild
 */
public class DockerBuildImpl extends MavenBuildImpl
        implements DockerBuild {

    public DockerBuildImpl(final Project project,
                           final List<String> goals,
                           final Properties properties) {
        super(project,
              goals,
              properties);
    }

    @Override
    public MavenBuild asNewClone(final MavenBuild source) {
        return new DockerBuildImpl(getProject(),
                                   getGoals(),
                                   getProperties());
    }
}