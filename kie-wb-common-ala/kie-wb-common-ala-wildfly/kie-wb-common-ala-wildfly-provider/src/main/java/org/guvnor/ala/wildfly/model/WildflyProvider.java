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

package org.guvnor.ala.wildfly.model;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;

/**
 * A provider for configure and provision applications into a a Wildfly server.
 * @see WildflyProviderConfig
 * @see Provider
 */
public interface WildflyProvider
        extends Provider<WildflyProviderConfig>,
                ProviderConfig {

}
