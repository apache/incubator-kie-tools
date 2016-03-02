/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.validation.ValidationErrorReason;
import org.uberfire.ext.editor.commons.client.validation.Validator;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class CopyPopup implements CopyPopupView.Presenter {

    private final CopyPopupView view;
    private final Path path;
    private final Validator validator;
    private final CommandWithFileNameAndCommitMessage command;

    public CopyPopup( Path path,
                      CommandWithFileNameAndCommitMessage command ) {
        this( path, command, getDefaultView() );
    }

    public CopyPopup( Path path,
                      CommandWithFileNameAndCommitMessage command,
                      CopyPopupView view ) {
        this( path,
              new Validator() {
                  @Override
                  public void validate( final String value,
                                        final ValidatorCallback callback ) {
                      callback.onSuccess();
                  }
              },
              command, view );
    }

    public CopyPopup( Path path,
                      Validator validator,
                      CommandWithFileNameAndCommitMessage command ) {
        this( path, validator, command, getDefaultView() );
    }

    public CopyPopup( Path path,
                      Validator validator,
                      CommandWithFileNameAndCommitMessage command,
                      CopyPopupView view ) {
        this.validator = checkNotNull( "validator",
                                       validator );
        this.path = checkNotNull( "path",
                                  path );
        this.command = checkNotNull( "command",
                                     command );
        this.view = checkNotNull( "view",
                                  view );

        this.view.init( this );
    }

    public void show() {
        view.show();
    }

    private void hide() {
        view.hide();
    }

    @Override
    public void onCancel() {
        hide();
    }

    @Override
    public void onCopy() {
        final String baseFileName = view.getNewName();
        final String originalFileName = path.getFileName();
        final String extension = ( originalFileName.lastIndexOf( "." ) > 0
                ? originalFileName.substring( originalFileName.lastIndexOf( "." ) ) : "" );
        final String fileName = baseFileName + extension;

        validator.validate( fileName,
                            new ValidatorWithReasonCallback() {
                                @Override
                                public void onFailure( final String reason ) {
                                    if ( ValidationErrorReason.DUPLICATED_NAME.name().equals( reason ) ) {
                                        view.handleDuplicatedFileName();
                                    } else {
                                        view.handleInvalidFileName();
                                    }
                                }

                                @Override
                                public void onSuccess() {
                                    command.execute( new FileNameAndCommitMessage( baseFileName,
                                                                                   view.getCheckInComment() ) );
                                }

                                @Override
                                public void onFailure() {
                                    view.handleInvalidFileName();
                                }
                            } );
    }

    public static CopyPopupView getDefaultView() {
        return IOC.getBeanManager().lookupBean( CopyPopupView.class ).getInstance();
    }
}
