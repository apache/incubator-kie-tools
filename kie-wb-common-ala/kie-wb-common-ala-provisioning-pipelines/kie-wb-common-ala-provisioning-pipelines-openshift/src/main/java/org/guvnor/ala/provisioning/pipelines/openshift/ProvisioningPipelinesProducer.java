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

package org.guvnor.ala.provisioning.pipelines.openshift;

import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.openshift.config.impl.ContextAwareOpenShiftRuntimeExecConfig;
import org.guvnor.ala.openshift.model.OpenShiftProviderType;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.SystemPipelineDescriptor;
import org.guvnor.ala.runtime.providers.ProviderType;

@ApplicationScoped
public class ProvisioningPipelinesProducer {

    public ProvisioningPipelinesProducer() {
        //Empty constructor for Weld proxying
    }

    /**
     * Produces a pipeline for provisioning kie-server images into an OpenShift project.
     */
    @Produces
    public SystemPipelineDescriptor getKieServerProvisioningPipeline() {
        return new SystemPipelineDescriptor() {

            @Override
            public Optional<ProviderType> getProviderType() {
                return Optional.of(OpenShiftProviderType.instance());
            }

            @Override
            public Pipeline getPipeline() {
                return PipelineFactory
                        .newBuilder()
                        .addConfigStage("OpenShift Provider Config",
                                        new OpenShiftProviderConfig() {
                                        })
                        .addConfigStage("OpenShift Runtime Config",
                                        new ContextAwareOpenShiftRuntimeExecConfig())
                        .buildAs("kie-server-provisioning");
            }
        };
    }
}