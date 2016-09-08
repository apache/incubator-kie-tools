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
package org.kie.workbench.common.screens.explorer.backend.server;

import org.jboss.weld.environment.se.Weld;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.explorer.service.ExplorerService;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import static org.junit.Assert.assertNotSame;


public class ExplorerServiceImplTest {

    protected Weld weld;
    protected BeanManager beanManager;
    private Bean explorerServiceBean;
    private CreationalContext explorerServiceBeanContext;

    @Before
    public void setUp() throws Exception {
        weld = new Weld();
        beanManager = weld.initialize().getBeanManager();

        explorerServiceBean = ( Bean ) beanManager.getBeans( ExplorerService.class ).iterator().next();
        explorerServiceBeanContext = beanManager.createCreationalContext( explorerServiceBean );

    }

    @Test
    public void explorerServiceShouldBeADependentBean() {
        ExplorerService reference1 = lookupReference();
        ExplorerService reference2 = lookupReference();
        assertNotSame( reference1, reference2 );
    }

    private ExplorerService lookupReference() {
        return ( ExplorerService ) beanManager.getReference( explorerServiceBean,
                                                             ExplorerService.class,
                                                             explorerServiceBeanContext );
    }

    @After
    public void tearDown() {
        if ( weld != null && beanManager != null ) {
            weld.shutdown();
        }
    }

}