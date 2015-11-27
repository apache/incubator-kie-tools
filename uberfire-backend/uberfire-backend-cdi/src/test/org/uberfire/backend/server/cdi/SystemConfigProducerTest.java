/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
public class SystemConfigProducerTest {

    SystemConfigProducer producer;
    BeanManager bm;
    Bean<IOService> ioServiceBean;
    Set<Bean<?>> configIOBeans = new HashSet<Bean<?>>();
    IOService ioServiceMock;
    FileSystem fs;

    @Before
    public void setUp() throws Exception {
        producer = new SystemConfigProducer();
        bm = mock( BeanManager.class );
        ioServiceBean = mock( Bean.class );
        configIOBeans.add( ioServiceBean );
        ioServiceMock = mock( IOService.class );
        fs =  mock( FileSystem.class );
    }

    @Test
    public void createAndDestroyFSShouldRegisterUnregisterOnPriorityDisposableRegistry() throws Exception {

        when( bm.getBeans( "configIO" ) ).thenReturn( configIOBeans );
        when( bm.getReference( eq( ioServiceBean ), eq( IOService.class ), any( CreationalContext.class ) ) )
                .thenReturn( ioServiceMock );
        when( ioServiceMock.newFileSystem( any( URI.class ), any( Map.class ) ) )
                .thenReturn(fs);


        final Bean fileSystemBean = producer.createFileSystemBean( bm,
                                                                   mock( InjectionTarget.class ) );


        assertNull( PriorityDisposableRegistry.get( "systemFS" ) );

        fileSystemBean.create( mock( CreationalContext.class ) );

        assertNotNull( PriorityDisposableRegistry.get( "systemFS" ) );

        fileSystemBean.destroy( fs, mock( CreationalContext.class ) );

        assertNull( PriorityDisposableRegistry.get( "systemFS" ) );
    }

}