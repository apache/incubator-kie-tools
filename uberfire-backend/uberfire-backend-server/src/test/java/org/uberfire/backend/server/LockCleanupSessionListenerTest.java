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

package org.uberfire.backend.server;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

import java.util.Collections;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.ConfigIOServiceProducer;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@RunWith(MockitoJUnitRunner.class)
public class LockCleanupSessionListenerTest {

    @Mock
    private HttpSessionEvent evt;

    @Mock
    private HttpSession session;

    @Mock
    private IOService ioService;

    @Test(expected = IllegalStateException.class)
    public void throwExceptionWhenIOProducerNotInitialized() {
        final LockCleanupSessionListener listener = new LockCleanupSessionListener();
        listener.sessionDestroyed( evt );
    }

    @Test
    public void initWithoutInjection() {
        final ConfigIOServiceProducer producer = spy( new ConfigIOServiceProducer() );
        final LockCleanupSessionListener listener = new LockCleanupSessionListener();

        try {
            when( evt.getSession() ).thenReturn( session );
            when( session.getAttribute( VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME ) ).thenReturn( Collections.emptySet() );
            when( producer.configIOService() ).thenReturn( ioService );

            producer.setup();
            listener.sessionDestroyed( evt );

            // Needs to programmatically request FS and IOService from producer instead of using @Inject (see UF-237)
            verify( producer ).configIOService();
            verify( producer ).configFileSystem();
        } finally {
            producer.destroy();
        }
    }

    @Test
    public void releaseLockAssociatedWithSession() {
        final ConfigIOServiceProducer producer = spy( new ConfigIOServiceProducer() );
        final LockCleanupSessionListener listener = spy( new LockCleanupSessionListener() );

        try {
            final String lockedBy = "christian";
            final LockInfo lock = new LockInfo( true,lockedBy, PathFactory.newPath( "file", "default://file" ) );

            when( evt.getSession() ).thenReturn( session );
            when( session.getAttribute( VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME ) ).thenReturn( Collections.singleton( lock ) );
            when( producer.configIOService() ).thenReturn( ioService );
            when( ioService.readAllString( any( Path.class ) ) ).thenReturn( lockedBy );

            producer.setup();
            listener.sessionDestroyed( evt );

            verify( ioService, times(1) ).delete( any( Path.class ));
        } finally {
            producer.destroy();
        }
    }

}
