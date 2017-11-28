package org.dashbuilder.client.widgets.common;

import org.dashbuilder.client.widgets.resources.i18n.DataSetExplorerConstants;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

/**
 * <p>Error Popup presenter for client runtime errors.</p>
 * @since 0.4.0
 */
@ApplicationScoped
@Specializes
public class ClientRuntimeErrorPopupPresenter extends ErrorPopupPresenter {

    @Inject
    public ClientRuntimeErrorPopupPresenter(View view) {
        super(view);
    }

    /**
     * Shows the given message in a modal popup that appears above all other workbench contents.
     * // TODO: Improve generated message by getting intermediate exception cause messages.
     * @param error The client runtime error.
     */
    public void showMessage( final ClientRuntimeError error) {
        StringBuilder result = new StringBuilder(DataSetExplorerConstants.INSTANCE.error());
        
        if (error != null) {
            final String message = error.getMessage();
            Throwable t = error.getRootCause();
            String localizedMessage = t != null ? t.getLocalizedMessage() : null;
            result = new StringBuilder(message);
            if (localizedMessage != null) {
                result.append(" ").append(DataSetExplorerConstants.INSTANCE.cause()).
                        append(": ").append(localizedMessage);
            }
        }
        
        showMessage(result.toString());
    }
    
}
