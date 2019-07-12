package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.events.RepositoryContributorsUpdatedEvent;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageBatch;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
    private SpaceConfigStorageRegistry registry;

    @Mock
    private Event<RepositoryContributorsUpdatedEvent> updatedEvent;

    @InjectMocks
    @Spy
    private RepositoryServiceImpl repositoryService;

    @Captor
    private ArgumentCaptor<RepositoryContributorsUpdatedEvent> captor;

    @Captor
    private ArgumentCaptor<RepositoryInfo> configCaptor;

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
                                                                   eq("alias"),
                                                                   eq(true))).thenReturn(repository);
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
                                                                   eq("alias"),
                                                                   eq(true))).thenReturn(repository);
        when(configuredRepositories.getRepositoryByRepositoryAlias(any(),
                                                                   eq("alias-1"),
                                                                   eq(true))).thenReturn(repository);
        doReturn(Optional.of(mock(Branch.class))).when(repository).getDefaultBranch();
        doReturn("alias").when(repository).getAlias();
        String newAlias = repositoryService.createFreshRepositoryAlias("alias",
                                                                       new Space("alias"));

        assertEquals("alias-2",
                     newAlias);
    }

    @Test
    public void updateContributorsTest() {

        final Space space = new Space("space");
        doReturn(space).when(repository).getSpace();
        doReturn("alias").when(repository).getAlias();

        doReturn(repository).when(configuredRepositories).getRepositoryByRepositoryAlias(any(),
                                                                                         any());

        final SpaceConfigStorage spaceConfigStorage = mock(SpaceConfigStorage.class);
        doReturn(new SpaceInfo("space",
                               "defaultGroupId",
                               Collections.emptyList(),
                               new ArrayList<>(Arrays.asList(new RepositoryInfo("alias",
                                                                                false,
                                                                                new RepositoryConfiguration()))),
                               Collections.emptyList())).when(spaceConfigStorage).loadSpaceInfo();
        doReturn(true)
                .when(spaceConfigStorage).isInitialized();

        when(registry.get(anyString())).thenReturn(spaceConfigStorage);
        when(registry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));

        String username = "admin1";
        repositoryService.updateContributors(repository,
                                             Collections.singletonList(new Contributor(username,
                                                                                       ContributorType.OWNER)));

        verify(updatedEvent).fire(captor.capture());
        assertEquals("alias",
                     captor.getValue().getRepository().getAlias());
        assertEquals("space",
                     captor.getValue().getRepository().getSpace().getName());
        verify(repositoryService).saveRepositoryConfig(eq("space"),
                                                       configCaptor.capture());

        assertEquals(username,
                     configCaptor.getValue().getContributors().get(0).getUsername());
        assertEquals(ContributorType.OWNER,
                     configCaptor.getValue().getContributors().get(0).getType());

        verify(spaceConfigStorage).startBatch();
        verify(spaceConfigStorage).saveSpaceInfo(any());
        verify(spaceConfigStorage).endBatch();
    }
}