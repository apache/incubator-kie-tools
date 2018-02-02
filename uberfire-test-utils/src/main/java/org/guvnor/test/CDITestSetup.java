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

package org.guvnor.test;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

public class CDITestSetup {

    public final SimpleFileSystemProvider fileSystemProvider = new SimpleFileSystemProvider();
    public BeanManager beanManager;
    public Paths paths;
    private Weld weld;

    public void setUp() throws Exception {
        // Bootstrap WELD container
        weld = new Weld();
        WeldContainer weldContainer = weld.initialize();
        beanManager = weldContainer.getBeanManager();

        // Instantiate Paths used in tests for Path conversion
        paths = getReference(Paths.class);

        // Ensure URLs use the default:// scheme
//        fileSystemProvider.forceAsDefault();
    }

    public void cleanup() {
        if (weld != null) {
            try {
                weld.shutdown();
            } catch (NullPointerException npeException) {
                LoggerFactory.getLogger(CDITestSetup.class)
                        .warn("Problem occured during weld clean up: " + npeException.getMessage());
            }
        }
    }

    public <T> T getReference(Class<T> clazz) {
        Bean bean = (Bean) beanManager.getBeans(clazz).iterator().next();
        CreationalContext cc = beanManager.createCreationalContext(bean);
        return (T) beanManager.getReference(bean,
                                            clazz,
                                            cc);
    }
}
