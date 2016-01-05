/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startable;

@Singleton
@Startup
public class AlternativeStarterBean {

    private static final Logger logger = LoggerFactory.getLogger(AlternativeStarterBean.class);

    private static final String EJB_METHOD = "ejb";
    private static final String START_METHOD = System.getProperty( "org.uberfire.start.method", "cdi" );

    @Inject
    private Instance<Startable> startableBeans;

    @PostConstruct
    public void configure() {
        if (EJB_METHOD.equals(START_METHOD)) {
            logger.debug("Starting all beans defined as startable...");
            if (!startableBeans.isUnsatisfied()) {
                for (Startable startableBean : startableBeans) {
                    startableBean.start();
                }
            }
            logger.info("All startable beans properly started");
        }
    }
}
