package org.uberfire.backend.server.repositories;

import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.repositories.RepositoryService;
import org.uberfire.backend.server.config.SystemRepositoryChangedEvent;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class RepositoryServiceTest {

    private static final String SCHEME = "git";
    private static final String UF_PLAYGROUND_ALIAS = "uf-playground";

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.uberfire.backend.server.cluster")
                .addPackage("org.uberfire.backend.server.config")
                .addPackage("org.uberfire.backend.server.deployment")
                .addPackage("org.uberfire.backend.server.io")
                .addPackage("org.uberfire.backend.server.organizationalunit")
                .addPackage("org.uberfire.backend.server.repositories")
                .addPackage("org.uberfire.backend.server.repositories.git")
                .addPackage("org.uberfire.backend.server.security")
                .addPackage("org.uberfire.backend.server.util")
                .addPackage("org.uberfire.backend")
                .addPackage("org.uberfire.backend.server")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    @Inject
    private RepositoryService repositoryService;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @org.uberfire.backend.server.config.Repository
    private Event<SystemRepositoryChangedEvent> repoChangedEvent;

    @Before
    public void configure() {
        assertNotNull(ioService);
        assertNotNull(repositoryService);

        repositoryService.removeRepository(UF_PLAYGROUND_ALIAS);
    }

    @Test
    public void testGetRepositories() {

        Collection<Repository> repositories = repositoryService.getRepositories();
        assertNotNull(repositories);
        assertEquals(0, repositories.size());
    }

    @Test
    public void testCreateRepository() throws Exception {

        final Repository ufRepo = repositoryService.createRepository( SCHEME,
                UF_PLAYGROUND_ALIAS,
                new HashMap<String, Object>() );

        assertNotNull( ufRepo );

        Repository ufRepoByRoot = repositoryService.getRepository(ufRepo.getRoot());
        assertNotNull(ufRepoByRoot);

        Repository ufRepoByAlias = repositoryService.getRepository(UF_PLAYGROUND_ALIAS);
        assertNotNull(ufRepoByAlias);

        // check current branch details
        assertEquals("master", ufRepo.getCurrentBranch());
        assertEquals(1, ufRepo.getBranches().size());

        repositoryService.removeRepository(UF_PLAYGROUND_ALIAS);
    }

    @Test
    public void testCreateRepositoryWithBranch() throws Exception {

        final Repository ufRepo = repositoryService.createRepository( SCHEME,
                UF_PLAYGROUND_ALIAS,
                new HashMap<String, Object>() );

        assertNotNull( ufRepo );

        Repository ufRepoByRoot = repositoryService.getRepository(ufRepo.getRoot());
        assertNotNull(ufRepoByRoot);

        Repository ufRepoByAlias = repositoryService.getRepository(UF_PLAYGROUND_ALIAS);
        assertNotNull(ufRepoByAlias);

        // check current branch details
        assertEquals("master", ufRepo.getCurrentBranch());
        assertEquals(1, ufRepo.getBranches().size());

        // create asset on new branch
        final Path path2 = ioService.get(URI.create("git://user_branch@" + UF_PLAYGROUND_ALIAS + "/myfile2.txt"));
        final OutputStream outStream2 = ioService.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        // fire event to reload repositories
        repoChangedEvent.fire(new SystemRepositoryChangedEvent());

        Repository ufRepoUpdated = repositoryService.getRepository(UF_PLAYGROUND_ALIAS);
        assertNotNull(ufRepoUpdated);

        assertEquals("master", ufRepoUpdated.getCurrentBranch());
        assertEquals(2, ufRepoUpdated.getBranches().size());

        repositoryService.removeRepository(UF_PLAYGROUND_ALIAS);
    }

    @Test
    public void testChangeRepositoryBranch() throws Exception {

        final Repository ufRepo = repositoryService.createRepository( SCHEME,
                UF_PLAYGROUND_ALIAS,
                new HashMap<String, Object>() );

        assertNotNull( ufRepo );

        Repository ufRepoByRoot = repositoryService.getRepository(ufRepo.getRoot());
        assertNotNull(ufRepoByRoot);

        Repository ufRepoByAlias = repositoryService.getRepository(UF_PLAYGROUND_ALIAS);
        assertNotNull(ufRepoByAlias);

        // check current branch details
        assertEquals("master", ufRepo.getCurrentBranch());
        assertEquals(1, ufRepo.getBranches().size());

        // create asset on new branch
        final Path path2 = ioService.get(URI.create("git://user_branch@" + UF_PLAYGROUND_ALIAS + "/myfile2.txt"));
        final OutputStream outStream2 = ioService.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        // fire event to reload repositories
        repoChangedEvent.fire(new SystemRepositoryChangedEvent());

        Repository ufRepoUpdated = repositoryService.getRepository(UF_PLAYGROUND_ALIAS);
        assertNotNull(ufRepoUpdated);

        assertEquals("master", ufRepoUpdated.getCurrentBranch());
        assertEquals(2, ufRepoUpdated.getBranches().size());

        // now let's change the branch
        HashMap<String, Object> configUpdate = new HashMap<String, Object>();
        configUpdate.put("branch", "user_branch");

        Repository onUserBranchUFRepo = repositoryService.updateRepository(ufRepoUpdated, configUpdate);
        assertNotNull(onUserBranchUFRepo);

        assertEquals("user_branch", onUserBranchUFRepo.getCurrentBranch());
        assertEquals(2, onUserBranchUFRepo.getBranches().size());

        repositoryService.removeRepository(UF_PLAYGROUND_ALIAS);
    }

    @Test
    public void testCreateWriteToRemoveRepository() throws Exception {

        final Repository ufRepo = repositoryService.createRepository( SCHEME,
                UF_PLAYGROUND_ALIAS,
                new HashMap<String, Object>() );

        assertNotNull( ufRepo );

        Thread.sleep( 2000 ); //wait for indexing?

        ioService.write( Paths.convert(ufRepo.getRoot()).resolve( "new_file.txt" ), "some new content" );

        assertEquals( "some new content", ioService.readAllString( Paths.convert( ufRepo.getRoot() ).resolve( "new_file.txt" ) ) );

        repositoryService.removeRepository(UF_PLAYGROUND_ALIAS);
    }
}
