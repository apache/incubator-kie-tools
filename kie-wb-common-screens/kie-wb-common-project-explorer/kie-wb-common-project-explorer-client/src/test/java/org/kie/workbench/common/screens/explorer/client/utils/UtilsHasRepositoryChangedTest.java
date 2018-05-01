package org.kie.workbench.common.screens.explorer.client.utils;

import java.util.Arrays;
import java.util.Collection;

import org.guvnor.structure.repositories.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class UtilsHasRepositoryChangedTest {

    private final Repository repository1;

    private final Repository repository2;

    private final boolean hasRepositoryCreated;

    public UtilsHasRepositoryChangedTest(final boolean hasRepositoryCreated,
                                         final Repository repository1,
                                         final Repository repository2) {

        this.hasRepositoryCreated = hasRepositoryCreated;
        this.repository1 = repository1;
        this.repository2 = repository2;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testData() {

        Repository repo1 = mock(Repository.class);
        when(repo1.getAlias()).thenReturn("repo1");

        Repository repo2 = mock(Repository.class);
        when(repo2.getAlias()).thenReturn("repo2");

        return Arrays.asList(new Object[][]{
                {false, null, null},
                {true, null, repo1},
                {true, repo1, null},
                {true, repo1, repo2},
                {false, repo1, repo1}
        });
    }

    @Test
    public void testRepositoryChanged() throws Exception {
        assertEquals(hasRepositoryCreated,
                     Utils.hasRepositoryChanged(repository1,
                                                repository2));
    }
}
