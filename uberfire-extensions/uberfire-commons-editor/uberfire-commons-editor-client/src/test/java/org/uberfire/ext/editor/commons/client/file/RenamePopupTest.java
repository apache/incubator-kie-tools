/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.editor.commons.client.file;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
public class RenamePopupTest {

    private static final String NAME_TEXT = "new name";
    private static final String COMMENT_TEXT = "hello world";
    private static final String PATH = "dir/file.ext";
    //target file name should have the same extension as the file provided in the original path.
    private static final String TARGET_FILE_NAME = NAME_TEXT + ".ext";

    private Validator successValidator;
    private Validator failureValidator;

    @Mock
    private RenamePopupView view;
    @Mock
    private Path path;
    @Mock
    private CommandWithFileNameAndCommitMessage command;
    @Captor
    private ArgumentCaptor<FileNameAndCommitMessage> msgCaptor;

    @Before
    public void setUp() {

        // stub mocks
        when( path.getFileName() ).thenReturn( PATH );
        when( view.getCheckInComment() ).thenReturn( COMMENT_TEXT );
        when( view.getName() ).thenReturn( NAME_TEXT );

        // set up testing validators
        // it is easier to set up real objects than stubbing validate() method but we need to spy them
        // to verify validation was invoked
        successValidator = spy( new Validator() {
            @Override
            public void validate( String value, ValidatorCallback callback ) {
                callback.onSuccess();
            }
        } );
        failureValidator = spy( new Validator() {
            @Override
            public void validate( String value, ValidatorCallback callback ) {
                callback.onFailure();
            }
        } );
    }

    @Test
    public void testSuccessfulValidation() {
        // popup with successful validation
        RenamePopup popup = new RenamePopup( path, successValidator, command, view );

        // simulate submitting the popup
        popup.onRename();

        // validation with expected target file name was invoked
        verify( successValidator ).validate( eq( TARGET_FILE_NAME ), any( ValidatorCallback.class ) );
        // command was executed
        verify( command ).execute( msgCaptor.capture() );
        // check contents of the message passed to the command
        assertThat( msgCaptor.getValue().getNewFileName(), CoreMatchers.equalTo( NAME_TEXT ) );
        assertThat( msgCaptor.getValue().getCommitMessage(), CoreMatchers.equalTo( COMMENT_TEXT ) );
    }

    @Test
    public void testFailedValidation() {
        // popup with failing validation
        RenamePopup popup = new RenamePopup( path, failureValidator, command, view );

        // simulate submitting the popup
        popup.onRename();

        // validation with expected target file name was invoked
        verify( failureValidator ).validate( eq( TARGET_FILE_NAME ), any( ValidatorCallback.class ) );
        // verify command was NOT executed
        verify( command, never() ).execute( any( FileNameAndCommitMessage.class ) );
        // popup stays active so that user can correct the input
        verify( view, never() ).hide();
        // view handles the failure message
        verify( view ).handleInvalidFileName();
    }

    @Test
    public void testPopupCanceled() {
        // popup with successful validation
        RenamePopup popup = new RenamePopup( path, successValidator, command, view );

        // simulate cancelling the popup
        popup.onCancel();

        // validation was NOT invoked
        verify( successValidator, never() ).validate( anyString(), any( ValidatorCallback.class ) );
        // command was NOT executed
        verify( command, never() ).execute( any( FileNameAndCommitMessage.class ) );
        // dialog was hidden
        verify( view ).hide();
    }

    @Test
    public void testDefaultValidator() {
        // return some crazy values that shouldn't pass any validation
        when( view.getName() ).thenReturn( null );
        when( view.getCheckInComment() ).thenReturn( null );

        // popup with a default validator
        RenamePopup popup = new RenamePopup( path, command, view );

        // simulate submitting the popup
        popup.onRename();

        // command was executed
        verify( command ).execute( msgCaptor.capture() );
        // check contents of the message passed to the command
        assertThat( msgCaptor.getValue().getNewFileName(), CoreMatchers.nullValue() );
        assertThat( msgCaptor.getValue().getCommitMessage(), CoreMatchers.nullValue() );
    }
}
