package org.uberfire.client.common.popups.footers;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ModalFooter;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.mvp.Command;

public class ModalFooterYesNoCancelButtons extends ModalFooter {

    private static ModalFooterYesNoCancelButtonsBinder uiBinder = GWT.create(ModalFooterYesNoCancelButtonsBinder.class);

    private final Command yesCommand;

    private final Command cancelCommand;

    private final Command noCommand;

    private final Modal panel;

    interface ModalFooterYesNoCancelButtonsBinder
            extends
            UiBinder<Widget, ModalFooterYesNoCancelButtons> {

    }

    @UiField
    Button yesButton;

    @UiField
    Button noButton;

    @UiField
    Button cancelButton;

    public ModalFooterYesNoCancelButtons( final Modal panel,
                                          final Command yesCommand,
                                          final String yesButtonText,
                                          final ButtonType yesButtonType,
                                          final IconType yesButtonIconType,
                                          final Command noCommand,
                                          final String noButtonText,
                                          final ButtonType noButtonType,
                                          final IconType noButtonIconType,
                                          final Command cancelCommand,
                                          final String cancelButtonText,
                                          final ButtonType cancelButtonType,
                                          final IconType cancelButtonIconType) {

        this.yesCommand = yesCommand;
        this.noCommand = noCommand;
        this.cancelCommand = cancelCommand;

        add( uiBinder.createAndBindUi( this ) );

        if (yesCommand == null) yesButton.setVisible(false);
        if (noCommand == null) noButton.setVisible(false);
        if (cancelCommand == null) cancelButton.setVisible(false);

        if (yesButtonType != null) yesButton.setType(yesButtonType);
        if (yesButtonText != null) yesButton.setText(yesButtonText);
        if (yesButtonIconType != null) yesButton.setIcon(yesButtonIconType);

        if (noButtonType != null) noButton.setType(noButtonType);
        if (noButtonText != null) noButton.setText(noButtonText);
        if (noButtonIconType != null) noButton.setIcon(noButtonIconType);

        if (cancelButtonType != null) cancelButton.setType(cancelButtonType);
        if (cancelButtonText != null) cancelButton.setText(cancelButtonText);
        if (cancelButtonIconType != null) cancelButton.setIcon(cancelButtonIconType);

        this.panel = panel;
    }

    public ModalFooterYesNoCancelButtons( final Modal panel,
                                          final Command yesCommand,
                                          final ButtonType yesButtonType,
                                          final Command noCommand,
                                          final ButtonType noButtonType,
                                          final Command cancelCommand,
                                          final ButtonType cancelButtonType) {
        this(panel, yesCommand, null, yesButtonType, null, noCommand, null, noButtonType, null, cancelCommand, null, cancelButtonType, null);
    }

    public ModalFooterYesNoCancelButtons( final Modal panel,
                                          final Command yesCommand,
                                          final Command noCommand,
                                          final Command cancelCommand) {
        this(panel, yesCommand, null, null, null, noCommand, null, null, null, cancelCommand, null, null, null);
    }

    @UiHandler("yesButton")
    public void onYesButtonClick( final ClickEvent e ) {
        if (yesCommand != null) yesCommand.execute();
        panel.hide();
    }

    @UiHandler("noButton")
    public void onNoButtonClick( final ClickEvent e ) {
        if (noCommand != null) noCommand.execute();
        panel.hide();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClick( final ClickEvent e ) {
        if (cancelCommand != null) cancelCommand.execute();
        panel.hide();
    }
}