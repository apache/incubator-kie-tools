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

package org.kie.workbench.screens.workbench.backend.weblogic;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

public class SwapClassloaderExtension implements Extension {

    private ClassLoader tccl;

    public void beforeBeanDiscovery(@Observes javax.enterprise.inject.spi.BeforeBeanDiscovery bbd) {
        this.tccl = Thread.currentThread().getContextClassLoader();

        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());

    }

    public void afterDeploymentValidation( final @Observes AfterDeploymentValidation event, final BeanManager manager ) {
        Thread.currentThread().setContextClassLoader(tccl);
    }
}
