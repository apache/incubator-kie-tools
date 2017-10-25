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

package org.guvnor.ala.ui.wildfly.client.pipeline;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.wizard.pipeline.PipelineDescriptor;
import org.guvnor.ala.ui.client.wizard.pipeline.params.PipelineParamsForm;
import org.guvnor.ala.ui.client.wizard.source.SourceConfigurationParamsPresenter;
import org.guvnor.ala.ui.model.PipelineKey;

/**
 * Descriptor for a pipeline that builds and provisions a war application into an Wildlfy server.
 */
@ApplicationScoped
public class WildflyProvisioningPipelineDescriptor
        implements PipelineDescriptor {

    public static final String SOURCE_TO_WILDFLY_PROVISIONING = "source-to-wildlfy-provisioning";

    private List<PipelineParamsForm> paramsForms = new ArrayList<>();

    @Inject
    public WildflyProvisioningPipelineDescriptor(final SourceConfigurationParamsPresenter sourceConfigurationParamsPresenter) {
        paramsForms.add(sourceConfigurationParamsPresenter);
    }

    @Override
    public boolean accept(PipelineKey pipelineKey) {
        return pipelineKey != null && SOURCE_TO_WILDFLY_PROVISIONING.equals(pipelineKey.getId());
    }

    @Override
    public List<PipelineParamsForm> getParamForms() {
        return paramsForms;
    }
}
