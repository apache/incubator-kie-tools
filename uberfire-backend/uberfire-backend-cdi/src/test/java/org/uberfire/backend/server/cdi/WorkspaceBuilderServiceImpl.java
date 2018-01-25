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

package org.uberfire.backend.server.cdi;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.slf4j.Logger;
import org.uberfire.backend.server.cdi.workspace.WorkspaceScoped;

/**
 * Just for testing purposes
 */
@Service
@WorkspaceScoped
public class WorkspaceBuilderServiceImpl implements
                                         WorkspaceBuilderService {

    //private Logger logger = LoggerFactory.getLogger( WorkspaceBuilderServiceImpl.class );
    private Logger logger;

    public WorkspaceBuilderServiceImpl() {
    }

    @Inject
    public WorkspaceBuilderServiceImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void build(String gav) {
        try {
            logger.info("Building {} ...",
                        gav);
            logger.info("Thread name: " + Thread.currentThread().getName());
            Thread.currentThread().sleep(5000l);
            logger.info("Building finished {}",
                        gav);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
