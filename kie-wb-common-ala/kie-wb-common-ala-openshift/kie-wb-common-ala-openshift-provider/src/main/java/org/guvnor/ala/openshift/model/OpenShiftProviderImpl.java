/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.guvnor.ala.openshift.model;

import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.openshift.config.OpenShiftProviderConfig;
import org.guvnor.ala.openshift.config.impl.OpenShiftProviderConfigImpl;
import org.guvnor.ala.runtime.providers.base.BaseProvider;

/**
 * OpenShift provider implementation.
 */
public class OpenShiftProviderImpl extends BaseProvider implements OpenShiftProvider, CloneableConfig<OpenShiftProvider> {

    public OpenShiftProviderImpl() {
    }

    public OpenShiftProviderImpl(final String id, final OpenShiftProviderConfig config) {
        super(id, OpenShiftProviderType.instance(), new OpenShiftProviderConfigImpl(config));
    }

    @Override
    public OpenShiftProvider asNewClone(final OpenShiftProvider source) {
        return new OpenShiftProviderImpl(source.getId(), (OpenShiftProviderConfig) source.getConfig());
    }
}
