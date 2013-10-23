package org.uberfire.backend.server.repositories;

import java.util.HashMap;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.repositories.Repository;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class RepositoryCreationTest {

    private BeanManager beanManager;
    private RepositoryServiceImpl repositoryService;

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

//        Instantiate Paths used in tests for Path conversion
        final Bean repoService = (Bean) beanManager.getBeans( RepositoryServiceImpl.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( repoService );

        repositoryService = (RepositoryServiceImpl) beanManager.getReference( repoService,
                                                                              RepositoryServiceImpl.class,
                                                                              cc );

    }

    @Test
    public void test() {
        repositoryService.removeRepository( "myrepo" );
        final Repository repo = repositoryService.createRepository( "git", "myrepo", new HashMap<String, Object>() );
        assertNotNull( repo );
        assertEquals( repo, repositoryService.getRepository( "myrepo" ) );
        repositoryService.removeRepository( "myrepo" );
        assertNull( repositoryService.getRepository( "myrepo" ) );
        final Repository sameRepo = repositoryService.createRepository( "git", "myrepo", new HashMap<String, Object>() );
        assertEquals( sameRepo, repositoryService.getRepository( "myrepo" ) );
        repositoryService.removeRepository( "myrepo" );
        assertNull( repositoryService.getRepository( "myrepo" ) );
    }
}
