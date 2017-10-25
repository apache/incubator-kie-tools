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

package org.guvnor.ala.ui.backend.service.converter;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.ui.model.ProviderConfiguration;

/**
 * Defines the conversion between provider specific configurations defined in the guvnor-ala core @see {@link ProviderConfig},
 * and a standard configuration that will be managed by the UI modules @see {@link ProviderConfiguration}.
 */
public interface ProviderConfigConverter<M extends ProviderConfiguration, D extends ProviderConfig>
        extends TypeConverter<M, D> {

}
