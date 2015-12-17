/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.backend;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import org.guvnor.common.services.project.model.GAV;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.model.ConnectionType;
import org.kie.workbench.common.screens.server.management.model.ContainerRef;
import org.kie.workbench.common.screens.server.management.model.ContainerStatus;
import org.kie.workbench.common.screens.server.management.model.ScannerStatus;
import org.kie.workbench.common.screens.server.management.model.ServerRef;
import org.kie.workbench.common.screens.server.management.model.impl.ContainerRefImpl;
import org.kie.workbench.common.screens.server.management.model.impl.ServerRefImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ServerReferenceStorageImplTest {

    private IOService ioServiceSpy;
    private XStream xs = new XStream();
    private ServerReferenceStorageImpl serverReferenceStorage;
    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();;

    @BeforeClass
    public static void setup() throws IOException {
        fileSystemTestingUtils.setup();
    }

    @AfterClass
    public static void cleanup() {
        fileSystemTestingUtils.cleanup();
    }

    @Before
    public void setUp() throws Exception {
        ioServiceSpy =spy( fileSystemTestingUtils.getIoService() );
        serverReferenceStorage = new ServerReferenceStorageImpl( ioServiceSpy, fileSystemTestingUtils.getFileSystem() );
    }

    private ContainerRefImpl createContainerServerRefImpl( String serverId,
                                                           String id ) {
        return new ContainerRefImpl( serverId, id, ContainerStatus.STOPPED,
                                     new GAV( "groupID", "artifactId", "version" ), ScannerStatus.CREATED, 1000l );
    }

    private ServerRefImpl createServerRefImpl( String serverId,
                                               String serverURL,
                                               String serverName ) {
        return new ServerRefImpl( serverId, serverURL, serverName,
                                  "username", "password", ContainerStatus.LOADING, ConnectionType.REMOTE,
                                  Collections.<String, String>emptyMap(), Collections.<ContainerRef>emptyList() );
    }

    private void assertServerRefWrite( ServerRef serverRef ) {
        Path path = serverReferenceStorage.buildPath( serverRef );
        verify( ioServiceSpy, atLeastOnce() ).write( path, xs.toXML( serverRef ) );
    }

    private void assertServerRefWriteMultipleCalls( ServerRef... serverRef ) {
        Path path = serverReferenceStorage.buildPath( serverRef[ 0 ] );

        ArgumentCaptor<String> argument = ArgumentCaptor.forClass( String.class );

        verify( ioServiceSpy, atLeastOnce() ).write( eq( path ), argument.capture() );

        List<String> values = argument.getAllValues();

        for ( int i = 0; i < serverRef.length; i++ ) {
            String xml = xs.toXML( serverRef[ i ] );
            String actualValue = values.get( i );
            assertTrue( actualValue.equalsIgnoreCase( xml ) );
        }
    }

    private void assertServerRefDelete( ServerRef serverRef ) {
        Path path = serverReferenceStorage.buildPath( serverRef );
        verify( ioServiceSpy, atLeastOnce() ).delete( path );
    }

    @Test
    public void testForceRegister() throws Exception {
        final ServerRef serverRef1 = createServerRefImpl( "server_id1", "server_url1", "my_server1" );
        assertFalse( serverReferenceStorage.exists( serverRef1 ) );
        serverReferenceStorage.forceRegister( serverRef1 );
        assertServerRefWrite( serverRef1 );
        assertTrue( serverReferenceStorage.exists( serverRef1 ) );
    }

    @Test
    public void testRegister() throws Exception {
        final ServerRef serverRef2 = createServerRefImpl( "server_id2", "server_url2", "my_server2" );
        serverReferenceStorage.register( serverRef2 );
        assertServerRefWrite( serverRef2 );
        assertTrue( serverReferenceStorage.exists( serverRef2 ) );
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterTwiceThrowsRuntimeException() throws Exception {
        final ServerRef serverRef3 = createServerRefImpl( "server_id3", "server_url3", "my_server3" );
        serverReferenceStorage.register( serverRef3 );
        serverReferenceStorage.register( serverRef3 );
    }

    @Test
    public void testListRegisteredServers() throws Exception {

        final ServerRef serverRef4 = createServerRefImpl( "server_id4", "server_url4", "my_server4" );
        final ServerRef serverRef5 = createServerRefImpl( "server_id5", "server_url5", "my_server5" );
        serverReferenceStorage.register( serverRef4 );
        serverReferenceStorage.register( serverRef5 );

        Collection<ServerRef> serverRefs = serverReferenceStorage.listRegisteredServers();
        assertTrue( serverRefs.contains( serverRef4 ) );
        assertTrue( serverRefs.contains( serverRef5 ) );
    }

    @Test
    public void testRegisterUnRegister() throws Exception {
        final ServerRef serverRef6 = createServerRefImpl( "server_id6", "server_url6", "my_server6" );

        serverReferenceStorage.register( serverRef6 );
        assertTrue( serverReferenceStorage.exists( serverRef6 ) );
        assertServerRefWrite( serverRef6 );
        serverReferenceStorage.unregister( serverRef6 );
        assertFalse( serverReferenceStorage.exists( serverRef6 ) );
        assertServerRefDelete( serverRef6 );
        serverReferenceStorage.register( serverRef6 );
        assertTrue( serverReferenceStorage.exists( serverRef6 ) );
    }

    @Test
    public void testLoadServerRef() throws Exception {
        String serverId = "server_id7";
        final ServerRef serverRef7 = createServerRefImpl( serverId, "server_url7", "my_server7" );

        assertNull( serverReferenceStorage.loadServerRef( serverId ) );
        serverReferenceStorage.register( serverRef7 );

        assertEquals( serverRef7, serverReferenceStorage.loadServerRef( serverId ) );

    }

    @Test
    public void testCreateContainer() throws Exception {
        String serverId = "server_id8";
        String containerId = "container1";
        final ServerRef serverRef8 = createServerRefImpl( serverId, "server_url8", "my_server8" );
        serverReferenceStorage.register( serverRef8 );

        ContainerRefImpl container1 = createContainerServerRefImpl( serverId, containerId );

        serverReferenceStorage.createContainer( container1 );

        ServerRef updateServerRef = serverReferenceStorage.loadServerRef( serverId );
        assertServerRefWriteMultipleCalls( serverRef8, updateServerRef );
        assertNotNull( updateServerRef.getContainerRef( containerId ) );

    }

    @Test
    public void testDeleteContainer() throws Exception {
        String serverId = "server_id9";
        String containerId = "container2";
        final ServerRef serverRef9 = createServerRefImpl( serverId, "server_url9", "my_server9" );
        serverReferenceStorage.register( serverRef9 );

        ContainerRefImpl container2 = createContainerServerRefImpl( serverId, containerId );

        serverReferenceStorage.createContainer( container2 );

        ServerRef updatedServerRefWithContainer = serverReferenceStorage.loadServerRef( serverId );
        assertNotNull( updatedServerRefWithContainer.getContainerRef( containerId ) );

        serverReferenceStorage.deleteContainer( serverId, containerId );

        ServerRef updatedServerRefDeletedContainer = serverReferenceStorage.loadServerRef( serverId );
        assertNull( updatedServerRefDeletedContainer.getContainerRef( containerId ) );
        assertServerRefWriteMultipleCalls( serverRef9, updatedServerRefWithContainer, updatedServerRefDeletedContainer );
    }

    @Test
    public void testUpdateContainer() throws Exception {
        String serverId = "server_id10";
        String containerId = "container3";
        final ServerRef serverRef10 = createServerRefImpl( serverId, "server_url10", "my_server10" );
        serverReferenceStorage.register( serverRef10 );
        ContainerRefImpl container3 = createContainerServerRefImpl( serverId, containerId );

        serverReferenceStorage.createContainer( container3 );
        ServerRef serverRefWithContainer = serverReferenceStorage.loadServerRef( serverId );
        ContainerRef containerRef = serverRefWithContainer.getContainerRef( containerId );

        assertEquals( container3.getPollInterval(), containerRef.getPollInterval() );
        assertEquals( container3.getReleasedId(), containerRef.getReleasedId() );

        serverReferenceStorage.updateContainer( serverId, containerId, containerRef.getPollInterval() + 1 );
        ServerRef serverRefWithContainerWithPollIntervalUpdated = serverReferenceStorage.loadServerRef( serverId );

        serverReferenceStorage.updateContainer( serverId, containerId, new GAV( "dummy", "dummy", "dummy" ) );

        ServerRef serverRefWithContainerUpdated = serverReferenceStorage.loadServerRef( serverId );
        containerRef = serverRefWithContainerUpdated.getContainerRef( containerId );
        assertEquals( new Long( container3.getPollInterval() + 1 ), containerRef.getPollInterval() );
        assertEquals( new GAV( "dummy", "dummy", "dummy" ), containerRef.getReleasedId() );

        assertServerRefWriteMultipleCalls( serverRef10, serverRefWithContainer, serverRefWithContainerWithPollIntervalUpdated, serverRefWithContainerUpdated );
    }

}
