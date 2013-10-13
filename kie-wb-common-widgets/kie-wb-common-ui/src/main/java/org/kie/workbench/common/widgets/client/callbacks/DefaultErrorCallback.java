package org.kie.workbench.common.widgets.client.callbacks;

import org.guvnor.common.services.project.service.PackageAlreadyExistsException;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.common.popups.errors.ErrorPopup;
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

        } catch ( PackageAlreadyExistsException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionPackageAlreadyExists0( e.getFile() ) );

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
