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

package org.kie.workbench.common.services.backend.validation.asset;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;

public class Validator {

    //TODO internationalize error messages?.
    private final static String ERROR_CLASS_NOT_FOUND = "Definition of class \"{0}\" was not found. Consequentially validation cannot be performed.\n" +
            "Please check the necessary external dependencies for this project are configured correctly.";

    private final ValidatorFileSystemProvider validatorFileSystemProvider;

    protected final List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

    private final KieBuilder kieBuilder;

    public Validator( final ValidatorFileSystemProvider validatorFileSystemProvider ) {
        this.validatorFileSystemProvider = validatorFileSystemProvider;
        this.kieBuilder = makeKieBuilder();
    }

    protected KieBuilder makeKieBuilder() {
        return KieServices.Factory.get().newKieBuilder( validatorFileSystemProvider.getFileSystem() );
    }

    public List<ValidationMessage> validate() {
        validatorFileSystemProvider.write();

        runValidation();

        return validationMessages;
    }

    public KieBuilder getKieBuilder() {
        return kieBuilder;
    }

    private void runValidation() {

        try {

            final String destinationBasePath = getBasePath( validatorFileSystemProvider.getDestinationPath() );

            for ( final Message message : getBuildMessages() ) {
                addMessage( destinationBasePath,
                            message );
            }

        } catch ( NoClassDefFoundError e ) {
            validationMessages.add( makeErrorMessage( MessageFormat.format( ERROR_CLASS_NOT_FOUND,
                                                                            e.getLocalizedMessage() ) ) );
        } catch ( Throwable e ) {
            validationMessages.add( makeErrorMessage( e.getLocalizedMessage() ) );
        }
    }

    protected void addMessage( final String destinationBasePath,
                               final Message message ) {
        final String messageBasePath = getMessagePath( message );

        if ( messageBasePath == null ||
                "".equals( messageBasePath ) ||
                destinationBasePath.endsWith( messageBasePath ) ) {
            validationMessages.add( convertMessage( message ) );
        }
    }

    private List<Message> getBuildMessages() {
        return kieBuilder.buildAll().getResults().getMessages();
    }

    private String getMessagePath( final Message message ) {
        return message.getPath() != null ? getBasePath( message.getPath() ) : null;
    }

    /*
     * Strip the file extension as it cannot be relied upon when filtering KieBuilder messages.
     * For example we write MyGuidedTemplate.template to KieFileSystem but KieBuilder returns
     * Messages containing MyGuidedTemplate.drl
     */
    private String getBasePath( final String path ) {
        if ( path != null && path.contains( "." ) ) {
            return path.substring( 0,
                                   path.lastIndexOf( "." ) );
        }
        return path;
    }

    private ValidationMessage makeErrorMessage( final String msg ) {
        final ValidationMessage validationMessage = new ValidationMessage();
        validationMessage.setLevel( Level.ERROR );
        validationMessage.setText( msg );
        return validationMessage;
    }

    protected ValidationMessage convertMessage( final Message message ) {
        final ValidationMessage msg = new ValidationMessage();
        switch ( message.getLevel() ) {
            case ERROR:
                msg.setLevel( Level.ERROR );
                break;
            case WARNING:
                msg.setLevel( Level.WARNING );
                break;
            case INFO:
                msg.setLevel( Level.INFO );
                break;
        }

        msg.setId( message.getId() );
        msg.setLine( message.getLine() );
        msg.setColumn( message.getColumn() );
        msg.setText( message.getText() );

        return msg;
    }
}
