package org.guvnor.structure.backend.repositories;

import java.util.Collections;
import java.util.Optional;

import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private RepositoryFactory repositoryFactory;

    @Spy
    ConfigurationFactoryImpl configurationFactory;

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

    @Test
    public void updateContributorsTest() {
        final Space space = new Space("alias");
        doReturn(space).when(repository).getSpace();
        doReturn("alias").when(repository).getAlias();

        final ConfigGroup configGroup = new ConfigGroup();
        configGroup.setName("alias");
        configGroup.addConfigItem(configurationFactory.newConfigItem("contributors", Collections.emptyList()));
        doReturn(Collections.singletonList(configGroup)).when(configurationService).getConfiguration(ConfigType.REPOSITORY, "alias");

        repositoryService.updateContributors(repository, Collections.singletonList(new Contributor("admin1", ContributorType.OWNER)));

        verify(configurationService).updateConfiguration(configGroup);
    }
}