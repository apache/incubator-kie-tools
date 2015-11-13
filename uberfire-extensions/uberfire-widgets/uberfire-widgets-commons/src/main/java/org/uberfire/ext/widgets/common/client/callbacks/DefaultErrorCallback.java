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
package org.uberfire.ext.widgets.common.client.callbacks;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.java.nio.IOException;

/**
 * Default Error handler for all Portable Exceptions
 */
public class DefaultErrorCallback implements ErrorCallback<Message> {

    @Override
    public boolean error( final Message message,
                          final Throwable throwable ) {
        try {
            throw throwable;

        } catch ( org.uberfire.java.nio.file.AccessDeniedException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.AtomicMoveNotSupportedException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.ClosedWatchServiceException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.DirectoryNotEmptyException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.FileAlreadyExistsException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionFileAlreadyExists0( e.getFile() ) );

        } catch ( org.uberfire.java.nio.file.FileSystemAlreadyExistsException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.FileSystemNotFoundException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.InvalidPathException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionInvalidPath() );

        } catch ( org.uberfire.java.nio.file.NoSuchFileException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionNoSuchFile0( e.getFile() ) );

        } catch ( org.uberfire.java.nio.file.NotDirectoryException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.NotLinkException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.PatternSyntaxException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.ProviderNotFoundException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.uberfire.java.nio.file.FileSystemException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( IOException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( Throwable e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        }
        return false;
    }

}
