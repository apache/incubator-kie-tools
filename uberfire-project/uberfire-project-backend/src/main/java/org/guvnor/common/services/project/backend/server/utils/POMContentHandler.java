/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.enterprise.context.Dependent;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.MavenRepository;
import org.guvnor.common.services.project.model.POM;

@Dependent
public class POMContentHandler {

    public POMContentHandler() {
        // Weld needs this for proxying.
    }

    public String toString(final POM pomModel)
            throws IOException {
        return toString(pomModel,
                        new Model());
    }

    private String toString(final POM pom,
                            final Model model) throws IOException {
        model.setName(pom.getName());
        model.setDescription(pom.getDescription());
        model.setArtifactId(pom.getGav().getArtifactId());
        model.setModelVersion(pom.getModelVersion());
        model.setUrl(pom.getUrl());

        model.setGroupId(pom.getGav().getGroupId());
        model.setVersion(pom.getGav().getVersion());

        model.setPackaging(pom.getPackaging());

        model.setParent(getParent(pom));
        model.setBuild(getBuild(pom,
                                model));
        model.setModules(getModules(pom));
        model.setRepositories(getRepositories(pom));
        new DependencyUpdater(model.getDependencies()).updateDependencies(pom.getDependencies());

        StringWriter stringWriter = new StringWriter();
        new MavenXpp3Writer().write(stringWriter,
                                    model);
        return stringWriter.toString();
    }

    private Build getBuild(final POM pom,
                           final Model model) {
        return new BuildContentHandler().update(pom.getBuild(),
                                                model.getBuild());
    }

    private ArrayList<Repository> getRepositories(final POM pom) {
        ArrayList<Repository> result = new ArrayList<Repository>();
        for (MavenRepository mavenRepository : pom.getRepositories()) {
            result.add(fromClientModelToPom(mavenRepository));
        }
        return result;
    }

    private ArrayList<String> getModules(final POM pom) {
        ArrayList<String> result = new ArrayList<String>();
        if (pom.getModules() != null) {
            for (String module : pom.getModules()) {
                result.add(module);
            }
        }
        return result;
    }

    private Parent getParent(final POM pom) {
        if (pom.getParent() == null) {
            return null;
        } else {
            Parent parent = new Parent();
            parent.setGroupId(pom.getParent().getGroupId());
            parent.setArtifactId(pom.getParent().getArtifactId());
            parent.setVersion(pom.getParent().getVersion());
            return parent;
        }
    }

    /**
     * @param gavModel The model that is saved
     * @param originalPomAsText The original pom in text form, since the guvnor POM model does not cover all the pom.xml features.
     * @return pom.xml for saving, The original pom.xml with the fields edited in gavModel replaced.
     * @throws IOException
     */
    public String toString(final POM gavModel,
                           final String originalPomAsText) throws IOException, XmlPullParserException {

        return toString(gavModel,
                        new MavenXpp3Reader().read(new StringReader(originalPomAsText)));
    }

    private Repository fromClientModelToPom(final MavenRepository from) {
        Repository to = new Repository();
        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    public POM toModel(final String pomAsString) throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new StringReader(pomAsString));

        POM pomModel = new POM(
                model.getName(),
                model.getDescription(),
                model.getUrl(),
                new GAV(
                        (model.getGroupId() == null ? model.getParent().getGroupId() : model.getGroupId()),
                        (model.getArtifactId() == null ? model.getParent().getArtifactId() : model.getArtifactId()),
                        (model.getVersion() == null ? model.getParent().getVersion() : model.getVersion())
                )
        );

        pomModel.setPackaging(model.getPackaging());

        if (model.getParent() != null) {
            pomModel.setParent(new GAV(model.getParent().getGroupId(),
                                       model.getParent().getArtifactId(),
                                       model.getParent().getVersion()));
        }

        pomModel.getModules().clear();
        for (String module : model.getModules()) {
            pomModel.getModules().add(module);
            pomModel.setPackaging("pom");
        }
        for (Repository repository : model.getRepositories()) {
            pomModel.addRepository(fromPomModelToClientModel(repository));
        }

        pomModel.setDependencies(new DependencyContentHandler().fromPomModelToClientModel(model.getDependencies()));

        pomModel.setBuild(new BuildContentHandler().fromPomModelToClientModel(model.getBuild()));

        return pomModel;
    }

    private MavenRepository fromPomModelToClientModel(final Repository from) {
        MavenRepository to = new MavenRepository();

        to.setId(from.getId());
        to.setName(from.getName());
        to.setUrl(from.getUrl());

        return to;
    }

    public Model convert(final POM pom,
                         final Model model) {
        model.setName(pom.getName());
        model.setDescription(pom.getDescription());
        model.setArtifactId(pom.getGav().getArtifactId());
        model.setModelVersion(pom.getModelVersion());
        model.setUrl(pom.getUrl());

        model.setGroupId(pom.getGav().getGroupId());
        model.setVersion(pom.getGav().getVersion());

        model.setPackaging(pom.getPackaging());

        model.setParent(getParent(pom));
        model.setBuild(getBuild(pom,
                                model));
        model.setModules(getModules(pom));
        model.setRepositories(getRepositories(pom));

        return model;
    }

    public Model convert(final POM pom) {
        return convert(pom, new Model());
    }
}
