/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */
package org.uberfire.commons.cluster;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClusterJMSJNDIServiceTest extends BaseClusterJMSServiceTest {

    @Override
    ClusterService getClusterService(ConnectionFactory factory) {
        System.setProperty(ClusterParameters.APPFORMER_INITIAL_CONTEXT_FACTORY,
                           this.getClass().getCanonicalName() + "$MyContextFactory");

        System.setProperty(ClusterParameters.APPFORMER_JMS_CONNECTION_MODE,
                           ConnectionMode.JNDI.toString());
        return new ClusterJMSService();
    }

    public static class MyContextFactory implements InitialContextFactory {

        @Override
        public Context getInitialContext(final Hashtable<?, ?> environment) throws NamingException {
            final InitialContext mockCtx = mock(InitialContext.class);
            when(mockCtx.lookup("java:/ConnectionFactory")).thenReturn(factory);
            return mockCtx;
        }
    }
}