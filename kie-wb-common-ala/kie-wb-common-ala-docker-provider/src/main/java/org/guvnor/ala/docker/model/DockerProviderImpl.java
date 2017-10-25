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
package org.guvnor.ala.docker.model;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.runtime.providers.base.BaseProvider;

public class DockerProviderImpl
        extends BaseProvider<DockerProviderConfig>
        implements DockerProvider,
                   CloneableConfig<DockerProvider> {

    public DockerProviderImpl() {
        //No-args constructor for enabling marshalling to work, please do not remove.
    }

    public DockerProviderImpl(final DockerProviderConfigImpl config) {
        super(config.getName(),
              DockerProviderType.instance(),
              config);
    }

    @Override
    public DockerProvider asNewClone(final DockerProvider source) {
        return new DockerProviderImpl(new DockerProviderConfigImpl(source.getConfig().getName(),
                                                                   source.getConfig().getHostIp()));
    }
}