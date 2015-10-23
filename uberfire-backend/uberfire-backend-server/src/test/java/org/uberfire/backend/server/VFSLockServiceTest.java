package org.uberfire.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.client.api.QueueSession;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.server.api.RpcContext;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.rpc.SessionInfo;

@RunWith(MockitoJUnitRunner.class)
public class VFSLockServiceTest {
    
    @InjectMocks
    private VFSLockServiceImpl lockService;
    
    @Mock
    private IOService ioService;

    @Mock
    private FileSystem fileSystem;
    
    @Mock
    private SessionInfo sessionInfo;
    
    @Mock
    private QueueSession queueSession;
    
    @Mock
    private HttpSession httpSession;
    
    private Path path = PathFactory.newPath( "file-to-lock.txt", "default://file-to-lock.txt" );
    
    @Before
    public void setup() {
        setupRpcContext();
        
        User testUser = new UserImpl("testUser");
        when(sessionInfo.getIdentity()).thenReturn( testUser );
        
        when(queueSession.getAttribute(HttpSession.class, HttpSession.class.getName())).thenReturn( httpSession );
    }
    
    @Test
    public void acquireLockSucceedsIfFileUnlocked() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( false );
        
        final LockResult result = lockService.acquireLock( path );
        assertTrue(result.isSuccess());
        assertEquals(path, result.getLockInfo().getFile());
        assertEquals("testUser", result.getLockInfo().lockedBy());
        assertTrue(result.getLockInfo().isLocked());
    }
    
    @Test
    public void acquireLockSucceedsIfLockOwned() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "testUser" );
        
        final LockResult result = lockService.acquireLock( path );
        assertTrue(result.isSuccess());
        assertEquals(path, result.getLockInfo().getFile());
        assertEquals("testUser", result.getLockInfo().lockedBy());
        assertTrue(result.getLockInfo().isLocked());
    }
    
    @Test
    public void acquireLockFailsIfFileLocked() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "some-other-user" );
        
        final LockResult result = lockService.acquireLock( path );
        assertFalse(result.isSuccess());
        assertEquals(path, result.getLockInfo().getFile());
        assertEquals("some-other-user", result.getLockInfo().lockedBy());
        assertTrue(result.getLockInfo().isLocked());
    }
    
    @Test
    public void acquireLockUpdatesSession() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( false );
        
        lockService.acquireLock( path );
        verify(httpSession).setAttribute( eq(VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME), any(Set.class));
    }
    
    @Test
    // Unfortunately, batching is required for ensuring writes are properly 
    // replicated in the cluster. This needs to addressed in a future version 
    // of UF: https://issues.jboss.org/browse/UF-242
    public void acquireLockUsesBatch() {
        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( false );

        lockService.acquireLock( path );

        final InOrder inOrder = inOrder( ioService );
        inOrder.verify( ioService ).startBatch( fileSystem );
        inOrder.verify( ioService ).exists( any( org.uberfire.java.nio.file.Path.class ) );
        inOrder.verify( ioService ).write( any( org.uberfire.java.nio.file.Path.class ), any( String.class ) );
        inOrder.verify( ioService ).endBatch();
    }
    
    @Test
    public void releaseLockSucceedsIfLockOwned() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "testUser" );
        
        final LockResult result = lockService.releaseLock( path );
        assertTrue(result.isSuccess());
        assertEquals(path, result.getLockInfo().getFile());
        assertEquals(null, result.getLockInfo().lockedBy());
        assertFalse(result.getLockInfo().isLocked());
    }
    
    @Test
    public void releaseLockFailsIfLockNotOwned() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "some-other-user" );
        
        try {
            lockService.releaseLock( path );
            fail("Expected exception on attempt to release lock not owned by user");
        } 
        catch (Exception ioe) {
         // expected    
        }
    }
    
    @Test
    public void forceReleaseLockSucceedsIfLockNotOwned() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "some-other-user" );
        
        final LockResult result = lockService.forceReleaseLock( path );
        assertTrue(result.isSuccess());
        assertEquals(path, result.getLockInfo().getFile());
        assertEquals(null, result.getLockInfo().lockedBy());
        assertFalse(result.getLockInfo().isLocked());
    }
    
    @Test
    public void releaseLockFailsIfFileUnlocked() {
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( false );
        
        final LockResult result = lockService.releaseLock( path );
        assertFalse(result.isSuccess());
        assertEquals(path, result.getLockInfo().getFile());
        assertEquals(null, result.getLockInfo().lockedBy());
        assertFalse(result.getLockInfo().isLocked());
    }
    
    @Test
    public void releaseLockUpdatesSession() {
        lockService.acquireLock( path );
        
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "testUser" );
        
        lockService.releaseLock( path );
        verify(httpSession).setAttribute(eq(VFSLockServiceImpl.LOCK_SESSION_ATTRIBUTE_NAME), any(Set.class));
    }
    
    @Test
    // Unfortunately, batching is required for ensuring writes are properly 
    // replicated in the cluster. This needs to addressed in a future version 
    // of UF: https://issues.jboss.org/browse/UF-242
    public void releaseLockUsesBatch() {
        lockService.acquireLock( path );
        
        when(ioService.exists( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( true );
        when(ioService.readAllString( any(org.uberfire.java.nio.file.Path.class) )).thenReturn( "testUser" );
        
        lockService.releaseLock( path );
        
        final InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).startBatch( fileSystem );
        inOrder.verify(ioService).exists( any(org.uberfire.java.nio.file.Path.class) );
        inOrder.verify(ioService).readAllString( any(org.uberfire.java.nio.file.Path.class) );
        inOrder.verify(ioService).delete( any(org.uberfire.java.nio.file.Path.class) );
        inOrder.verify(ioService).endBatch( );
    }
    
    @Test
    public void retrieveLockInfoForLockedFile() {
        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( true );
        when( ioService.readAllString( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( "some-user" );

        final LockInfo info = lockService.retrieveLockInfo( path );
        assertTrue( info.isLocked() );
        assertEquals( "some-user", info.lockedBy() );
    }

    @Test
    public void retrieveLockInfoForUnlockedFile() {
        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( false );
        when( ioService.readAllString( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( null );

        final LockInfo info = lockService.retrieveLockInfo( path );
        assertFalse( info.isLocked() );
        assertNull( info.lockedBy() );
    }
    
    @Test
    public void retrieveLockInfoNoSuchFileException() {
        when( ioService.exists( any( org.uberfire.java.nio.file.Path.class ) ) ).thenReturn( true );
        when( ioService.readAllString( any( org.uberfire.java.nio.file.Path.class ) ) ).thenThrow( new NoSuchFileException() );

        final LockInfo info = lockService.retrieveLockInfo( path );
        assertFalse( info.isLocked() );
        assertNull( info.lockedBy() );
    }
    
    private void setupRpcContext() {
        final Message message = MessageBuilder.createMessage("for testing").signalling().done().getMessage();
        message.setResource( "Session", queueSession );
        RpcContext.set( message );
    }

}