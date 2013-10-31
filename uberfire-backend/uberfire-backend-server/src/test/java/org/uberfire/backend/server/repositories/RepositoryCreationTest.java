package org.uberfire.backend.server.repositories;

import java.util.HashMap;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;

import static junit.framework.Assert.*;

@Ignore
public class RepositoryCreationTest {

    private static final String SCHEME = "git";
    private static final String DROOLS_WB_PLAYGROUND_ALIAS = "uf-playground";
    private static final String DROOLS_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/guvnorng-playground.git";

    private static final String JBPM_WB_PLAYGROUND_ALIAS = "jbpm-playground";
    private static final String JBPM_WB_PLAYGROUND_ORIGIN = "https://github.com/guvnorngtestuser1/jbpm-console-ng-playground-kjar.git";

    private BeanManager beanManager;
    private RepositoryServiceImpl repositoryService;
    private IOService ioService;

    @Before
    public void setUp() throws Exception {
        //Bootstrap WELD container
        StartMain startMain = new StartMain( new String[ 0 ] );
        beanManager = startMain.go().getBeanManager();

        final Bean repoService = (Bean) beanManager.getBeans( RepositoryServiceImpl.class ).iterator().next();
        final Bean ioServiceBean = (Bean) beanManager.getBeans( "ioStrategy" ).iterator().next();

        final CreationalContext cc = beanManager.createCreationalContext( repoService );

        repositoryService = (RepositoryServiceImpl) beanManager.getReference( repoService,
                                                                              RepositoryServiceImpl.class,
                                                                              cc );

        ioService = (IOService) beanManager.getReference( ioServiceBean,
                                                          IOService.class,
                                                          cc );

    }

    @Test
    public void test() throws InterruptedException {
        final Repository drools = repositoryService.createRepository( SCHEME,
                                                                      DROOLS_WB_PLAYGROUND_ALIAS,
                                                                      new HashMap<String, Object>() {{
                                                                          put( "origin", DROOLS_WB_PLAYGROUND_ORIGIN );
                                                                      }} );

        final Repository jbpm = repositoryService.createRepository( SCHEME,
                                                                    JBPM_WB_PLAYGROUND_ALIAS,
                                                                    new HashMap<String, Object>() {{
                                                                        put( "origin", JBPM_WB_PLAYGROUND_ORIGIN );
                                                                    }} );

        assertNotNull( drools );
        assertNotNull( jbpm );

        Thread.sleep( 2000 ); //wait for indexing?

        ioService.write( Paths.convert( drools.getRoot() ).resolve( "new_file.txt" ), "some new content" );

        assertEquals( "some new content\n", ioService.readAllString( Paths.convert( drools.getRoot() ).resolve( "new_file.txt" ) ) );

        Thread.sleep( 2000 );//wait a bit more for indexing?

        ioService.write( Paths.convert( drools.getRoot() ).resolve( "new_file.txt" ), "some new content 2" );

        assertEquals( "some new content 2\n", ioService.readAllString( Paths.convert( drools.getRoot() ).resolve( "new_file.txt" ) ) );

        repositoryService.removeRepository( DROOLS_WB_PLAYGROUND_ALIAS );
        repositoryService.removeRepository( JBPM_WB_PLAYGROUND_ALIAS );
    }
}
