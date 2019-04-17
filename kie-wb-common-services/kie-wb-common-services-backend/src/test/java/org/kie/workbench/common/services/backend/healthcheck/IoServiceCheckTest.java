package org.kie.workbench.common.services.backend.healthcheck;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.HEALTHY;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.INCONCLUSIVE;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.UNHEALTHY;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class IoServiceCheckTest {

    private IoServiceCheck ioServiceCheck;

    @Before
    public void before() {
        this.ioServiceCheck = spy(new IoServiceCheck(mock(IOService.class)));
    }

    @Test
    public void testHealthy() {
        doReturn(true).when(ioServiceCheck).systemFsExists();
        assertEquals(HEALTHY, ioServiceCheck.getStatus());
    }

    @Test
    public void testUnhealthy() {
        doReturn(false).when(ioServiceCheck).systemFsExists();
        assertEquals(UNHEALTHY, ioServiceCheck.getStatus());
    }

    @Test
    public void testInconclusive() {
        doThrow(new RuntimeException()).when(ioServiceCheck).systemFsExists();
        assertEquals(INCONCLUSIVE, ioServiceCheck.getStatus());
    }

    @Test
    public void testSystemFsExistsOpen() {
        final FileSystem fs = mock(FileSystem.class);
        doReturn(true).when(fs).isOpen();
        doReturn(fs).when(ioServiceCheck).getFileSystem();

        assertTrue(ioServiceCheck.systemFsExists());
    }

    @Test
    public void testSystemFsExistsClosed() {
        final FileSystem fs = mock(FileSystem.class);
        doReturn(false).when(fs).isOpen();
        doReturn(fs).when(ioServiceCheck).getFileSystem();

        assertFalse(ioServiceCheck.systemFsExists());
    }

    @Test
    public void testSystemFsExistsWhenNull() {
        doReturn(null).when(ioServiceCheck).getFileSystem();
        assertFalse(ioServiceCheck.systemFsExists());
    }

    @Test
    public void testSystemFsExistsError() {
        doThrow(new RuntimeException()).when(ioServiceCheck).getFileSystem();
        assertFalse(ioServiceCheck.systemFsExists());
    }

    @Test
    public void testSystemExistsErrorGetName() {
        final FileSystem fs = mock(FileSystem.class);
        doThrow(new RuntimeException()).when(fs).isOpen();
        doReturn(fs).when(ioServiceCheck).getFileSystem();

        assertFalse(ioServiceCheck.systemFsExists());
    }
}