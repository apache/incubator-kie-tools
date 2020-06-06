package org.guvnor.structure.backend.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.assertj.core.api.Assertions;
import org.guvnor.common.services.project.events.RepositoryContributorsUpdatedEvent;
import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageRegistryImpl;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.RepositoryConfiguration;
import org.guvnor.structure.organizationalunit.config.RepositoryInfo;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageBatch;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.spaces.Space;
import org.uberfire.spaces.SpacesAPI;

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

    @Mock
    private GitMetadataStore gitMetadataStore;

    @Mock
    private OrganizationalUnitService organizationalUnitService;

    @Mock
    private SpacesAPI spacesAPI;

    @Mock
    private PasswordService passwordService;

    @Mock
    private RepositoryFactory repositoryFactory;

    @InjectMocks
    @Spy
    private RepositoryServiceImpl repositoryService;

    @Captor
    private ArgumentCaptor<RepositoryContributorsUpdatedEvent> captor;

    @Captor
    private ArgumentCaptor<RepositoryInfo> configCaptor;

    @Before
    public void setUp() {
        doAnswer(invocationOnMock -> null).when(gitMetadataStore).delete(anyString());
    }

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
                               "Test space",
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

    @Test
    public void testDoRemoveInOrder() {

        Consumer<Repository> notification = mock(Consumer.class);
        OrganizationalUnit orgUnit = mock(OrganizationalUnit.class);
        String alias = "alias";
        Optional<RepositoryInfo> repositoryConfig = Optional.of(mock(RepositoryInfo.class));

        doAnswer(invocationOnMock -> null).when(repositoryService).close(any());
        when(configuredRepositories.getRepositoryByRepositoryAlias(any(), anyString())).thenReturn(repository);
        when(repository.getAlias()).thenReturn(alias);
        when(orgUnit.getRepositories()).thenReturn(Collections.singletonList(repository));

        InOrder inOrder = inOrder(this.organizationalUnitService, notification);

        this.repositoryService.doRemoveRepository(orgUnit, alias, repositoryConfig, notification, false);

        inOrder.verify(this.organizationalUnitService).removeRepository(any(), any());
        inOrder.verify(notification).accept(repository);
    }

    @Test
    public void testCreateRepositoryConfiguration() {

        final String space = "space";
        final String scheme = "git";
        final String repoName = "test";
        final String userName = "user";
        final String password = "pass";

        OrganizationalUnit orgUnit = mock(OrganizationalUnit.class);
        when(orgUnit.getName()).thenReturn(space);

        final SpaceConfigStorage spaceConfigStorage = mock(SpaceConfigStorage.class);
        when(registry.getBatch(anyString())).thenReturn(new SpaceConfigStorageRegistryImpl.SpaceStorageBatchImpl(spaceConfigStorage));

        when(spacesAPI.getSpace(anyString())).thenAnswer((Answer<Space>) invocationOnMock -> new Space(invocationOnMock.getArguments()[0].toString()));
        when(passwordService.encrypt(anyString())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0].toString());

        RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
        configurations.setUserName(userName);
        configurations.setPassword(password);
        configurations.setOrigin("https://github.com/kiegroup/appformer.git");

        repositoryService.createRepository(orgUnit, scheme, repoName, configurations, Collections.emptyList());

        verify(passwordService, times(1)).encrypt(eq(password));

        ArgumentCaptor<RepositoryInfo> captor = ArgumentCaptor.forClass(RepositoryInfo.class);
        verify(repositoryFactory).newRepository(captor.capture());

        RepositoryInfo info = captor.getValue();

        Assertions.assertThat(info)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", repoName)
                .hasFieldOrPropertyWithValue("deleted", false);

        assertEquals(scheme, info.getScheme());
        assertEquals(space, info.getSpace());
        assertEquals(userName, info.getConfiguration().get(String.class, "username"));
        assertEquals(password, info.getConfiguration().get(String.class, "secure:password"));
    }
}