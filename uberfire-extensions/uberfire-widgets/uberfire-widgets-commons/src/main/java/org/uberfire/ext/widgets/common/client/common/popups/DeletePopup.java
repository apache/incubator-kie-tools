package org.uberfire.ext.widgets.common.client.common.popups;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.user.client.Window;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.ext.widgets.common.client.resources.CommonImages;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;


public class DeletePopup extends FormStylePopup {

    final private TextBox checkInCommentTextBox = new TextBox();

    public DeletePopup( final Command command ) {
        super( CommonConstants.INSTANCE.DeletePopupTitle() );

        checkNotNull("command",
                command);

        //Make sure it appears on top of other popups
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        final GenericModalFooter footer = new GenericModalFooter();
        footer.addButton( CommonConstants.INSTANCE.DeletePopupDelete(),
                new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        //if ( !Window.confirm("Confirm?") ) {
                        //    return;
                        //}
                        hide();
                        command.execute( );
                    }
                },
                IconType.REMOVE,
                ButtonType.PRIMARY );
        footer.addButton( CommonConstants.INSTANCE.Cancel(),
                new com.google.gwt.user.client.Command() {
                    @Override
                    public void execute() {
                        hide();
                    }
                },
                ButtonType.DEFAULT );
        add( footer );
    }

}
