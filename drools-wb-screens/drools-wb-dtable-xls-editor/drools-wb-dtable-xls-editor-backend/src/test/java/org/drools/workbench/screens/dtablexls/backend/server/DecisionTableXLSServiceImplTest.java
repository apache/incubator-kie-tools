/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.dtablexls.backend.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.event.Event;

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
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.CopyService;
import org.uberfire.ext.editor.commons.service.DeleteService;
import org.uberfire.ext.editor.commons.service.RenameService;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
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

    private ExtendedDecisionTableXLSService service;

    @Before
    public void setup() throws IOException {
        this.service = new DecisionTableXLSServiceImpl( ioService,
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
                //Do nothing; tests do not use a *real* XLS file
            }
        };

        when( authenticationService.getUser() ).thenReturn( user );
        when( user.getIdentifier() ).thenReturn( "user" );

        when( path.toURI() ).thenReturn( "default://p0/src/main/resources/dtable.xls" );
        when( inputstream.read( anyObject() ) ).thenReturn( -1 );
        when( ioService.newOutputStream( any( org.uberfire.java.nio.file.Path.class ),
                                         commentedOptionArgumentCaptor.capture() ) ).thenReturn( outputStream );
    }

    @Test
    public void testSessionInfoOnCreate() {
        service.create( path,
                        inputstream,
                        sessionId,
                        comment );

        final CommentedOption commentedOption = commentedOptionArgumentCaptor.getValue();
        assertNotNull( commentedOption );
        assertEquals( "user",
                      commentedOption.getName() );
        assertEquals( "123",
                      commentedOption.getSessionId() );
    }

    @Test
    public void testSessionInfoOnSave() {
        service.save( path,
                      inputstream,
                      sessionId,
                      comment );

        final CommentedOption commentedOption = commentedOptionArgumentCaptor.getValue();
        assertNotNull( commentedOption );
        assertEquals( "user",
                      commentedOption.getName() );
        assertEquals( "123",
                      commentedOption.getSessionId() );
    }

}
