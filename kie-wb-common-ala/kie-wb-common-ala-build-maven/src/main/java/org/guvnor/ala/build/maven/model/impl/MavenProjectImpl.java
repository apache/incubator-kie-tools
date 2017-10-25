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

package org.guvnor.ala.build.maven.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.guvnor.ala.build.maven.model.MavenProject;
import org.guvnor.ala.build.maven.model.PlugIn;
import org.guvnor.ala.config.CloneableConfig;
import org.uberfire.java.nio.file.Path;

public class MavenProjectImpl implements MavenProject,
                                         CloneableConfig<MavenProject> {

    private String id;
    private String type;
    private String name;
    private String expectedBinary;
    private Path rootPath;
    private Path path;
    private Path binaryPath;
    private String tempDir;
    private Collection<PlugIn> buildPlugins = Collections.emptyList();

    public MavenProjectImpl() {
    }

    public MavenProjectImpl(final String id,
                            final String type,
                            final String name,
                            final String expectedBinary,
                            final Path rootPath,
                            final Path path,
                            final Path binaryPath,
                            final String tempDir,
                            final Collection<PlugIn> buildPlugins) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.expectedBinary = expectedBinary;
        this.rootPath = rootPath;
        this.path = path;
        this.binaryPath = binaryPath;
        this.tempDir = tempDir;
        if (buildPlugins != null) {
            this.buildPlugins = new ArrayList<>(buildPlugins);
        }
    }

    @Override
    public Collection<PlugIn> getBuildPlugins() {
        return buildPlugins;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getExpectedBinary() {
        return expectedBinary;
    }

    @Override
    public Path getRootPath() {
        return rootPath;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Path getBinaryPath() {
        return binaryPath;
    }

    @Override
    public String getTempDir() {
        return tempDir;
    }

    @Override
    public MavenProject asNewClone(final MavenProject origin) {
        return new MavenProjectImpl(origin.getId(),
                                    origin.getType(),
                                    origin.getName(),
                                    origin.getExpectedBinary(),
                                    origin.getRootPath(),
                                    origin.getPath(),
                                    origin.getBinaryPath(),
                                    origin.getTempDir(),
                                    origin.getBuildPlugins());
    }
}
