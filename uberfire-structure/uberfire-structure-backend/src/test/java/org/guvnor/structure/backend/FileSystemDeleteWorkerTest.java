package org.guvnor.structure.backend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.guvnor.structure.backend.organizationalunit.config.SpaceConfigStorageImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;
import org.uberfire.spaces.Space;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileSystemDeleteWorkerTest {

    private FileSystemDeleteWorker worker;

    @Mock
    private IOService ioService;

    @Mock
    private OrganizationalUnitService ouService;

    @Mock
    private RepositoryService repoService;

    @Mock
    private FileSystem systemFs;

    @Mock
    private SpaceConfigStorageRegistry registry;

    @Mock
    private Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent;

    @Mock
    private ConfigurationService configurationService;

    @Before
    public void setUp() throws IOException {

        this.worker = spy(new FileSystemDeleteWorker(this.ioService,
                                                     this.ouService,
                                                     this.repoService,
                                                     this.systemFs,
                                                     this.registry,
                                                     this.removeOrganizationalUnitEvent,
                                                     this.configurationService));

        doAnswer(invocation -> null).when(ioService).delete(any());
        doAnswer(invocation -> null).when(worker).removeRepository(any());
        doAnswer(invocation -> null).when(worker).delete(any());
        doAnswer(invocation -> null).when(removeOrganizationalUnitEvent).fire(any());
    }

    @Test
    public void testRemoveSpaceDirectory() throws IOException {

        JGitPathImpl configPath = mock(JGitPathImpl.class,
                                       RETURNS_DEEP_STUBS);

        Space space = mock(Space.class);

        Path deletePath = mock(Path.class,
                               RETURNS_DEEP_STUBS);

        when(configPath.getFileSystem().getPath(anyString())).thenReturn(deletePath);

        SpaceConfigStorageImpl configStorage = mock(SpaceConfigStorageImpl.class);
        when(configStorage.getPath()).thenReturn(configPath);

        when(registry.get(any())).thenReturn(configStorage);

        doReturn(Collections.singletonList(mock(Repository.class)))
                .when(this.repoService)
                .getAllRepositories(eq(space),
                                    eq(true));

        doReturn(configPath).when(ioService).get(any());

        File spacePathFile = mock(File.class);
        doReturn(spacePathFile).when(worker).getSpacePath(any());

        this.worker.removeSpaceDirectory(space);

        verify(this.worker,
               times(1)).removeRepository(any());

        verify(ioService).deleteIfExists(deletePath);
        verify(this.worker).delete(spacePathFile);
    }

    @Test
    public void testRemoveAllDeletedSpaces() {

        doAnswer(invocation -> null).when(worker).removeSpaceDirectory(any());

        OrganizationalUnit ou1 = mock(OrganizationalUnit.class);
        doReturn(mock(Space.class)).when(ou1).getSpace();
        OrganizationalUnit ou2 = mock(OrganizationalUnit.class);
        doReturn(mock(Space.class)).when(ou2).getSpace();

        List<OrganizationalUnit> orgUnits = Arrays.asList(ou1,
                                                          ou2);
        doReturn(orgUnits).when(this.ouService).getAllDeletedOrganizationalUnit();

        this.worker.removeAllDeletedSpaces();

        verify(this.worker,
               times(1)).removeSpaceDirectory(ou1.getSpace());
        verify(this.worker,
               times(1)).removeSpaceDirectory(ou2.getSpace());
        verify(this.removeOrganizationalUnitEvent,
               times(1)).fire(any());
    }

    @Test
    public void testRemoveZeroDeletedSpaces() {

        doAnswer(invocation -> null).when(worker).removeSpaceDirectory(any());
        doReturn(new ArrayList<>()).when(this.ouService).getAllDeletedOrganizationalUnit();
        this.worker.removeAllDeletedSpaces();
        verify(this.worker,
               never()).removeSpaceDirectory(any());
        verify(this.removeOrganizationalUnitEvent,
               never()).fire(any());
    }

    @Test
    public void testRemoveAllDeletedRepository() {

        Repository repo1 = mock(Repository.class);
        Repository repo2 = mock(Repository.class);
        Repository repo3 = mock(Repository.class);
        Repository repo4 = mock(Repository.class);

        Space space1 = mock(Space.class);
        Space space2 = mock(Space.class);
        OrganizationalUnit ou1 = mock(OrganizationalUnit.class);
        OrganizationalUnit ou2 = mock(OrganizationalUnit.class);

        doReturn(space1).when(ou1).getSpace();
        doReturn(space2).when(ou2).getSpace();

        doReturn(Arrays.asList(ou1,
                               ou2)).when(this.ouService).getAllOrganizationalUnits(eq(false), any());

        doReturn(Arrays.asList(repo1,
                               repo2)).when(this.repoService).getAllDeletedRepositories(eq(space1));

        doReturn(Arrays.asList(repo3,
                               repo4)).when(this.repoService).getAllDeletedRepositories(eq(space2));

        Branch branch = mock(Branch.class);
        Repository repo = mock(Repository.class);
        doReturn(Optional.of(branch)).when(repo).getDefaultBranch();

        this.worker.removeAllDeletedRepositories();

        verify(worker,
               times(1)).removeRepository(repo1);
        verify(worker,
               times(1)).removeRepository(repo2);
        verify(worker,
               times(1)).removeRepository(repo3);
        verify(worker,
               times(1)).removeRepository(repo4);
    }

    @Test
    public void testMonitoringEnabled(){
       when(worker.isDeleteWorkerEnabled()).thenReturn(false);
        worker.doRemove();
        verify(worker, never()).removeAllDeletedSpaces();
    }
}