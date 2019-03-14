package org.kie.workbench.common.services.backend.healthcheck;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
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
        this.ioServiceCheck = spy(new IoServiceCheck(mock(IOService.class),
                                                     mock(FileSystem.class)));
    }

    @Test
    public void testHealthy() {
        doReturn(true).when(ioServiceCheck).globalDirExists();
        assertEquals(HEALTHY, ioServiceCheck.getStatus());
    }

    @Test
    public void testUnhealthy() {
        doReturn(false).when(ioServiceCheck).globalDirExists();
        assertEquals(UNHEALTHY, ioServiceCheck.getStatus());
    }

    @Test
    public void testInconclusive() {
        doThrow(new RuntimeException()).when(ioServiceCheck).globalDirExists();
        assertEquals(INCONCLUSIVE, ioServiceCheck.getStatus());
    }
}