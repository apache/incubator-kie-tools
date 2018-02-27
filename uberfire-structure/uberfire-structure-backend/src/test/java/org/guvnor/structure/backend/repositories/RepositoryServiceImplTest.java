package org.guvnor.structure.backend.repositories;

import java.util.Optional;

import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryServiceImplTest {

    @Mock
    private Repository repository;

    @Mock
    private ConfiguredRepositories configuredRepositories;

    @InjectMocks
    private RepositoryServiceImpl repositoryService;

    @Test
    public void testNotCreateNewAliasIfNecessary() {
        when(configuredRepositories.getRepositoryByRepositoryAlias(any(),
                                                                   eq("other-name"))).thenReturn(repository);
        doReturn(Optional.of(mock(Branch.class))).when(repository).getDefaultBranch();
        doReturn("alias").when(repository).getAlias();
        String newAlias = repositoryService.createFreshRepositoryAlias("alias",
                                                                       new Space("alias"));

        assertEquals("alias",
                     newAlias);
    }

    @Test
    public void testCreateNewAliasIfNecessary() {
        when(configuredRepositories.getRepositoryByRepositoryAlias(any(),
                                                                   eq("alias"))).thenReturn(repository);
        doReturn(Optional.of(mock(Branch.class))).when(repository).getDefaultBranch();
        doReturn("alias").when(repository).getAlias();
        String newAlias = repositoryService.createFreshRepositoryAlias("alias",
                                                                       new Space("alias"));

        assertEquals("alias-1",
                     newAlias);
    }

    @Test
    public void testCreateSecondNewAliasIfNecessary() {
        when(configuredRepositories.getRepositoryByRepositoryAlias(any(),
                                                                   eq("alias"))).thenReturn(repository);
        when(configuredRepositories.getRepositoryByRepositoryAlias(any(),
                                                                   eq("alias-1"))).thenReturn(repository);
        doReturn(Optional.of(mock(Branch.class))).when(repository).getDefaultBranch();
        doReturn("alias").when(repository).getAlias();
        String newAlias = repositoryService.createFreshRepositoryAlias("alias",
                                                                       new Space("alias"));

        assertEquals("alias-2",
                     newAlias);
    }
}