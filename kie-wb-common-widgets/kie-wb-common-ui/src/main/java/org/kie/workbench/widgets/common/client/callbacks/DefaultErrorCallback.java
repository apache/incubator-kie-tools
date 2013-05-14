package org.kie.workbench.widgets.common.client.callbacks;

import org.jboss.errai.bus.client.api.ErrorCallback;
import org.jboss.errai.bus.client.api.Message;
import org.kie.workbench.widgets.common.client.popups.errors.ErrorPopup;
import org.kie.workbench.widgets.common.client.resources.i18n.CommonConstants;

/**
 * Default Error handler for all Portable Exceptions
 */
public class DefaultErrorCallback implements ErrorCallback {

    @Override
    public boolean error( final Message message,
                          final Throwable throwable ) {
        try {
            throw throwable;

        } catch ( org.kie.commons.java.nio.file.AccessDeniedException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.AtomicMoveNotSupportedException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.ClosedWatchServiceException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.DirectoryNotEmptyException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.FileAlreadyExistsException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionFileAlreadyExists0( e.getFile() ) );

        } catch ( org.kie.commons.java.nio.file.FileSystemAlreadyExistsException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.FileSystemNotFoundException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.InvalidPathException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionInvalidPath() );

        } catch ( org.kie.commons.java.nio.file.NoSuchFileException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionNoSuchFile0( e.getFile() ) );

        } catch ( org.kie.commons.java.nio.file.NotDirectoryException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.NotLinkException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.PatternSyntaxException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.ProviderNotFoundException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.file.FileSystemException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( org.kie.commons.java.nio.IOException e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        } catch ( Throwable e ) {
            ErrorPopup.showMessage( CommonConstants.INSTANCE.ExceptionGeneric0( e.getMessage() ) );

        }
        return false;
    }

}
