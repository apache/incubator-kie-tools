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

package org.drools.workbench.screens.dtablexls.backend.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import javax.enterprise.event.Event;

import org.apache.commons.io.IOUtils;
import org.drools.template.parser.DecisionTableParseException;
import org.drools.workbench.screens.dtablexls.service.DecisionTableXLSConversionService;
import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.backend.util.CommentedOptionFactoryImpl;
import org.guvnor.common.services.backend.validation.GenericValidator;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@PrepareForTest(IOUtils.class)
@RunWith(PowerMockRunner.class)
public class DecisionTableXLSServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private CopyService copyService;

    @Mock
    private DeleteService deleteService;

    @Mock
    private RenameService renameService;

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Mock
    private DecisionTableXLSConversionService conversionService;

    @Mock
    private GenericValidator genericValidator;

    private CommentedOptionFactory commentedOptionFactory = new CommentedOptionFactoryImpl();

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private User user;

    @Mock
    private Path path;

    @Mock
    private InputStream inputstream;

    @Mock
    private OutputStream outputStream;

    @Captor
    private ArgumentCaptor<CommentedOption> commentedOptionArgumentCaptor;

    private final String sessionId = "123";
    private final String comment = "comment";

    private DecisionTableXLSServiceImpl service;

    @Before
    public void setup() throws IOException {
        when( authenticationService.getUser() ).thenReturn( user );
        when( user.getIdentifier() ).thenReturn( "user" );

        when( path.toURI() ).thenReturn( "file://p0/src/main/resources/dtable.xls" );
        when( inputstream.read( anyObject() ) ).thenReturn( -1 );
        when( ioService.newOutputStream( any( org.uberfire.java.nio.file.Path.class ),
                                         commentedOptionArgumentCaptor.capture() ) ).thenReturn( outputStream );
    }

    @Test
    public void testSessionInfoOnCreate() {
        this.service = getServiceWithValidationOverride( ( tempFile ) -> {
            //Do nothing; tests do not use a *real* XLS file
        } );

        service.create( path,
                        inputstream,
                        sessionId,
                        comment );
        assertCommentedOption();
    }

    @Test
    public void testSessionInfoOnSave() {
        this.service = getServiceWithValidationOverride( ( tempFile ) -> {
            //Do nothing; tests do not use a *real* XLS file
        } );

        service.save( path,
                      inputstream,
                      sessionId,
                      comment );
        assertCommentedOption();
    }

    @Test
    public void inputStreamShouldNotBeReused() throws IOException {
        this.service = getServiceWithValidationOverride( ( tempFile ) -> {
            //Do nothing; tests do not use a *real* XLS file
        } );

        mockStatic( IOUtils.class );

        service.create( path,
                        inputstream,
                        sessionId,
                        comment );

        verifyStatic( times( 1 ) );
        IOUtils.copy( eq( inputstream ),
                      any( OutputStream.class ) );
    }

    private void assertCommentedOption() {
        this.service = getServiceWithValidationOverride( ( tempFile ) -> {
            //Do nothing; tests do not use a *real* XLS file
        } );

        final CommentedOption commentedOption = commentedOptionArgumentCaptor.getValue();
        assertNotNull( commentedOption );
        assertEquals( "user",
                      commentedOption.getName() );
        assertEquals( "123",
                      commentedOption.getSessionId() );
    }

    @Test
    public void testInvalidTableNotCreated() throws IOException {
        testInvalidTable( ( s ) -> s.create( path, inputstream, sessionId, comment ) );
    }

    @Test
    public void testInvalidTableNotSaved() throws IOException {
        testInvalidTable( ( s ) -> s.save( path, inputstream, sessionId, comment ) );
    }

    private void testInvalidTable( Consumer<DecisionTableXLSServiceImpl> serviceConsumer ) throws IOException {
        this.service = getServiceWithValidationOverride( ( tempFile ) -> {
            // mock an invalid file
            Throwable t = new Throwable( "testing invalid xls dt creation" );
            throw new DecisionTableParseException( "DecisionTableParseException: " + t.getMessage(), t );
        } );

        mockStatic( IOUtils.class );
        when( IOUtils.copy( any( InputStream.class ), any( OutputStream.class ) ) ).thenReturn( 0 );
        try {
            serviceConsumer.accept( service );
        } catch ( RuntimeException e ) {
            // this is expected correct behavior
        }
        verify( ioService, never() ).newOutputStream( any( org.uberfire.java.nio.file.Path.class ), any( CommentedOption.class ) );
        verifyStatic( never() );
    }

    @Test(expected = DecisionTableParseException.class)
    public void testValidateNonexistentFile() {
        this.service = getServiceWithValidationOverride( null );

        service.validate( new File( "" ) );
    }

    @Test(expected = DecisionTableParseException.class)
    public void testValidateEmptyFile() throws IOException {
        this.service = getServiceWithValidationOverride( null );

        service.validate( File.createTempFile( "emptyxls", null ) );
    }

    @Test(expected = DecisionTableParseException.class)
    public void testValidateFileWithInvalidContent() throws IOException {
        this.service = getServiceWithValidationOverride( null );

        File tempFile = File.createTempFile( "emptyxls", null );
        try ( FileOutputStream tempFOS = new FileOutputStream( tempFile ) ) {
            IOUtils.write( "birdplane!", tempFOS );
            tempFOS.flush();
            service.validate( tempFile );
        }
    }

    @Test
    public void testValidateFileWithValidContent() throws IOException, URISyntaxException {
        this.service = getServiceWithValidationOverride( null );

        File tempFile = new File( this.getClass().getResource( "dummy.xls" ).toURI() );
        service.validate( tempFile );
    }

    private DecisionTableXLSServiceImpl getServiceWithValidationOverride( Consumer<File> validationOverride ) {
        return new DecisionTableXLSServiceImpl( ioService,
                                                copyService,
                                                deleteService,
                                                renameService,
                                                resourceOpenedEvent,
                                                conversionService,
                                                genericValidator,
                                                commentedOptionFactory,
                                                authenticationService ) {
            @Override
            void validate( final File tempFile ) {
                if ( validationOverride != null ) {
                    validationOverride.accept( tempFile );
                } else {
                    super.validate( tempFile );
                }
            }
        };
    }
}
