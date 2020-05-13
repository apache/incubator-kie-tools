/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.backend.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableEditorGraphModel;
import org.drools.workbench.screens.guided.dtable.service.GuidedDecisionTableGraphEditorService;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableGraphResourceTypeDefinition;
import org.drools.workbench.screens.guided.dtable.type.GuidedDTableResourceTypeDefinition;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidedDecisionTableEditorGraphDeleteHelperTest {

    @Mock
    private IOService ioService;

    @Mock
    private GuidedDecisionTableGraphEditorService dtableGraphService;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private FileSystemProvider fileSystemProvider;

    @Mock
    private BasicFileAttributes basicFileAttributes;

    @Mock
    private Path path;

    private List<org.uberfire.java.nio.file.Path> paths = new ArrayList<>();
    private GuidedDTableResourceTypeDefinition dtableType = new GuidedDTableResourceTypeDefinition(new Decision());
    private GuidedDTableGraphResourceTypeDefinition dtableGraphType = new GuidedDTableGraphResourceTypeDefinition(new Decision());

    private GuidedDecisionTableEditorGraphDeleteHelper helper;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        paths.clear();

        final GuidedDecisionTableEditorGraphDeleteHelper wrapped = new GuidedDecisionTableEditorGraphDeleteHelper( ioService,
                                                                                                                   dtableType,
                                                                                                                   dtableGraphType,
                                                                                                                   dtableGraphService,
                                                                                                                   commentedOptionFactory );
        helper = spy( wrapped );

        when( ioService.newDirectoryStream( any( org.uberfire.java.nio.file.Path.class ),
                                            any( DirectoryStream.Filter.class ) ) ).thenAnswer( ( invocation ) -> {
            final List<org.uberfire.java.nio.file.Path> allPaths = new ArrayList<>( paths );
            final DirectoryStream.Filter filter = (DirectoryStream.Filter) invocation.getArguments()[ 1 ];
            return new MockDirectoryStream( allPaths.stream().filter( filter::accept ).collect( Collectors.toList() ) );
        } );

        when( fileSystem.provider() ).thenReturn( fileSystemProvider );
        when( fileSystemProvider.readAttributes( any( org.uberfire.java.nio.file.Path.class ),
                                                 any( Class.class ) ) ).thenReturn( basicFileAttributes );
        when( basicFileAttributes.isRegularFile() ).thenReturn( true );
    }

    @Test
    public void checkDoesSupportGuidedDecisionTables() {
        when( path.getFileName() ).thenReturn( "a-file." + dtableType.getSuffix() );
        assertTrue( helper.supports( path ) );
    }

    @Test
    public void checkDoesNotSupportNonGuidedDecisionTables() {
        when( path.getFileName() ).thenReturn( "a-file.txt" );
        assertFalse( helper.supports( path ) );
    }

    @Test
    public void checkRemoveReferencesNoFiles() {
        when( path.getFileName() ).thenReturn( "dtable.gdst" );
        when( path.toURI() ).thenReturn( "file://test/dtable.gdst" );

        helper.postProcess( path );

        verify( helper,
                never() ).updateGraphReferences( any( Path.class ),
                                                 any( Path.class ) );
    }

    @Test
    public void checkRemoveReferencesWithDecisionTableGraphs() throws URISyntaxException {
        final org.uberfire.java.nio.file.Path dtGraphPath = mock( org.uberfire.java.nio.file.Path.class );
        when( dtGraphPath.getFileName() ).thenReturn( mock( org.uberfire.java.nio.file.Path.class ) );
        when( dtGraphPath.toUri() ).thenReturn( new URI( "file://test/dtable-set." + dtableGraphType.getSuffix() ) );
        when( dtGraphPath.getFileSystem() ).thenReturn( fileSystem );
        paths.add( dtGraphPath );

        when( path.getFileName() ).thenReturn( "dtable.gdst" );
        when( path.toURI() ).thenReturn( "file://test/dtable.gdst" );

        final GuidedDecisionTableEditorGraphModel model = new GuidedDecisionTableEditorGraphModel();
        model.getEntries().add( new GuidedDecisionTableEditorGraphModel.GuidedDecisionTableGraphEntry( path,
                                                                                                       path ) );
        when( dtableGraphService.load( any( Path.class ) ) ).thenReturn( model );

        helper.postProcess( path );

        verify( helper,
                times( 1 ) ).updateGraphReferences( any( Path.class ),
                                                    any( Path.class ) );

        final ArgumentCaptor<org.uberfire.java.nio.file.Path> dtGraphPathCaptor = ArgumentCaptor.forClass( org.uberfire.java.nio.file.Path.class );
        final ArgumentCaptor<String> modelXmlCaptor = ArgumentCaptor.forClass( String.class );

        verify( ioService,
                times( 1 ) ).write( dtGraphPathCaptor.capture(),
                                    modelXmlCaptor.capture(),
                                    any( CommentedOption.class ) );

        final org.uberfire.java.nio.file.Path dtGraphPath2 = dtGraphPathCaptor.getValue();
        assertEquals( dtGraphPath.toUri().getPath(),
                      dtGraphPath2.toUri().getPath() );

        final String modelXml = modelXmlCaptor.getValue();
        final GuidedDecisionTableEditorGraphModel newModel = GuidedDTGraphXMLPersistence.getInstance().unmarshal( modelXml );
        assertEquals( 0,
                      newModel.getEntries().size() );
    }

    @Test
    public void checkRemoveReferencesWithoutDecisionTableGraphs() throws URISyntaxException {
        final org.uberfire.java.nio.file.Path dtPath = mock( org.uberfire.java.nio.file.Path.class );
        when( dtPath.getFileName() ).thenReturn( mock( org.uberfire.java.nio.file.Path.class ) );
        when( dtPath.toUri() ).thenReturn( new URI( "file://test/dtable." + dtableType.getSuffix() ) );
        when( dtPath.getFileSystem() ).thenReturn( fileSystem );
        paths.add( dtPath );

        when( path.getFileName() ).thenReturn( "dtable.gdst" );
        when( path.toURI() ).thenReturn( "file://test/dtable.gdst" );

        helper.postProcess( path );

        verify( helper,
                never() ).updateGraphReferences( any( Path.class ),
                                                 any( Path.class ) );
    }

}
