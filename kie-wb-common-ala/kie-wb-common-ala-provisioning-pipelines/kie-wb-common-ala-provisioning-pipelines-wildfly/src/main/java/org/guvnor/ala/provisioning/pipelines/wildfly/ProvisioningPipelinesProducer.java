/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.provisioning.pipelines.wildfly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.SystemPipelineDescriptor;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.config.impl.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProviderType;

@ApplicationScoped
public class ProvisioningPipelinesProducer {

    public ProvisioningPipelinesProducer() {
        //Empty constructor for Weld proxying
    }

    /**
     * Produces a pipeline for provisioning building an provisioning war applications into an Wildlfy server.
     */
    @Produces
    public SystemPipelineDescriptor getWildflyPipeline() {

        return new SystemPipelineDescriptor() {
            @Override
            public Optional<ProviderType> getProviderType() {
                return Optional.of(WildflyProviderType.instance());
            }

            @Override
            public Pipeline getPipeline() {

                // Create Wildfly Pipeline Configuration
                final GitConfig gitConfig = new GitConfig() {
                };

                final MavenProjectConfig projectConfig = new MavenProjectConfig() {
                };

                final MavenBuildConfig mavenBuildConfig = new MavenBuildConfig() {
                    @Override
                    public List<String> getGoals() {
                        final List<String> result = new ArrayList<>();
                        result.add("clean");
                        result.add("package");
                        return result;
                    }

                    @Override
                    public Properties getProperties() {
                        final Properties result = new Properties();
                        result.setProperty("failIfNoTests",
                                           "false");
                        return result;
                    }
                };

                final MavenBuildExecConfig mavenBuildExecConfig = new MavenBuildExecConfig() {
                };

                final WildflyProviderConfig wildflyProviderConfig = new WildflyProviderConfig() {
                };

                final ContextAwareWildflyRuntimeExecConfig wildflyRuntimeExecConfig = new ContextAwareWildflyRuntimeExecConfig() {
                };

                final Pipeline pipeline = PipelineFactory
                        .newBuilder()
                        .addConfigStage("Git Source",
                                        gitConfig)
                        .addConfigStage("Maven Project",
                                        projectConfig)
                        .addConfigStage("Maven Build Config",
                                        mavenBuildConfig)
                        .addConfigStage("Maven Build",
                                        mavenBuildExecConfig)
                        .addConfigStage("Wildfly Provider Config",
                                        wildflyProviderConfig)
                        .addConfigStage("Wildfly Runtime Exec",
                                        wildflyRuntimeExecConfig)
                        .buildAs("source-to-wildlfy-provisioning");

                return pipeline;
            }
        };
    }
}