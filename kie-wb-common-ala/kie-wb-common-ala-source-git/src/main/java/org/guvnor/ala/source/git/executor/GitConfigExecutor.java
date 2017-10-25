/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.source.git.executor;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitRepository;
import org.guvnor.ala.source.git.UFLocal;
import org.guvnor.ala.source.git.config.GitConfig;
import org.uberfire.java.nio.file.FileSystems;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

public class GitConfigExecutor implements FunctionConfigExecutor<GitConfig, Source> {

    private final SourceRegistry sourceRegistry;

    @Inject
    public GitConfigExecutor(final SourceRegistry sourceRegistry) {
        this.sourceRegistry = sourceRegistry;
    }

    @Override
    public Optional<Source> apply(final GitConfig gitConfig) {
        checkNotEmpty("repo-name parameter is mandatory",
                      gitConfig.getRepoName());
        if (Boolean.parseBoolean(gitConfig.getCreateRepo())) {
            final URI uri = URI.create("git://" + gitConfig.getRepoName());
            FileSystems.newFileSystem(uri,
                                      new HashMap<String, Object>() {
                                          {
                                              if (gitConfig.getOrigin() != null && !gitConfig.getOrigin().isEmpty()) {
                                                  put("origin",
                                                      gitConfig.getOrigin());
                                              } else {
                                                  put("init",
                                                      Boolean.TRUE);
                                              }
                                              if (gitConfig.getOutPath() != null && !gitConfig.getOutPath().isEmpty()) {
                                                  put("out-dir",
                                                      gitConfig.getOutPath());
                                              }
                                          }
                                      });
        } else {
            final URI uri = URI.create("git://" + gitConfig.getRepoName() + "?sync");
            try {
                FileSystems.getFileSystem(uri);
            } catch (Exception ex) {
                // The repo might not support sync, because it doesn't have an origin, we should move forward for now.
            }
        }
        final GitRepository gitRepository = (GitRepository) new UFLocal().getRepository(gitConfig.getRepoName(),
                                                                                        Collections.emptyMap());
        final Optional<Source> source_ = Optional.ofNullable(gitRepository.getSource((gitConfig.getBranch() != null && !gitConfig.getBranch().isEmpty()) ? gitConfig.getBranch() : "master"));
        if (source_.isPresent()) {
            Source source = source_.get();
            sourceRegistry.registerRepositorySources(source.getPath(),
                                                     gitRepository);
            sourceRegistry.registerSource(gitRepository,
                                          source);
        }
        return source_;
    }

    @Override
    public Class<? extends Config> executeFor() {
        return GitConfig.class;
    }

    @Override
    public String outputId() {
        return "source";
    }

    @Override
    public String inputId() {
        return "git-config";
    }
}
