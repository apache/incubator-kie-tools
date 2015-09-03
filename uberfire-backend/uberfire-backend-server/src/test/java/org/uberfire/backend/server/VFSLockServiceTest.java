package org.uberfire.backend.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;

@RunWith(MockitoJUnitRunner.class)
public class VFSLockServiceTest {
    
    @InjectMocks
    private VFSLockServiceImpl lockService;
    
    @Mock
    private IOService ioService;
    
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
    
    private void setupRpcContext() {
        final Message message = MessageBuilder.createMessage("for testing").signalling().done().getMessage();
        message.setResource( "Session", queueSession );
        RpcContext.set( message );
    }

}