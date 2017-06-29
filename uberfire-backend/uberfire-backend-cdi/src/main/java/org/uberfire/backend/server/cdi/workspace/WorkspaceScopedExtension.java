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

package org.uberfire.backend.server.cdi.workspace;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Workspace Scoped CDI Extension to add WorkspaceScoped behavior into Uberfire
 */
public class WorkspaceScopedExtension implements Extension {

    private Logger logger = LoggerFactory.getLogger(WorkspaceScopedExtension.class);

    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
        if (logger.isDebugEnabled()) {
            logger.debug("Before bean discovery, adding WosrkspaceScoped");
        }

        bbd.addScope(WorkspaceScoped.class,
                     true,
                     false);
    }

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd,
                                   BeanManager beanManager) {
        if (logger.isDebugEnabled()) {
            logger.debug("After bean discovery, adding WorkspaceScopeContext");
        }

        abd.addContext(new WorkspaceScopeContext(beanManager));
    }
}
