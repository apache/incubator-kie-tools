package org.kie.workbench.common.project.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;
import org.kie.workbench.common.migration.cli.MigrationConstants;
import org.kie.workbench.common.migration.cli.SystemAccess;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExternalMigrationServiceTest {

    @Test
    public void testMoveSystemRepos() throws IOException {
        SystemAccess systemAccess = mock(SystemAccess.class);
        Path niogitDir = mock(Path.class);
        Path somePath = mock(Path.class);
        File file = mock(File.class);

        when(niogitDir.resolve(anyString())).thenReturn(somePath);
        when(somePath.toFile()).thenReturn(file);
        when(file.exists()).thenReturn(true);
        when(file.isDirectory()).thenReturn(true);
        when(systemAccess.out()).thenReturn(System.out);

        ExternalMigrationService service = new ExternalMigrationService(systemAccess);
        service.moveSystemRepos(niogitDir);

        verify(systemAccess, never()).createDirectory(any(Path.class));
        verify(systemAccess, times(MigrationConstants.systemRepos.length)).move(any(Path.class), any(Path.class));
    }
}
