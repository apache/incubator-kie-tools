/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.dtablexls.backend.server;

import org.jboss.weld.environment.se.StartMain;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.commons.java.nio.fs.file.SimpleFileSystemProvider;
import org.kie.guvnor.dtablexls.service.DecisionTableXLSService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


public class DRLTextEditorServiceImplTest {
    private BeanManager beanManager;
    private Paths paths;
    private final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();
    
    @Before
    public void setUp() throws Exception {
        StartMain startMain = new StartMain(new String[0]);
        beanManager = startMain.go().getBeanManager();
        
        final Bean pathsBean = (Bean) beanManager.getBeans( Paths.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( pathsBean );
        paths = (Paths) beanManager.getReference( pathsBean,
                                                  Paths.class,
                                                  cc );
        
        //Ensure URLs use the default:// scheme
        fs.forceAsDefault();
    }
    
    @Test
    @Ignore //TODO {porcelli} have no idea why weld can't start container =/
    public void testCopyAndRenameAndDelete() throws Exception {
        final Bean drlTextEditorServiceBean = (Bean) beanManager.getBeans( DecisionTableXLSService.class ).iterator().next();
        final CreationalContext cc = beanManager.createCreationalContext( drlTextEditorServiceBean );
        final DecisionTableXLSService drlTextEditorService = (DecisionTableXLSService) beanManager.getReference( drlTextEditorServiceBean,
                DecisionTableXLSService.class, cc );
               
        //Copy
        drlTextEditorService.copy(makePath( "/ProjectStructureValid/src/main/resources/org/kie/test/rule1.drl"), "copiedFromRule1.drl", "copied");        
        URL testUrl = this.getClass().getResource( "/ProjectStructureValid/src/main/resources/org/kie/test/copiedFromRule1.drl" );
        assertNotNull(testUrl);
        
        //Rename
        drlTextEditorService.rename(makePath( "/ProjectStructureValid/src/main/resources/org/kie/test/copiedFromRule1.drl"), "renamedFromRule1.drl", "renamed");        
        testUrl = this.getClass().getResource( "/ProjectStructureValid/src/main/resources/org/kie/test/renamedFromRule1.drl" );
        assertNotNull(testUrl);
        
        //Delete
        drlTextEditorService.delete(makePath( "/ProjectStructureValid/src/main/resources/org/kie/test/renamedFromRule1.drl"), "deleted");
        testUrl = this.getClass().getResource( "/ProjectStructureValid/src/main/resources/org/kie/test/renamedFromRule1.drl" );
        assertNull(testUrl);
    }
    
    private Path makePath( final String url ) throws URISyntaxException {
        final URL testUrl = this.getClass().getResource( url );
        final org.kie.commons.java.nio.file.Path testNioPath = fs.getPath( testUrl.toURI() );
        final Path testPath = paths.convert( testNioPath );
        return testPath;
    }


}
