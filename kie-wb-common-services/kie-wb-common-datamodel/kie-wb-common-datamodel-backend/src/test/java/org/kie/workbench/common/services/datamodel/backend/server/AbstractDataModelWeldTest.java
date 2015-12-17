/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import org.jboss.weld.environment.se.Weld;
import org.junit.After;
import org.junit.Before;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

abstract public class AbstractDataModelWeldTest {

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    protected Weld weld;
    protected BeanManager beanManager;
    protected Paths paths;

    @Before
    public void setUp() throws Exception {
        // disable git and ssh daemons as they are not needed for the tests
        System.setProperty( "org.uberfire.nio.git.daemon.enabled", "false" );
        System.setProperty( "org.uberfire.nio.git.ssh.enabled", "false" );
        System.setProperty( "org.uberfire.sys.repo.monitor.disabled", "true" );
        //Bootstrap WELD container
        weld = new Weld();
        beanManager = weld.initialize().getBeanManager();
        //Instantiate Paths used in tests for Path conversion
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                Paths.class,
                cc );

        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }

    @After
    public void tearDown() {
        // beanManager will be null in case weld.initialize() failed. And if that is the case the shutdown method
        // would return NPE
        if (weld != null && beanManager != null) {
            weld.shutdown();
        }
    }

}
