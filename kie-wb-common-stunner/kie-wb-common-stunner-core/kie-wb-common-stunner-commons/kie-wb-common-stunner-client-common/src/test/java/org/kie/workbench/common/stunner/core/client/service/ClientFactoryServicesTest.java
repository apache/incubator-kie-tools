/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.service;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.service.FactoryService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClientFactoryServicesTest {

    @Mock ClientFactoryManager clientFactoryManager;
    @Mock FactoryService factoryService;
    @Mock Metadata metadata;
    private Caller<FactoryService> factoryServiceCaller;;

    private ClientFactoryService tested;

    @Before
    public void setup() throws Exception {
        this.factoryServiceCaller = new CallerMock<FactoryService>( factoryService );
        this.tested = new ClientFactoryService( clientFactoryManager, factoryServiceCaller );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDefinitionLocal() {
        String id = "id1";
        ServiceCallback<Object> callback = mock( ServiceCallback.class );
        Object def = mock( Object.class );
        when( clientFactoryManager.newDefinition( eq( id ) ) ).thenReturn( def );
        tested.newDefinition( id, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 1 ) ).newDefinition( eq( id ) );
        verify( clientFactoryManager, times( 0 ) ).newDefinition( any( Class.class ) );
        verify( factoryService, times( 0 ) ).newDefinition( anyString() );
        verify( factoryService, times( 0 ) ).newDefinition( any( Class.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDefinitionRemote() {
        String id = "id1";
        ServiceCallback<Object> callback = mock( ServiceCallback.class );
        Object def = mock( Object.class );
        when( clientFactoryManager.newDefinition( eq( id ) ) ).thenReturn( null );
        when( factoryService.newDefinition( eq( id ) ) ).thenReturn( def );
        tested.newDefinition( id, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 1 ) ).newDefinition( eq( id ) );
        verify( clientFactoryManager, times( 0 ) ).newDefinition( any( Class.class ) );
        verify( factoryService, times( 1 ) ).newDefinition( eq( id ) );
        verify( factoryService, times( 0 ) ).newDefinition( any( Class.class ) );
    }

    private class MyType { }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDefinitionByTpeLocal() {
        ServiceCallback<MyType> callback = mock( ServiceCallback.class );
        MyType def = mock( MyType.class );
        when( clientFactoryManager.newDefinition( eq( MyType.class ) ) ).thenReturn( def );
        tested.newDefinition( MyType.class, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 0 ) ).newDefinition( anyString() );
        verify( clientFactoryManager, times( 1 ) ).newDefinition( eq( MyType.class ) );
        verify( factoryService, times( 0 ) ).newDefinition( anyString() );
        verify( factoryService, times( 0 ) ).newDefinition( any( Class.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDefinitionByTpeRemote() {
        ServiceCallback<MyType> callback = mock( ServiceCallback.class );
        MyType def = mock( MyType.class );
        when( clientFactoryManager.newDefinition( eq( MyType.class ) ) ).thenReturn( null );
        when( factoryService.newDefinition( eq( MyType.class ) ) ).thenReturn( def );
        tested.newDefinition( MyType.class, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 0 ) ).newDefinition( anyString() );
        verify( clientFactoryManager, times( 1 ) ).newDefinition( eq( MyType.class ) );
        verify( factoryService, times( 0 ) ).newDefinition( anyString() );
        verify( factoryService, times( 1 ) ).newDefinition( eq( MyType.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewElementLocal() {
        String id = "id1";
        String defSetId = "defSet1";
        ServiceCallback<Element> callback = mock( ServiceCallback.class );
        Element def = mock( Element.class );
        when( clientFactoryManager.newElement( eq( id ), eq( defSetId ) ) ).thenReturn( def );
        tested.newElement( id, defSetId, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 1 ) ).newElement( eq( id ), eq( defSetId ) );
        verify( clientFactoryManager, times( 0 ) ).newElement( anyString(), any( Class.class ) );
        verify( factoryService, times( 0 ) ).newElement( anyString(), anyString() );
        verify( factoryService, times( 0 ) ).newElement( anyString(), any( Class.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewElementRemote() {
        String id = "id1";
        String defSetId = "defSet1";
        ServiceCallback<Element> callback = mock( ServiceCallback.class );
        Element def = mock( Element.class );
        when( clientFactoryManager.newElement( eq( id ), eq( defSetId ) ) ).thenReturn( null );
        when( factoryService.newElement( eq( id ), eq( defSetId ) ) ).thenReturn( def );
        tested.newElement( id, defSetId, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 1 ) ).newElement( eq( id ), eq( defSetId ) );
        verify( clientFactoryManager, times( 0 ) ).newElement( anyString(), any( Class.class ) );
        verify( factoryService, times( 1 ) ).newElement( eq( id ), eq( defSetId ) );
        verify( factoryService, times( 0 ) ).newElement( anyString(), any( Class.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewElementByTpeLocal() {
        String id = "id1";
        ServiceCallback<Element> callback = mock( ServiceCallback.class );
        Element def = mock( Element.class );
        when( clientFactoryManager.newElement( eq( id ), eq( MyType.class ) ) ).thenReturn( def );
        tested.newElement( id, MyType.class, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 0 ) ).newElement( anyString(), anyString() );
        verify( clientFactoryManager, times( 1 ) ).newElement( anyString(), eq( MyType.class ) );
        verify( factoryService, times( 0 ) ).newElement( anyString(), anyString() );
        verify( factoryService, times( 0 ) ).newElement( anyString(), any( Class.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewElementByTpeRemote() {
        String id = "id1";
        ServiceCallback<Element> callback = mock( ServiceCallback.class );
        Element def = mock( Element.class );
        when( clientFactoryManager.newElement( eq( id ), eq( MyType.class ) ) ).thenReturn( null );
        when( factoryService.newElement( eq( id ), eq( MyType.class ) ) ).thenReturn( def );
        tested.newElement( id, MyType.class, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 0 ) ).newElement( anyString(), anyString() );
        verify( clientFactoryManager, times( 1 ) ).newElement( anyString(), eq( MyType.class ) );
        verify( factoryService, times( 0 ) ).newElement( anyString(), anyString() );
        verify( factoryService, times( 1 ) ).newElement( anyString(), any( Class.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDiagramLocal() {
        String id = "id1";
        String name = "name1";
        ServiceCallback<Diagram> callback = mock( ServiceCallback.class );
        Diagram def = mock( Diagram.class );
        when( clientFactoryManager.newDiagram( eq( name ), eq( id ), any( Metadata.class ) ) ).thenReturn( def );
        tested.newDiagram( name, id, metadata, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 1 ) ).newDiagram( eq( name ), eq( id ), eq( metadata ) );
        verify( clientFactoryManager, times( 0 ) ).newDiagram( anyString(), any( Class.class ), any( Metadata.class ) );
        verify( factoryService, times( 0 ) ).newDiagram( anyString(), anyString(), any( Metadata.class ) );
        verify( factoryService, times( 0 ) ).newDiagram( anyString(), any( Class.class ), any( Metadata.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDiagramRemote() {
        String id = "id1";
        String name = "name1";
        ServiceCallback<Diagram> callback = mock( ServiceCallback.class );
        Diagram def = mock( Diagram.class );
        when( clientFactoryManager.newDiagram( eq( name ), eq( id ), any( Metadata.class ) ) ).thenReturn( null );
        when( factoryService.newDiagram( eq( name ), eq( id ), any( Metadata.class ) ) ).thenReturn( def );
        tested.newDiagram( name, id, metadata, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 1 ) ).newDiagram( eq( name ), eq( id ), any( Metadata.class ) );
        verify( clientFactoryManager, times( 0 ) ).newDiagram( anyString(), any( Class.class ), any( Metadata.class ) );
        verify( factoryService, times( 1 ) ).newDiagram( eq( name ), eq( id ), any( Metadata.class ) );
        verify( factoryService, times( 0 ) ).newDiagram( anyString(), any( Class.class ), any( Metadata.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDiagramByTpeLocal() {
        String name = "name1";
        ServiceCallback<Diagram> callback = mock( ServiceCallback.class );
        Diagram def = mock( Diagram.class );
        when( clientFactoryManager.newDiagram( eq( name ), eq( MyType.class ), any( Metadata.class ) ) ).thenReturn( def );
        tested.newDiagram( name, MyType.class, metadata, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 0 ) ).newDiagram( anyString(), anyString(), any( Metadata.class ) );
        verify( clientFactoryManager, times( 1 ) ).newDiagram( anyString(), eq( MyType.class ), any( Metadata.class ) );
        verify( factoryService, times( 0 ) ).newDiagram( anyString(), anyString(), any( Metadata.class ) );
        verify( factoryService, times( 0 ) ).newDiagram( anyString(), any( Class.class ), any( Metadata.class ) );
    }

    @Test
    @SuppressWarnings( "unchecked" )
    public void testNewDiagramByTpeRemote() {
        String name = "name1";
        ServiceCallback<Diagram> callback = mock( ServiceCallback.class );
        Diagram def = mock( Diagram.class );
        when( clientFactoryManager.newDiagram( eq( name ), eq( MyType.class ), any( Metadata.class ) ) ).thenReturn( null );
        when( factoryService.newDiagram( eq( name ), eq( MyType.class ), any( Metadata.class ) ) ).thenReturn( def );
        tested.newDiagram( name, MyType.class, metadata, callback );
        verify( callback, times( 1 ) ).onSuccess( eq( def ) );
        verify( clientFactoryManager, times( 0 ) ).newDiagram( anyString(), anyString(), any( Metadata.class ) );
        verify( clientFactoryManager, times( 1 ) ).newDiagram( anyString(), eq( MyType.class ), any( Metadata.class ) );
        verify( factoryService, times( 0 ) ).newDiagram( anyString(), anyString(), any( Metadata.class ) );
        verify( factoryService, times( 1 ) ).newDiagram( anyString(), any( Class.class ), any( Metadata.class ) );
    }

}
