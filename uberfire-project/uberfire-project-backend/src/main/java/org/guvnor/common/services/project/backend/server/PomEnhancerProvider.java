/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.project.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.uberfire.annotations.Customizable;
import org.uberfire.annotations.FallbackImplementation;

@ApplicationScoped
public class PomEnhancerProvider {

    @Inject
    private Instance<PomEnhancer> pomEnhancer;

    @Inject
    @FallbackImplementation
    private DefaultPomEnhancer defaultPomEnhancer;

    @Produces
    @Customizable
    public PomEnhancer produce() {
        if (this.pomEnhancer.isUnsatisfied()) {
            return defaultPomEnhancer;
        }
        return this.pomEnhancer.get();
    }
}
