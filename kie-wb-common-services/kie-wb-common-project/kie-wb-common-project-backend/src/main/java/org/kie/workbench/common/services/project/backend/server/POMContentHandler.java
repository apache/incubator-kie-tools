/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.services.project.backend.server;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.kie.workbench.common.services.project.service.model.GAV;
import org.kie.workbench.common.services.project.service.model.POM;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

@Dependent
public class POMContentHandler {


    public POMContentHandler() {
        // Weld needs this for proxying.       
    }

    public String toString(POM gavModel)
            throws IOException {

        Model model = new Model();
        model.setGroupId(gavModel.getGav().getGroupId());
        model.setArtifactId(gavModel.getGav().getArtifactId());
        model.setVersion(gavModel.getGav().getVersion());
        model.setModelVersion(gavModel.getModelVersion());

        for (org.kie.workbench.common.services.project.service.model.Repository repository : gavModel.getRepositories()) {
            model.addRepository(fromClientModelToPom(repository));
        }

        for (org.kie.workbench.common.services.project.service.model.Dependency dependency : gavModel.getDependencies()) {
            model.addDependency(fromClientModelToPom(dependency));
        }

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter, model);

        return stringWriter.toString();
    }

    private Repository fromClientModelToPom(org.kie.workbench.common.services.project.service.model.Repository from) {
        Repository to = new Repository();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    public POM toModel(String pomAsString)
            throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new StringReader(pomAsString));


        POM gavModel = new POM(
                new GAV(
                        model.getGroupId(),
                        model.getArtifactId(),
                        model.getVersion())
        );

        for (Repository repository : model.getRepositories()) {
            gavModel.addRepository(fromPomModelToClientModel(repository));
        }

        for (Dependency dependency : model.getDependencies()) {
            gavModel.getDependencies().add(fromPomModelToClientModel(dependency));
        }

        return gavModel;
    }

    private org.kie.workbench.common.services.project.service.model.Repository fromPomModelToClientModel(Repository from) {
        org.kie.workbench.common.services.project.service.model.Repository to = new org.kie.workbench.common.services.project.service.model.Repository();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    private org.kie.workbench.common.services.project.service.model.Dependency fromPomModelToClientModel(Dependency from) {
        org.kie.workbench.common.services.project.service.model.Dependency dependency = new org.kie.workbench.common.services.project.service.model.Dependency();

        dependency.setArtifactId(from.getArtifactId());
        dependency.setGroupId(from.getGroupId());
        dependency.setVersion(from.getVersion());

        return dependency;
    }

    private Dependency fromClientModelToPom(org.kie.workbench.common.services.project.service.model.Dependency from) {
        Dependency dependency = new Dependency();

        dependency.setArtifactId(from.getArtifactId());
        dependency.setGroupId(from.getGroupId());
        dependency.setVersion(from.getVersion());

        return dependency;
    }
}
