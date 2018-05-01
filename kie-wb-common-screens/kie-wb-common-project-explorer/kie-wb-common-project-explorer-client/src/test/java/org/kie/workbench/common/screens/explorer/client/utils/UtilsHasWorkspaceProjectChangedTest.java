package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Arrays;
import java.util.Collection;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class UtilsHasWorkspaceProjectChangedTest {

    private final WorkspaceProject wp1;

    private final WorkspaceProject wp2;

    private final boolean hasWorkspaceProjectCreated;

    public UtilsHasWorkspaceProjectChangedTest(final boolean hasWorkspaceProjectCreated,
                                               final WorkspaceProject wp1,
                                               final WorkspaceProject wp2) {

        this.hasWorkspaceProjectCreated = hasWorkspaceProjectCreated;
        this.wp1 = wp1;
        this.wp2 = wp2;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {

        WorkspaceProject wp1 = mock(WorkspaceProject.class);
        when(wp1.getRepository()).thenReturn(mock(Repository.class));

        WorkspaceProject wp2 = mock(WorkspaceProject.class);
        when(wp2.getRepository()).thenReturn(mock(Repository.class));

        return Arrays.asList(new Object[][]{
                {false, null, null},
                {true, null, wp1},
                {true, wp1, null},
                {true, wp1, wp2},
                {false, wp1, wp1}
        });
    }

    @Test
    public void testRepositoryChanged() throws Exception {
        assertEquals(hasWorkspaceProjectCreated,
                     Utils.hasRepositoryChanged(wp1,
                                                wp2));
    }
}
